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

public class Utils{

    public static File createDamlFile() throws IOException {
        File file = new File("daml.yaml");
        List<String> lines = Arrays.asList(
                "sdk-version: 2.7.1",
                "scenario: Test:test",
                "source: src/test/daml/Test.daml",
                "name: test",
                "version: 1.0.0",
                "parties:",
                "  party",
                "dependencies:",
                "  - daml-prim",
                "  - daml-stdlib",
                "  - daml-script"
        );
        Files.write(file.toPath(),
                    lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        return file;
    }

}
