CREATE TABLE conventionstage (
    id serial,
    ctype smallint,
    idper  integer,
    idorg integer,
    assurance varchar(64),
    assuranceref varchar(64),
    libelle varchar(256),
    saison varchar(32),
    debut date,
    fin date,
    datesign date
);
ALTER TABLE conventionstage OWNER TO nobody;
COMMENT ON COLUMN conventionstage.ctype IS 'Type de contrat : 0 = non défini, 1 = bipartite, 2 = tripartite';
COMMENT ON COLUMN conventionstage.libelle IS 'Nom de la formation';
COMMENT ON COLUMN conventionstage.assurance IS 'Nom assurance responsabilité civile';
COMMENT ON COLUMN conventionstage.assuranceref IS 'Référence du contrat d''assurance';
COMMENT ON COLUMN conventionstage.debut IS 'Date de début du stage en entreprise';
COMMENT ON COLUMN conventionstage.fin IS 'Date de fin de stage en entreprise';
COMMENT ON COLUMN conventionstage.datesign IS 'Date de signature';

-- données de test
INSERT into conventionstage values(default,2,18691,21751,'Generali','G42568FR','Cycle intensif Guitare','Saison 2016-2017','2017-01-01','2017-03-30','2017-12-01');
