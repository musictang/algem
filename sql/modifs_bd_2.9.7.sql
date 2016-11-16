-- 2.9.7
-- fix technicien
CREATE TABLE technicien(
    idper integer PRIMARY KEY REFERENCES personne(id) ON DELETE CASCADE,
    specialite integer DEFAULT 0 NOT NULL REFERENCES categorie_studio(id) ON DELETE SET DEFAULT
);
ALTER TABLE technicien OWNER TO nobody;


