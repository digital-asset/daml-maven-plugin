/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.daml.extensions.damlmavenplugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.yaml.snakeyaml.Yaml;

public class DamlProject {

    private final Path damlYaml;
    private final Map<String, Object> project;

    public static DamlProject create(Path damlYaml) throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream is = Files.newInputStream(damlYaml)) {
            return new DamlProject(damlYaml, yaml.load(is));
        }
    }

    private DamlProject(Path damlYaml, Map<String, Object> project) {
        this.damlYaml = damlYaml;
        this.project = project;
    }

    private Path projectRelative(String p) {
        return damlYaml.getParent().resolve(p);
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
