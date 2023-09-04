/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.daml.extensions.damlmavenplugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.io.File;

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
        File damlFile = Utils.createDamlFile();
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
}
