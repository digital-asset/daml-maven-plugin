/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.digitalasset.damlmavenplugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;

@Mojo(name = "damlcompile", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class Compile extends MojoBase {

    @Parameter(defaultValue = "${project.build.directory}/${project.artifactId}.dar")
    String darName;

    public void execute() throws MojoExecutionException, MojoFailureException {
        ProcessBuilder pb =
                new ProcessBuilder(Commands.DAML, "build", "-o", darName).redirectErrorStream(true);
        getLog().info("Running DAML command: " + String.join(" ", pb.command()));
        try {
            Process daml = pb.start();
            redirectOutput(daml.getInputStream());
            int status = daml.waitFor();
            if (status != 0) {
                throw new MojoFailureException("daml compilation failed with exit status " + status);
            }
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Cannot start `daml` compiler. Make sure that the SDK is installed", e);
        }
    }

}
