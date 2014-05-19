-- -- postgresql < 8.4
-- CREATE AGGREGATE array_agg(anyelement) (
-- SFUNC=array_append,
-- STYPE=anyarray,
-- INITCOND='{}'
-- );
-- -- MISE EN SERVICE HORAIRES SALLES
-- 
-- Créer une table horaires
-- Des horaires d'ouverture et de fermeture identiques indiquent la fermeture de la salle pour un jour donné
-- Par défaut, la fermeture est représentée par 00:00:00  | 00:00:00
CREATE TABLE horaires (
	idsalle integer REFERENCES salle(id) ON DELETE CASCADE,
-- 0 = dimanche, 1 = lundi, etc.
	jour integer CONSTRAINT jour_semaine CHECK (jour >= 0 AND jour <= 6),
	ouverture time,
	fermeture time,
	CONSTRAINT horaires_pk PRIMARY KEY (idsalle,jour)
);

-- Gestion studio

CREATE TABLE categorie_tech(
	id serial PRIMARY KEY,
	nom varchar(64)
);

ALTER SEQUENCE categorie_tech_id_seq MINVALUE 0;

INSERT INTO categorie_tech VALUES (0,'Aucune');
INSERT INTO categorie_tech VALUES (1,'Enregistrement');
INSERT INTO categorie_tech VALUES (2,'Mixage');
INSERT INTO categorie_tech VALUES (3,'Sonorisation');
INSERT INTO categorie_tech VALUES (4,'Mastering');
INSERT INTO categorie_tech VALUES (5,'Installation');
INSERT INTO categorie_tech VALUES (6,'Maintenance');

CREATE TABLE technicien(
	idper integer PRIMARY KEY REFERENCES personne(id) ON DELETE CASCADE,
	specialite integer DEFAULT 0 NOT NULL REFERENCES categorie_tech(id) ON DELETE SET DEFAULT
);

-- -- Ajout code cours stage
ALTER SEQUENCE idmoduletype restart 12;
INSERT INTO module_type VALUES(DEFAULT, 'STG', 'Stage');

ALTER SEQUENCE groupe_id_seq MINVALUE 0;
INSERT INTO groupe VALUES (0,'Aucun',0,0,0,0,0);
ALTER TABLE echeancier2 ADD groupe integer REFERENCES groupe(id) ON DELETE SET DEFAULT DEFAULT 0;
