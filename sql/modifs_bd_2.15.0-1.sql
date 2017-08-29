CREATE TABLE contratformation (
    id serial,
    idper  integer,
    org integer,
    libelle varchar(256),
    idmodule integer,
    debut date,
    fin date,
    financement varchar(128),
    total numeric(9,2),
    montant numeric(9,2),
    volumint smallint,
    volumext smallint,
    datesign date
    
);
ALTER TABLE contratformation OWNER TO nobody;
COMMENT ON COLUMN contratformation.org IS 'Organisation id';
COMMENT ON COLUMN contratformation.libelle IS 'Nom formation';
COMMENT ON COLUMN contratformation.financement IS 'Nom de l''organisme financeur';
COMMENT ON COLUMN contratformation.total IS 'Montant total de la formation';
COMMENT ON COLUMN contratformation.montant IS 'Montant pris en charge';
COMMENT ON COLUMN contratformation.volumint IS 'Nombre d''heures de formation en interne';
COMMENT ON COLUMN contratformation.volumext IS 'Nombre d''heures de formation en entreprise';
