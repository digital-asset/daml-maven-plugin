/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.daml.extensions.damlmavenplugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.plugin.MojoFailureException;
import org.yaml.snakeyaml.Yaml;

public class DamlProject {

    private static String DAML_YAML_NAME = "daml.yaml";

    private final Path damlYaml;
    private final Map<String, Object> project;

    public static DamlProject create(Path damlYaml) throws MojoFailureException {
        Yaml yaml = new Yaml();
        try (InputStream is = Files.newInputStream(damlYaml)) {
            return new DamlProject(damlYaml, yaml.load(is));
        } catch (IOException ioe) {
            throw new MojoFailureException("Error reading daml.yaml", ioe);
        }
    }

    public static DamlProject create() throws MojoFailureException {
        Path cwd = Paths.get("").toAbsolutePath();
        while (cwd != null) {
            Path damlYaml = cwd.resolve(DAML_YAML_NAME);
            if (Files.exists(damlYaml)) {
                return create(damlYaml);
            }
            cwd = cwd.getParent();
        }
        throw new MojoFailureException("daml.yaml file not found");
    }

    private DamlProject(Path damlYaml, Map<String, Object> project) {
        this.damlYaml = damlYaml;
        this.project = project;
    }

    private Path projectRelative(String p) {
        return damlYaml.getParent().resolve(p);
    }

    public String getSdkVersion() {
        return (String) project.get("sdk-version");
    }

    @SuppressWarnings("unchecked")
    public List<String> getDataDependencies() {
        return Optional
            .ofNullable((List<String>) project.get("data-dependencies"))
            .orElse(Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    public List<String> getDependencies() {
        return Optional
            .ofNullable((List<String>) project.get("dependencies"))
            .orElse(Collections.emptyList());
    }

    public List<Path> getDependencyPaths() {
        return Stream
            .concat(getDependencies().stream(), getDataDependencies().stream())
            .map(this::projectRelative)
            .collect(Collectors.toList());
    }

}
