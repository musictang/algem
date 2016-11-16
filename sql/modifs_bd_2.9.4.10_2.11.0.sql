-- 2.9.4.10
ALTER TABLE menuprofil ADD CONSTRAINT menuprofil_pk PRIMARY KEY(idmenu,profil);
ALTER TABLE menuaccess ADD CONSTRAINT menuaccess_pk PRIMARY KEY(idper,idmenu);
-- 2.9.4.13
CREATE TABLE module_preset(
  id serial,
  nom varchar(128),
  modules int[]
);
ALTER TABLE module_preset OWNER TO nobody;
-- 2.9.4.14
-- Algem web app prerequisites
ALTER TABLE login ALTER login TYPE VARCHAR(16);
ALTER TABLE login ADD CONSTRAINT login_unique UNIQUE (login);

-- TODO vérifier doublons au préalable (verifier menuaccess_pk)
-- DELETE FROM menuaccess where oid IN (SELECT m2.oid from menuaccess m1, menuaccess m2 where m1.idper = m2.idper and m1.idmenu = m2.idmenu and m2.oid > m1.oid);
--ALTER TABLE menuaccess ADD CONSTRAINT menuaccess_pk PRIMARY KEY (idper,idmenu); 2.9.4.10

-- photo management
CREATE TABLE personne_photo (
    idper integer primary key,
    photo bytea
);

ALTER TABLE personne_photo OWNER TO nobody;

INSERT INTO menu2 VALUES(146,'Photos.export.auth');
INSERT INTO menu2 VALUES(147,'Photos.import.auth');

INSERT INTO menuprofil VALUES(146,0,false);
INSERT INTO menuprofil VALUES(146,1,false);
INSERT INTO menuprofil VALUES(146,2,false);
INSERT INTO menuprofil VALUES(146,3,false);
INSERT INTO menuprofil VALUES(146,4,true);

INSERT INTO menuprofil VALUES(147,0,false);
INSERT INTO menuprofil VALUES(147,1,false);
INSERT INTO menuprofil VALUES(147,2,false);
INSERT INTO menuprofil VALUES(147,3,false);
INSERT INTO menuprofil VALUES(147,4,true);

INSERT INTO menuaccess (select idper, 146, false from login where profil = 0);
INSERT INTO menuaccess (select idper, 146, false from login where profil = 1);
INSERT INTO menuaccess (select idper, 146, false from login where profil = 2);
INSERT INTO menuaccess (select idper, 146, false from login where profil = 3);
INSERT INTO menuaccess (select idper, 146, true from login where profil = 4);

INSERT INTO menuaccess (select idper, 147, false from login where profil = 0);
INSERT INTO menuaccess (select idper, 147, false from login where profil = 1);
INSERT INTO menuaccess (select idper, 147, false from login where profil = 2);
INSERT INTO menuaccess (select idper, 147, false from login where profil = 3);
INSERT INTO menuaccess (select idper, 147, true from login where profil = 4);
-- 2.9.5.0
CREATE TABLE jeton_login(
    idper integer PRIMARY KEY,
    jeton varchar(64),
    creadate timestamp
);
COMMENT ON TABLE jeton_login IS 'Jeton temporaire pour la modification du mot de passe';
COMMENT ON COLUMN jeton_login.idper IS 'Identifiant de la personne';
COMMENT ON COLUMN jeton_login.jeton IS 'UUID';
COMMENT ON COLUMN jeton_login.creadate IS 'Date de création du jeton';
-- -- See comments with psql : \d+ jeton_login 

ALTER TABLE jeton_login OWNER TO nobody;

CREATE TABLE reservation(
    id serial PRIMARY KEY,
    idper integer,
    idaction integer,
    dateres timestamp DEFAULT 'now',
    pass boolean DEFAULT FALSE,
    statut smallint DEFAULT 0 -- 0 (en attente de confirmation),1 (confirmée)
);

COMMENT ON TABLE reservation IS 'Stockage temporaire des informations de réservation';
COMMENT ON COLUMN reservation.idper IS 'Identifiant de la personne ayant réservé';  
COMMENT ON COLUMN reservation.idaction IS 'Action sur le planning'; 
COMMENT ON COLUMN reservation.dateres IS 'Date de soumission de la réservation';    
COMMENT ON COLUMN reservation.pass IS 'Sélection carte d''abonnement';
COMMENT ON COLUMN reservation.statut IS '0 = en attente de confirmation, 1 = confirmée';

ALTER TABLE reservation OWNER TO nobody;
-- delai minimum de réservation en heures
INSERT INTO config VALUES ('Reservation.delai.min',24);
-- delai d'annulation en heures
INSERT INTO config VALUES ('Reservation.annulation.delai',72);
-- delai max de réservation en jours
INSERT INTO config VALUES ('Reservation.delai.max',15);
-- paiment de l'adhésion exigé pour les réservations
INSERT INTO config VALUES ('Reservation.adhesion.requise', 't');
-- date de début des pré-inscriptions
INSERT INTO config VALUES ('Date.debut.preinscription','01-07-2015');

--INSERT INTO config 'Date.debut.preinscription', valeur::date - interval '2 month' from config where clef = 'Date.DebutAnnee';
-- compte adhésion auxiliaire pour les formations professionnelles
INSERT INTO comptepref VALUES ('ADHÉSIONS PRO',0,'');
--INSERT INTO comptepref SELECT 'ADHÉSIONS PRO', idcompte, idanalytique
--FROM comptepref WHERE id = 'ADHÉSIONS';

INSERT INTO menuprofil SELECT idmenu,10,auth FROM menuprofil WHERE profil = 0;
INSERT INTO menuprofil SELECT idmenu,11,auth FROM menuprofil WHERE profil = 0;

ALTER TABLE salle ALTER fonction TYPE varchar(128);

-- fix technicien
--CREATE TABLE technicien(
    --idper integer PRIMARY KEY REFERENCES personne(id) ON DELETE CASCADE,
    --specialite integer DEFAULT 0 NOT NULL REFERENCES categorie_studio(id) ON DELETE SET DEFAULT
--);
--ALTER TABLE technicien OWNER TO nobody;
-- 2.9.7
-- fix technicien
CREATE TABLE technicien(
    idper integer PRIMARY KEY REFERENCES personne(id) ON DELETE CASCADE,
    specialite integer DEFAULT 0 NOT NULL REFERENCES categorie_studio(id) ON DELETE SET DEFAULT
);
ALTER TABLE technicien OWNER TO nobody;


-- 2.10.0
INSERT INTO config VALUES('Jour.echeance.par.defaut','15');
INSERT INTO config VALUES('Arrondir.inter.paiements','f');
INSERT INTO config VALUES('Facturer.echeances.inscription','f');

CREATE TABLE echeance (
id serial,
reglement char(4),
libelle varchar(50),
montant integer,
piece varchar(10),
ecole integer DEFAULT 0,
compte integer,
analytique varchar(13)
);

CREATE INDEX echeance_compte_idx ON echeance(compte);

ALTER TABLE echeance OWNER TO nobody;

--ALTER TABLE suivi ADD note varchar(8);
--ALTER TABLE suivi ADD abs boolean;
--ALTER TABLE suivi ADD exc boolean;
-- 2.10.4
--ALTER TABLE suivi DROP exc;
--ALTER TABLE suivi DROP abs;
ALTER TABLE suivi ADD note varchar(8);
ALTER TABLE suivi ADD statut smallint DEFAULT 0;
COMMENT ON COLUMN suivi.statut IS '0 = PRE (présent), 1 = ABS (absent), 2 = EXC (excusé)';
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

-- musichalle
update config set valeur = E'\\\\192.168.1.202\\Algem-Serveur\\scripts' where clef='Scripts.path';
update version set version='2.11.2';
