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

