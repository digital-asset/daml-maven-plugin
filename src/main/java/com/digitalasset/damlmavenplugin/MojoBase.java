package com.digitalasset.damlmavenplugin;

import org.apache.maven.plugin.AbstractMojo;

import java.io.InputStream;
import java.util.Scanner;

public abstract class MojoBase extends AbstractMojo {

    protected void redirectOutput(InputStream in) {
        Scanner scanner = new Scanner(in);
        while (scanner.hasNextLine()) {
            getLog().info(scanner.nextLine());
        }
    }

}
