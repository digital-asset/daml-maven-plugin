# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.1]

### Fixex

- Fixed an issue with artifact download, which was not working on windows, because a wrong download URL was generated.

## [0.2.0]

### Fixed

- Finding daml.yaml in multi-module maven project.
- Don't download dependencies if they exist.

### Added

- Added `damlProjectDirectory` setting to the plugin if the daml project is not the same as the maven project directory.

## [0.1.6]

### Changed

- Updated bulid dependencies
- Updated code and tests to work with the latest daml versions (2.7.x)
- Download daml finance dependencies. If a project depends on daml finance lib in their daml.yaml file, the daml compile target will download these dependencies to the specified location.
- The daml.yaml file can be in a parent directory relative to the pom.xml of the daml project, the plugin will find it.
