# Maven plugin for DAML compilation and codegen

## Usage

In your DAML project define the usual `daml.yaml` and import the Java bindings in your `pom.xml`:
```
<dependency>
    <groupId>com.daml.ledger</groupId>
    <artifactId>bindings-rxjava</artifactId>
    <version>${daml-sdk.version}</version>
</dependency>
```

Then add this plugin:

```
<plugin>
    <groupId>com.digitalasset</groupId>
    <artifactId>daml-maven-plugin</artifactId>
    <version>${daml-plugin.version}</version>
    <executions>
        <execution>
            <id>daml-compile-codegen</id>
            <goals>
                <goal>damlcompile</goal>
                <goal>codegen</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

When using the `codegen` goal it will fetch the codegen version corresponding to the Java binding version and use it to 
generate the java sources.

The project also fails to build if the SDK version in your `daml.yaml` doesn't match the version of Java bindings in `pom.xml`.

Optional configuration elements:
* `darName`: the generated dar file. Default: `${project.build.directory}/${project.artifactId}.dar`.
* `decoderClass`: defines the `-d` option of the codegen. Default: `daml.Decorder`.
* `output`: the target directory where the classes are generated to. Default: `${project.build.directory}/generated-sources/main/java`.

By default these goals are bound to `generate-sources` phase.

## DAML doc gen

To generate damldocs at `verify` phase include this execution:
```
<execution>
    <id>daml-docs</id>
    <goals>
        <goal>docsgen</goal>
    </goals>
    <configuration>
        <docsOutput>damldocs.md</docsOutput>
        <combined>true</combined>
        <damlFiles>
            <param>src/main/daml/your_main_daml.daml</param>
        </damlFiles>
    </configuration>
</execution>
```

Configuration:
* `docsOutput`: the output parameter of the generator. Default: `damldocs`.
* `combined`: if true a single file is generated (`--combine` option of the generator). Default is false.
* `format`: the `--format` option of the generator. Default: `markdown`.

By default this goal is bound to `verify` phase.

CONFIDENTIAL © 2019 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
Any unauthorized use, duplication or distribution is strictly prohibited.


