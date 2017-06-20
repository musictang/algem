-- -- 2.14.0
INSERT INTO config VALUES('Code.langue','fr-FR');

ALTER TABLE echeancier2 ADD tva numeric(4,2) DEFAULT 0.0;
--INSERT INTO comptepref VALUES ('TVA',NULL,NULL);

ALTER TABLE tva ADD compte integer;
ALTER TABLE tva ALTER pourcentage TYPE numeric(4,2);

INSERT INTO compte VALUES(DEFAULT,'445711','TVA collectée 2,1%',false);
INSERT INTO compte VALUES(DEFAULT,'445712','TVA collectée 5,5%',false);
INSERT INTO compte VALUES(DEFAULT,'445713','TVA collectée 7%',false);
INSERT INTO compte VALUES(DEFAULT,'445714','TVA collectée 19,6%',false);
INSERT INTO compte VALUES(DEFAULT,'445715','TVA collectée 10%',false);

ALTER TABLE sallequip ADD visible boolean default TRUE;
ALTER TABLE sallequip ADD immo varchar(32);
CREATE INDEX sallequip_idx ON sallequip (idsalle);

INSERT INTO menu2 VALUES (151,'OrderLine.date.before.financial.year.auth');

INSERT INTO menuprofil VALUES(151,0,false);
INSERT INTO menuprofil VALUES(151,1,false);
INSERT INTO menuprofil VALUES(151,2,false);
INSERT INTO menuprofil VALUES(151,3,false);
INSERT INTO menuprofil VALUES(151,4,true);
INSERT INTO menuprofil VALUES(151,10,false);
INSERT INTO menuprofil VALUES(151,11,false);

INSERT INTO menuaccess (select idper, 151, false from login where profil = 0);
INSERT INTO menuaccess (select idper, 151, false from login where profil = 1);
INSERT INTO menuaccess (select idper, 151, false from login where profil = 2);
INSERT INTO menuaccess (select idper, 151, false from login where profil = 3);
INSERT INTO menuaccess (select idper, 151, true from login where profil = 4);
INSERT INTO menuaccess (select idper, 151, false from login where profil = 10);
INSERT INTO menuaccess (select idper, 151, false from login where profil = 11);



-- CREATE FUNCTION is_in_year()
-- RETURNS TRIGGER AS $$
-- declare
    -- debut date := (select valeur::date from public.config where clef = 'Date.DebutAnnee');
    -- now date := (select CURRENT_DATE);
-- BEGIN
  -- IF debut >= CURRENT_DATE THEN
    -- RAISE NOTICE 'annee en cours %, %', now, debut;
    -- ELSE
    -- RAISE NOTICE 'avant annee %, %', now, debut;
  -- END IF;
  -- return null;
-- END$$ LANGUAGE 'plpgsql';

-- CREATE TRIGGER inc_numero_piece
-- AFTER UPDATE ON config
-- FOR EACH STATEMENT
-- EXECUTE PROCEDURE is_in_year();
-- create table histo_echeance (
-- id echeance serial,
-- oid        | integer               | not null default nextval('echeancier2_oid_seq'::regclass)
 -- echeance   | date                  | 
 -- payeur     | integer               | 
 -- adherent   | integer               | 
 -- commande   | integer               | 
 -- reglement  | character(4)          | 
 -- libelle    | character varying(50) | 
 -- montant    | integer               | 
 -- piece      | character varying(10) | 
 -- ecole      | integer               | 
 -- compte     | integer               | 
 -- paye       | boolean               | 
 -- transfert  | boolean               | 
 -- monnaie    | character(4)          | 
 -- analytique | character varying(13) | 
 -- facture    | character varying(10) | default NULL::character varying
 -- groupe     | integer               | default 0
 -- tva        | numeric(4,2)          | d
 
 -- id serial,
 -- date_mod timestamp


