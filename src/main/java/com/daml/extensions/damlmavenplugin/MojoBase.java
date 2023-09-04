/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.daml.extensions.damlmavenplugin;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

public abstract class MojoBase extends AbstractMojo {

    protected void redirectOutput(InputStream in) {
        try (Scanner scanner = new Scanner(in)) {
            while (scanner.hasNextLine()) {
                getLog().info(scanner.nextLine());
            }
        }
    }

    protected Path getDamlYamlFile() throws MojoFailureException {
        Path cwd = Paths.get("").toAbsolutePath();
        while (cwd != null) {
            Path damlYaml = cwd.resolve("daml.yaml");
            if (Files.exists(damlYaml)) {
                return damlYaml;
            }
            cwd = cwd.getParent();
        }
        throw new MojoFailureException("daml.yaml file not found");
    }

}
