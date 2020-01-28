/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.digitalasset.damlmavenplugin;

public class Commands {
    static final String DAML = isWindows() ? "daml.cmd" : "daml";

    private static boolean isWindows() {
        String osName = System.getProperty("os.name");
        return osName.startsWith("Windows");
    }
}
