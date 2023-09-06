/**
 * Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.daml.extensions.damlmavenplugin;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.WordUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

public abstract class DependencyUtils {

    static Pattern dependencyPattern = Pattern.compile("^((contingent-claims|daml-finance)-[-\\w]+)-([.\\d]+).dar$");

    public static DependencyUtils defaultInstance(Log log) {
        return new DependencyUtils() {
            @Override
            protected void downloadDependency(URL url, Path target) throws IOException {
                log.info(String.format("Downloading daml dependency %s -> %s", url, target));
                FileUtils.copyURLToFile(url, target.toFile());
            }
        };
    }

    public void downloadDependencyDars(List<Path> dependencies) throws MojoExecutionException {
        try {
            for(Path dep : dependencies) {
                processDependency(dep);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error download daml dependency", e);
        }
    }

    private void processDependency(Path dep) throws IOException {
        if (Files.exists(dep))
            return;

        String filename = dep.getFileName().toString();
        Matcher matcher = dependencyPattern.matcher(filename);
        if (matcher.matches()) {
            String packageName = matcher.group(1);
            String version = matcher.group(3);
            String moduleName = WordUtils
                .capitalize(packageName.replaceAll("-", "."), '.')
                .replaceFirst("Contingent.Claims", "ContingentClaims"); // artifact names are not consistent :(
            String path = Paths.get(
                    "/digital-asset/daml-finance/releases/download",
                    moduleName, version,
                    String.format("/%s-%s.dar", packageName, version))
                    .toString();
            URL dependencyUrl = new URL("https", "github.com", path);

            downloadDependency(dependencyUrl, dep);
        }
    }

    // moduleName: ContingentClaims.Core
    // packageName: contingent-claims-core
    // https://github.com/digital-asset/daml-finance/releases/download/${module_name}/${version}/${package_name}-${version}.dar"
    protected abstract void downloadDependency(URL url, Path target) throws IOException;
}
