/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.daml.extensions.damlmavenplugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class CompileTest {

    @Test(expected = MojoFailureException.class)
    public void noDaml() throws MojoExecutionException, MojoFailureException {
        Compile compile = new Compile();
        compile.darName = "target/test.dar";
        compile.execute();
    }

    @Test
    public void daml() throws Exception {
        File damlFile = createDamlFile();
        try {
            Compile compile = new Compile();
            compile.darName = "target/test.dar";
            compile.execute();

            File darFile = new File(compile.darName);
            assertTrue(darFile.exists());
        } finally {
            damlFile.delete();
        }
    }

    public File createDamlFile() throws IOException {
        File file = new File("daml.yaml");
        List<String> lines = Arrays.asList(
                "sdk-version: 1.0.0",
                "scenario: Test:test",
                "source: src/test/daml/Test.daml",
                "name: test",
                "version: 1.0.0",
                "parties:",
                "  party",
                "exposed-modules: []",
                "dependencies:",
                "  - daml-prim",
                "  - daml-stdlib"
        );
        Files.write(file.toPath(), lines, StandardOpenOption.CREATE_NEW);
        return file;
    }
}
