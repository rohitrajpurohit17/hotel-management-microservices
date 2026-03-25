#!/bin/bash
# This makes the script stop immediately if any command fails.
set -e

# This function creates one database when it does not already exist.
create_database() {
  # This stores the database name passed to the function.
  local database_name="$1"
  # This prints which database is currently being processed.
  echo "Checking database '${database_name}'"
  # This checks whether the database already exists in PostgreSQL.
  database_exists=$(psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -tAc "SELECT 1 FROM pg_database WHERE datname='${database_name}'")
  # This creates the database only when PostgreSQL did not return an existing match.
  if [ "$database_exists" != "1" ]; then
    # This prints a message so container logs clearly show database creation.
    echo "Creating database '${database_name}'"
    # This creates the requested database.
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "CREATE DATABASE \"${database_name}\";"
  fi
}

# This checks whether the caller provided a comma-separated database list.
if [ -n "$POSTGRES_MULTIPLE_DATABASES" ]; then
  # This prints the full list for easier troubleshooting.
  echo "Requested databases: $POSTGRES_MULTIPLE_DATABASES"
  # This changes the shell separator so the comma-separated list can be split correctly.
  IFS=',' read -ra database_array <<< "$POSTGRES_MULTIPLE_DATABASES"
  # This loops through every requested database name.
  for database_name in "${database_array[@]}"; do
    # This calls the helper function for each database in the list.
    create_database "$database_name"
  done
  # This prints a completion message after all databases are handled.
  echo "Database initialization completed"
fi
