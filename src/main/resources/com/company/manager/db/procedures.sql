DROP PROCEDURE IF EXISTS wipe_tables_data;

DELIMITER //
CREATE PROCEDURE wipe_tables_data()
BEGIN
  SET FOREIGN_KEY_CHECKS=0;
  TRUNCATE TABLE offices;
  TRUNCATE TABLE employees;
  TRUNCATE TABLE clients;
  TRUNCATE TABLE providers;
  TRUNCATE TABLE providers_clients;
  SET FOREIGN_KEY_CHECKS=1;
END //
DELIMITER ;