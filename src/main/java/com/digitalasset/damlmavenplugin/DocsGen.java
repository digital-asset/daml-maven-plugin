/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.digitalasset.damlmavenplugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mojo(name = "docsgen", defaultPhase = LifecyclePhase.VERIFY)
public class DocsGen extends MojoBase {
    @Parameter(defaultValue = "damldocs")
    String docsOutput;

    @Parameter(defaultValue = "false")
    boolean combined;

    @Parameter(defaultValue = "markdown")
    String format;

    @Parameter
    List<String> damlFiles;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<String> args = new ArrayList(Arrays.asList("daml", "damlc", "docs", "-o", docsOutput, "-f", format));
        if (combined) {
            args.add("--combine");
        }
        args.addAll(damlFiles);
        ProcessBuilder pb =
                new ProcessBuilder(args).redirectErrorStream(true);
        try {
            Process daml = pb.start();
            redirectOutput(daml.getInputStream());
            int status = daml.waitFor();
            if (status != 0) {
                throw new MojoFailureException("daml compilation failed with exit status " + status);
            }
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Cannot start `daml` compiler. Make sure that the SDK is installed.", e);
        }
    }
}
