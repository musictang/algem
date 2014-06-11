CREATE TABLE categorie_salarie(
	id serial PRIMARY KEY,
	libelle varchar(128)
);
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

UPDATE groupe SET nom = '0 (aucun)' WHERE id = 0;

CREATE OR REPLACE VIEW planningvue AS
SELECT pl.id, pl.jour, pl.debut, pl.fin, pl.action, p.id AS profid, p.prenom AS prenomprof, p.nom AS nomprof, s.id AS salleid, s.nom AS salle, c.id AS coursid, c.titre AS cours, c.ecole
   FROM planning pl, personne p, salle s, cours c, action a
  WHERE pl.ptype IN (1,5,6) AND pl.action = a.id AND a.cours = c.id AND pl.lieux = s.id AND pl.idper = p.id;
  
