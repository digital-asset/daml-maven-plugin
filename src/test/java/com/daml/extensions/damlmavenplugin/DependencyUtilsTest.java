/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.daml.extensions.damlmavenplugin;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

public class DependencyUtilsTest {

    static class TestDependencyUtils extends DependencyUtils {
        Map<String, URL> deps = new HashMap<>();

        TestDependencyUtils() {
            try {
                deps.put("contingent-claims-core-2.0.0.dar", new URL(
                        "https://github.com/digital-asset/daml-finance/releases/download/ContingentClaims.Core/2.0.0/contingent-claims-core-2.0.0.dar"));
                deps.put("contingent-claims-lifecycle-2.0.0.dar", new URL(
                        "https://github.com/digital-asset/daml-finance/releases/download/ContingentClaims.Lifecycle/2.0.0/contingent-claims-lifecycle-2.0.0.dar"));
                deps.put("daml-finance-account-2.0.0.dar", new URL(
                        "https://github.com/digital-asset/daml-finance/releases/download/Daml.Finance.Account/2.0.0/daml-finance-account-2.0.0.dar"));
                deps.put("daml-finance-interface-account-2.0.0.dar", new URL(
                        "https://github.com/digital-asset/daml-finance/releases/download/Daml.Finance.Interface.Account/2.0.0/daml-finance-interface-account-2.0.0.dar"));
                deps.put("daml-finance-util-3.0.0.dar", new URL(
                        "https://github.com/digital-asset/daml-finance/releases/download/Daml.Finance.Util/3.0.0/daml-finance-util-3.0.0.dar"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void downloadDependency(URL url, Path target) throws IOException {
            assertEquals(deps.get(target.getFileName().toString()), url);
        }
    }

    @Test
    public void testDependencyPattern() throws MojoExecutionException {
        Path base = Paths.get(".daml/libs");
        TestDependencyUtils sut = new TestDependencyUtils();

        sut.downloadDependencyDars(
                Arrays.asList(
                        "daml-stdlib",
                        "daml-prim",
                        "daml-stdlib",
                        ".daml/libs/contingent-claims-core-2.0.0.dar",
                        ".daml/libs/contingent-claims-lifecycle-2.0.0.dar",
                        ".daml/libs/daml-finance-account-2.0.0.dar",
                        ".daml/libs/daml-finance-interface-account-2.0.0.dar",
                        ".daml/libs/daml-finance-util-3.0.0.dar",
                        ".daml/libs/my-project-data.dar")
                        .stream()
                        .map(base::resolve)
                        .collect(Collectors.toList()));
    }
}
