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

INSERT INTO config VALUES ('Reservation.delai',24);
INSERT INTO config VALUES ('Annulation.delai',72);
