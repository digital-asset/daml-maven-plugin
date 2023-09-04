/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.daml.extensions.damlmavenplugin;

import java.io.InputStream;
import java.util.Scanner;

import org.apache.maven.plugin.AbstractMojo;

public abstract class MojoBase extends AbstractMojo {

    protected void redirectOutput(InputStream in) {
        try (Scanner scanner = new Scanner(in)) {
            while (scanner.hasNextLine()) {
                getLog().info(scanner.nextLine());
            }
        }
    }
}
