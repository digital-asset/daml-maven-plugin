/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.daml.extensions.damlmavenplugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;


public class DamlProjectTest {

    private DamlProject getProject() throws MojoFailureException {
        return DamlProject.create(Paths.get("src/test/resources/daml-project-test/daml.yaml"));
    }
    @Test
    public void testDependencyPattern() throws MojoFailureException {
        DamlProject project = getProject();
        assertEquals(Arrays.asList("daml-prim", "daml-stdlib", "daml-script"), project.getDependencies());
        assertTrue(project.getDataDependencies().isEmpty());
    }

    @Test
    public void testSdkVersion() throws MojoFailureException {
        assertEquals("2.7.1", getProject().getSdkVersion());
    }
}
