/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.daml.extensions.damlmavenplugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoFailureException;

public class Utils {

    public static File createDamlFile() throws MojoFailureException {
        File file = new File("daml.yaml");
        String sdkVersion = System.getProperty("daml.sdk.version");
        if (StringUtils.isEmpty(sdkVersion)) {
            throw new MojoFailureException("System property daml.sdk.version is unspecified");
        }

        List<String> lines = Arrays.asList(
                "sdk-version: " + sdkVersion,
                "source: src/test/daml/Test.daml",
                "name: test",
                "version: 1.0.0",
                "parties:",
                "  party",
                "dependencies:",
                "  - daml-prim",
                "  - daml-stdlib",
                "  - daml-script");
        try {
            Files.write(file.toPath(),
                    lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new MojoFailureException("Error writing daml.yaml", e);
        }
        return file;
    }

}
