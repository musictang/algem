-- 2.17.1

CREATE TABLE objetalouer (
    id serial primary key,
    creation date,
    type varchar(32),
    marque varchar(32),
    identification varchar(48),
    description varchar(64),
    vendeur varchar(32),
    actif boolean
);

ALTER TABLE objetalouer OWNER TO nobody;
COMMENT ON COLUMN objetalouer.creation IS 'Date achat';
COMMENT ON COLUMN objetalouer.type IS 'type instrument';
COMMENT ON COLUMN objetalouer.marque IS 'marque fabricant';
COMMENT ON COLUMN objetalouer.identification IS 'code texte identifiant';
COMMENT ON COLUMN objetalouer.vendeur IS 'boutique vendeur';
COMMENT ON COLUMN objetalouer.description IS 'texte description';
COMMENT ON COLUMN objetalouer.actif IS 'booleen actif O/N';

CREATE TABLE location (
    id serial primary key,
    objet int,
    debut date,
    fin date,
    adherent int,
    libelle varchar(64)
);

ALTER TABLE location OWNER TO nobody;
COMMENT ON COLUMN location.objet IS 'ID object loué';
COMMENT ON COLUMN location.debut IS 'Date debut location';
COMMENT ON COLUMN location.fin IS 'Date fin location';
COMMENT ON COLUMN location.adherent IS 'id adherent';
COMMENT ON COLUMN location.libelle IS 'texte libelle';



