/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.digitalasset.damlmavenplugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.transfer.artifact.DefaultArtifactCoordinate;
import org.apache.maven.shared.transfer.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.transfer.artifact.resolve.ArtifactResolverException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Mojo(name = "codegen", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class CodeGen extends MojoBase {

    @Parameter(defaultValue = "${project.build.directory}/${project.artifactId}.dar")
    private String darName;

    @Parameter(defaultValue = "daml.Decorder")
    private String decoderClass;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/main/java")
    private String output;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
    private List<ArtifactRepository> remoteRepositories;

    @Component
    private ArtifactResolver artifactResolver;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String[] args = new String[]{
                "-d",
                decoderClass,
                "-o",
                output,
                darName
        };

        getLog().info("Checking if java bindings version matches `daml.yaml`");
        String sdkVersion = getDamlVersion();
        String bindingsVersion = findJavaBindingsVersion();
        if (!sdkVersion.equals(bindingsVersion)) {
            throw new MojoFailureException("SDK version in `daml.yaml` does not match java bindings version. " +
                    sdkVersion + " != " + bindingsVersion);
        }

        DefaultArtifactCoordinate coordinate = new DefaultArtifactCoordinate();
        coordinate.setGroupId("com.daml");
        coordinate.setArtifactId("codegen-java");
        coordinate.setVersion(sdkVersion);

        ProjectBuildingRequest buildingRequest =
                new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
        buildingRequest.setRemoteRepositories(remoteRepositories);

        try {
            getLog().info("Resolving codegen");
            Artifact artifact = artifactResolver.resolveArtifact(buildingRequest, coordinate).getArtifact();
            getLog().info("Resolved to " + artifact.getFile());

            // creating a new classLoader so that the dependencies won't clash with the dependencies of the host project
            String mainClass = "com.digitalasset.daml.lf.codegen.Main";
            URL codeGenDepURL = artifact.getFile().toPath().toUri().toURL();
            ClassLoader classLoader = getClassLoader(codeGenDepURL);

            Class<?> bootClass = classLoader.loadClass(mainClass);
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            MethodHandle mainHandle = lookup.findStatic(bootClass, "main",
                    MethodType.methodType(void.class, String[].class));

            mainHandle.invoke(args);
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
            throw new MojoExecutionException("Cannot prepare codegen dependency", e);
        } catch (ArtifactResolverException e) {
            throw new MojoExecutionException("Cannot resolve codegen dependency", e);
        } catch (Throwable e) {
            throw new MojoFailureException("Codegen failed", e);
        }

        getLog().info("Registering compile source root " + output);
        project.addCompileSourceRoot(output);
    }

    private static ClassLoader getClassLoader(URL cp) {
        return new URLClassLoader(new URL[] { cp });
    }

    private String findJavaBindingsVersion() throws MojoFailureException {
        Optional<Artifact> codegenOpt = project
                .getDependencyArtifacts()
                .stream()
                .filter(a -> ((Artifact)a).getArtifactId().equals("bindings-rxjava"))
                .findFirst();
        if (codegenOpt.isPresent()) {
            return codegenOpt.get().getVersion();
        } else {
            throw new MojoFailureException("Cannot find `bindings-rxjava` among the dependencies");
        }
    }

    private static String getDamlVersion() throws MojoFailureException {
        String errorMsg =
                "Cannot determine project sdk version. Make sure that `daml.yaml` includes a line specifying `sdk-version`.";
        try (Scanner scanner = new Scanner(Paths.get("daml.yaml"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("sdk-version:")) {
                    return line.substring(12).trim();
                }
            }
        } catch (IOException e) {
            throw new MojoFailureException(errorMsg, e);
        }
        throw new MojoFailureException(errorMsg);
    }
}
