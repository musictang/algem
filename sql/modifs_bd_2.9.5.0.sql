-- 2.9.5.0
CREATE TABLE jeton_login(
    idper PRIMARY KEY,
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
    typeres integer,
    dateres timestamp DEFAULT 'now',
    pass boolean DEFAULT FALSE
);

COMMENT ON TABLE reservation IS 'Stockage temporaire des informations de réservation';
COMMENT ON COLUMN reservation.idper IS 'Identifiant de la personne ayant réservé';  
COMMENT ON COLUMN reservation.typeres IS 'Type de réservation : individuelle, groupe'; 
COMMENT ON COLUMN reservation.dateres IS 'Date de soumission de la réservation';    
COMMENT ON COLUMN reservation.pass IS 'Sélection carte d''abonnement';   

ALTER TABLE reservation OWNER TO nobody;
-- delai minimum de réservation en heures
INSERT INTO config VALUES ('Reservation.delai.min',24);
-- delai d'annulation en heures
INSERT INTO config VALUES ('Annulation.delai',72);
-- delai max de réservation en jours
INSERT INTO config VALUES ('Reservation.delai.max',15);
