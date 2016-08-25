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

ALTER TABLE suivi ADD note varchar(8);
ALTER TABLE suivi ADD abs boolean;
ALTER TABLE suivi ADD exc boolean;
