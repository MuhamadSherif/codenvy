#!/bin/bash
#
# CODENVY CONFIDENTIAL
# ________________
#
# [2012] - [2015] Codenvy, S.A.
# All Rights Reserved.
# NOTICE: All information contained herein is, and remains
# the property of Codenvy S.A. and its suppliers,
# if any. The intellectual and technical concepts contained
# herein are proprietary to Codenvy S.A.
# and its suppliers and may be covered by U.S. and Foreign Patents,
# patents in process, and are protected by trade secret or copyright law.
# Dissemination of this information or reproduction of this material
# is strictly forbidden unless prior written permission is obtained
# from Codenvy S.A..
#

# load lib.sh from path stored in parameter 1
. $1

printAndLog "TEST CASE: Login"

vagrantUp ${SINGLE_NODE_VAGRANT_FILE}

installImCliClient
validateInstalledImCliClientVersion

auth "prodadmin" "CodenvyAdmin" "${SAAS_SERVER}"

UUID_OWNER=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 4 | head -n 1)
UUID_MEMBER=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 4 | head -n 1)
PASSWORD="pwd123ABC"

# create account
doPost "application/json" "{\"name\":\"account-${UUID_OWNER}\"}" "${SAAS_SERVER}/api/account?token=${TOKEN}"
TMP=${OUTPUT}
fetchJsonParameter "id"
ACCOUNT_ID=${OUTPUT}

OUTPUT=${TMP}
fetchJsonParameter "name"
ACCOUNT_NAME=${OUTPUT}

# add user with [account/owner] role
doPost "application/json" "{\"name\":\"${UUID_OWNER}@codenvy.com\",\"password\":\"${PASSWORD}\"}" "${SAAS_SERVER}/api/user/create?token=${TOKEN}"
fetchJsonParameter "id"
USER_OWNER_ID=${OUTPUT}
doPost "application/json" "{\"userId\":\"${USER_OWNER_ID}\",\"roles\":[\"account/owner\"]}" "${SAAS_SERVER}/api/account/${ACCOUNT_ID}/members?token=${TOKEN}"

# add user with [account/member] role
doPost "application/json" "{\"name\":\"${UUID_MEMBER}@codenvy.com\",\"password\":\"${PASSWORD}\"}" "${SAAS_SERVER}/api/user/create?token=${TOKEN}"
fetchJsonParameter "id"
USER_MEMBER_ID=${OUTPUT}
doPost "application/json" "{\"userId\":\"${USER_MEMBER_ID}\",\"roles\":[\"account/member\"]}" "${SAAS_SERVER}/api/account/${ACCOUNT_ID}/members?token=${TOKEN}"

# login with username and password
executeIMCommand "login" "${UUID_OWNER}@codenvy.com" "${PASSWORD}"
validateExpectedString ".*Login.success.*"

# login with wrong password
executeIMCommand "--valid-exit-code=1" "login" "${UUID_OWNER}@codenvy.com" "wrong_password"
validateExpectedString ".*Unable.to.authenticate.for.the.given.credentials.on.URL.'${SAAS_SERVER}'\..Check.the.username.and.password\..*Login.failed.on.remote.'saas-server'\..*"

# login with wrong username
executeIMCommand "--valid-exit-code=1" "login" "wrong_username" "${PASSWORD}"
validateExpectedString ".*Unable.to.authenticate.for.the.given.credentials.on.URL.'${SAAS_SERVER}'\..Check.the.username.and.password\..*Login.failed.on.remote.'saas-server'\..*"

printAndLog "RESULT: PASSED"

vagrantDestroy
