/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.daml.extensions.damlmavenplugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Test;


public class DamlProjectTest {

    @Test
    public void testDependencyPattern() throws IOException {
        DamlProject project = DamlProject.create(Paths.get("src/test/resources/daml-project-test/daml.yaml"));
        assertEquals(Arrays.asList("daml-prim", "daml-stdlib", "daml-script"), project.getDependencies());
        assertTrue(project.getDataDependencies().isEmpty());
    }
}
