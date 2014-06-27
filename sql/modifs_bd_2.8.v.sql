CREATE TABLE categorie_salarie(
	id serial PRIMARY KEY,
	libelle varchar(128)
);
ALTER TABLE categorie_salarie OWNER TO nobody;

ALTER SEQUENCE categorie_salarie_id_seq MINVALUE 0;

INSERT INTO categorie_salarie VALUES (0,'Aucune');
INSERT INTO categorie_salarie VALUES (1,'Professeur');
INSERT INTO categorie_salarie VALUES (2,'Technicien');
INSERT INTO categorie_salarie VALUES (3,'Administratif');


INSERT INTO config VALUES('Studio.par.defaut','0');

CREATE TABLE salarie_type (
	idper integer REFERENCES salarie(idper) ON DELETE CASCADE,
	idcat integer DEFAULT 0 references categorie_salarie(id) ON DELETE SET DEFAULT,
	idx smallint DEFAULT 0,
PRIMARY KEY (idper,idcat)
);
ALTER TABLE salarie_type OWNER TO nobody;

UPDATE groupe SET nom = '0 (aucun)' WHERE id = 0;

-- CREATE OR REPLACE VIEW planningvue AS
-- SELECT pl.id, pl.jour, pl.debut, pl.fin, pl.action, p.id AS profid, p.prenom AS prenomprof, p.nom AS nomprof, s.id AS salleid, s.nom AS salle, c.id AS coursid, c.titre AS cours, c.ecole
--    FROM planning pl, personne p, salle s, cours c, action a
--   WHERE pl.ptype IN (1,5,6) AND pl.action = a.id AND a.cours = c.id AND pl.lieux = s.id AND pl.idper = p.id;

-- Configuration du nom de domaine par défaut
INSERT INTO config VALUES ('Organisation.domaine','');

ALTER TABLE categorie_tech RENAME TO categorie_studio;
ALTER TABLE categorie_studio OWNER TO nobody;

ALTER SEQUENCE categorie_tech_id_seq RENAME TO categorie_studio_id_seq;
--ALTER SEQUENCE categorie_studio_id_seq OWNER TO nobody;

DELETE FROM categorie_studio WHERE id = 6;
UPDATE categorie_studio SET nom = 'Maintenance' WHERE id = 5;
ALTER SEQUENCE categorie_studio_id_seq restart 6;
INSERT INTO categorie_studio VALUES (DEFAULT, 'Post-prod');

CREATE INDEX id_module_idx ON commande_cours (module);
CREATE INDEX action_cours_idx ON action (cours); 

-- -- -- -- complement final 2
DROP VIEW planningvue;
-- modification taille nom salle
ALTER TABLE salle ALTER nom type varchar(32);
ALTER INDEX slx2 RENAME TO salle_nom_idx; 

CREATE VIEW planningvue AS 
SELECT pl.id, pl.jour, pl.debut, pl.fin, pl.action, p.id AS profid, p.prenom AS prenomprof, p.nom AS nomprof, s.id AS salleid, s.nom AS salle, c.id AS coursid, c.titre AS cours, c.ecole
   FROM planning pl, personne p, salle s, cours c, action a
  WHERE (pl.ptype = ANY (ARRAY[1, 5, 6])) AND pl.action = a.id AND a.cours = c.id AND pl.lieux = s.id AND pl.idper = p.id;

-- nettoyage salle 0
DELETE FROM sallequip where idsalle = 0; 

