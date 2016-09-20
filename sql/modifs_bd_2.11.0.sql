--2.11
ALTER TABLE ville ALTER nom type varchar(64);
DROP INDEX vdx1;
CREATE UNIQUE INDEX ville_idx on ville(cdp,nom);
-- BACKUP
--COPY ville TO '/tmp/algem_ville_backup.csv' CSV DELIMITER ';';
DELETE FROM ville;
COPY ville FROM '/tmp/laposte_10-02-2016_tri1.csv' CSV HEADER DELIMITER ';';
