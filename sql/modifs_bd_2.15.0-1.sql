CREATE TABLE contratformation (
    id serial,
    ctype smallint,
    idper  integer,
    idcmd integer,
    libelle varchar(256),
    saison varchar(32),
    debut date,
    fin date,
    financement varchar(128),
    total numeric(9,2),
    montant numeric(9,2),
    volumint real,
    volumext real,
    datesign date
    
);
ALTER TABLE contratformation OWNER TO nobody;
COMMENT ON COLUMN contratformation.ctype IS 'Type de contrat : 0 = non défini, 1 = bipartite, 2 = tripartite';
COMMENT ON COLUMN contratformation.libelle IS 'Nom formation';
COMMENT ON COLUMN contratformation.financement IS 'Nom de l''organisme financeur';
COMMENT ON COLUMN contratformation.total IS 'Montant total de la formation';
COMMENT ON COLUMN contratformation.montant IS 'Montant pris en charge';
COMMENT ON COLUMN contratformation.volumint IS 'Nombre d''heures de formation en interne';
COMMENT ON COLUMN contratformation.volumext IS 'Nombre d''heures de formation en entreprise';

-- données de test
INSERT into contratformation values(default,2,18691,6387,'TEST CONTRAT 2017', 'Saison 2016-2017','2017-01-01','2017-03-30','Région',4600,4600,400,150,'2017-12-01');

-- test
-- select m.titre from module m join commande_module cm on m.id = cm.module join commande c on cm.idcmd = c.id where c.id=6387;

-- select sum(duree) from (select distinct pl.fin-pl.debut as duree,p.jour,pl.debut from plage pl join planning p on pl.idplanning = p.id  join commande_cours cc on p.action = cc.idaction join commande c on cc.idcmd = c.id where c.id=6387 order by p.jour,pl.debut) as th;
