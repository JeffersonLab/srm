#!/bin/bash
#
# Adds SRM's roles and test users to the realm that the image's default scripts set up
# (01_base.sh logs kcadm.sh in, and creates test-realm and the srm client; 02_accounts.sh creates
# jdoe and the others, and the srm-user and srm-admin roles).
#
# kc-lib.sh's functions return non-zero, naming any missing variable, and never exit. This script
# tries every step and then exits non-zero if any failed, which the container's log shows.

# Located in root of container
. /kc-lib.sh

rc=0

# Creates user USERNAME, with the password KC_PASSWORD, and gives them the realm roles that follow.
# Arguments: USERNAME FIRSTNAME LASTNAME ROLE...
add_user() {
  KC_USERNAME=$1
  KC_FIRSTNAME=$2
  KC_LASTNAME=$3
  KC_EMAIL=$1@example.com
  shift 3
  create_user || rc=1
  for KC_ROLE_NAME in "$@"; do
    assign_role || rc=1
  done
}

echo "----------------"
echo "| Create Roles |"
echo "----------------"
for KC_ROLE_NAME in group1Leaders group2Leaders group3Leaders vtaadm; do
  create_role || rc=1
done

echo "----------------"
echo "| Create Users |"
echo "----------------"
KC_PASSWORD=password
add_user user1 James Johnson "${KC_RESOURCE}-user"
add_user user2 Robert Williams "${KC_RESOURCE}-user"
add_user user3 Michael Miller "${KC_RESOURCE}-user" group3Leaders vtaadm

echo "------------------------------------"
echo "| Allow Password Grant (Test Only) |"
echo "------------------------------------"
# Integration tests get tokens for test users such as jdoe with their passwords
"${KC_HOME}/bin/kcadm.sh" update "clients/${KC_CLIENT_NAME}" -r "${KC_REALM}" \
  -s directAccessGrantsEnabled=true || rc=1

if (( rc != 0 )); then
  echo "ERROR: ${0##*/}: a step failed; see above" >&2
fi
exit "${rc}"
