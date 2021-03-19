#!/bin/bash

set -e
set -u

function create_user_and_schema() {
	local schema=$1
	local user=$2
	local password=$3

	echo "  Creating user and schema '$schema'"
	psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
	    CREATE USER $user WITH ENCRYPTED PASSWORD '$password';
	    CREATE SCHEMA IF NOT EXISTS $schema AUTHORIZATION $user;
EOSQL
}

if [ -n "$POSTGRES_ADDITIONAL_SCHEMA" ]; then
	echo "Additional schemas creation requested: $POSTGRES_ADDITIONAL_SCHEMA"
	for db in $(echo "$POSTGRES_ADDITIONAL_SCHEMA" | tr ',' ' '); do
		create_user_and_schema "$db" "$db" "$POSTGRES_PASSWORD"
	done
	echo "Additional schemas created"
fi
