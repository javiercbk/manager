CREATE DATABASE IF NOT EXISTS manager_test CHARACTER SET utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS manager_test.offices(
	id BIGINT NOT NULL AUTO_INCREMENT,
	location VARCHAR(100) NOT NULL,
	creation TIMESTAMP NOT NULL,
	opened DATE NOT NULL,
	closed DATE,
	PRIMARY KEY (id),
	UNIQUE INDEX (location)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS manager_test.employees(
	id BIGINT NOT NULL AUTO_INCREMENT,
	name VARCHAR(80) NOT NULL,
	email VARCHAR(254) NOT NULL,
	phone VARCHAR(50),
	creation TIMESTAMP NOT NULL,
	working_since DATE NOT NULL,
	office_id BIGINT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (office_id) REFERENCES manager_test.offices(id),
	INDEX (name),
	UNIQUE INDEX (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS manager_test.clients(
	id BIGINT NOT NULL AUTO_INCREMENT,
	name VARCHAR(80) NOT NULL,
	email VARCHAR(254) NOT NULL,
	phone VARCHAR(50),
	creation TIMESTAMP NOT NULL,
	PRIMARY KEY (id),
	INDEX (name),
	UNIQUE INDEX (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS manager_test.providers(
	id BIGINT NOT NULL AUTO_INCREMENT,
	name VARCHAR(80) NOT NULL,
	creation TIMESTAMP NOT NULL,
	PRIMARY KEY (id),
	INDEX (name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS manager_test.providers_clients(
	client_id BIGINT NOT NULL,
	provider_id BIGINT NOT NULL,
	PRIMARY KEY (client_id, provider_id),
	FOREIGN KEY (client_id) REFERENCES manager_test.clients(id),
	FOREIGN KEY (provider_id) REFERENCES manager_test.providers(id)
) ENGINE=InnoDB;
