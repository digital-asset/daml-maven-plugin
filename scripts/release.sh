#!/usr/bin/env bash
#
# Copyright (c) 2019, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
# SPDX-License-Identifier: Apache-2.0
#

if [ $# -ne 5 ]
then
    echo "ERROR: No parameters given! Usage: ${0} maven_login maven_password signing_key gpg_passphrase sdk_version"
    exit 1
fi

if [ $(grep SNAPSHOT VERSION) ]
then
    echo "Not releasing snapshots"
    exit 0
fi

MAVEN_LOGIN="${1}"
MAVEN_PASSWORD="${2}"
GPG_SIGNING_KEY="${3}"
GPG_PASSPHRASE="${4}"
SDK_VERSION="${5}"
BASE_DIR="$(dirname "$(readlink -f "$0")")"

"${BASE_DIR}"/install-daml.sh "${SDK_VERSION}"

# Import a key
echo "${GPG_SIGNING_KEY}" | base64 -d &> my.key
gpg --import my.key &> gpg.out
# We need to get the id and cut the : from it
GPG_SIGNING_KEY_ID=$(grep 'gpg: key ' gpg.out | sort | head -1 | cut -f3 -d' ' | cut -f1 -d':')

# Export environment variables for Maven release process
export PATH=$PATH:~/.daml/bin
export MAVEN_LOGIN
export MAVEN_PASSWORD
export GPG_SIGNING_KEY_ID
export GPG_PASSPHRASE

PROJECT_VERSION="$(cat VERSION)"

mvn versions:set -DnewVersion="${PROJECT_VERSION}" -DgenerateBackupPoms=false
mvn clean package deploy -B --settings ./scripts/settings.xml
