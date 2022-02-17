CREATE USER spring_boot_api WITH PASSWORD 'password';
CREATE DATABASE spring_boot_api;
GRANT ALL PRIVILEGES ON DATABASE spring_boot_api TO spring_boot_api;

CREATE USER keycloak WITH PASSWORD 'password';
CREATE DATABASE keycloak;
GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;