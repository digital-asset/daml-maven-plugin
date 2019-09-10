package com.digitalasset.damlmavenplugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class DocsGenTest {

    @Test
    public void generateDocs() throws MojoFailureException, MojoExecutionException {
        DocsGen docsGen = new DocsGen();
        docsGen.damlFiles = Arrays.asList("src/test/daml/Test.daml");
        docsGen.format = "markdown";
        docsGen.docsOutput = "target/docs.md";
        docsGen.execute();
        File file = new File(docsGen.docsOutput);
        assertTrue(file.exists());
    }
}
