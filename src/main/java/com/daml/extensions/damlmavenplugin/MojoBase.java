/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.daml.extensions.damlmavenplugin;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class MojoBase extends AbstractMojo {

    @Parameter(defaultValue = "${project.basedir}")
    protected File damlProjectDirectory;

    protected void redirectOutput(InputStream in) {
        try (Scanner scanner = new Scanner(in)) {
            while (scanner.hasNextLine()) {
                getLog().info(scanner.nextLine());
            }
        }
    }

    public File getDamlProjectDirectory() {
        if (damlProjectDirectory == null) {
            return Paths.get("").toFile();
        } else
            return damlProjectDirectory;
    }

    protected DamlProject createDamlProject() throws MojoFailureException {
        return DamlProject.create();
    }
}
