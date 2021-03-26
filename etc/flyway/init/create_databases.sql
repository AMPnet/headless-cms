DROP DATABASE IF EXISTS headless_cms_service;
CREATE DATABASE headless_cms_service ENCODING 'UTF-8';

DROP DATABASE IF EXISTS headless_cms_service_test;
CREATE DATABASE headless_cms_service_test ENCODING 'UTF-8';

DROP USER IF EXISTS headless_cms_service;
CREATE USER headless_cms_service WITH PASSWORD 'password';

DROP USER IF EXISTS headless_cms_service_test;
CREATE USER headless_cms_service_test WITH PASSWORD 'password';
