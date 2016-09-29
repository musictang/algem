--2.11
ALTER TABLE ville ALTER nom type varchar(64);
DROP INDEX vdx1;
CREATE UNIQUE INDEX ville_idx on ville(cdp,nom);
-- BACKUP
--COPY ville TO '/tmp/algem_ville_backup.csv' CSV DELIMITER ';';
DELETE FROM ville;
COPY ville FROM '/tmp/laposte_10-02-2016_tri1.csv' CSV HEADER DELIMITER ';';


CREATE TABLE etablissement(
    id INTEGER REFERENCES personne(id) ON DELETE CASCADE,
    idper integer REFERENCES personne(id) ON DELETE CASCADE,
    actif boolean DEFAULT true,
    PRIMARY KEY (id,idper)
);
ALTER TABLE etablissement OWNER TO nobody;
INSERT INTO etablissement SELECT p.id,l.idper,true FROM personne p,login l WHERE p.ptype = 5 AND l.profil IN (1,2,4);

--INSERT INTO config VALUES('Activation.etablissement.type', '0');
INSERT INTO config VALUES('Scripts.path',E'C:\\Algem\\Scripts');
CREATE RULE protect_first_entry_suivi AS ON DELETE TO suivi WHERE old.id = 0 DO INSTEAD NOTHING;
