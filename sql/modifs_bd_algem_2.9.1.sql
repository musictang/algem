--ALTER TABLE personne ADD pseudo varchar(64);

--ALTER SEQUENCE action_id_seq MINVALUE 0;
--INSERT INTO action VALUES(0,0,0,0,0,0);

CREATE TYPE priperiod AS ENUM(
  'HOUR',
  'MNTH',
  'QTER',
  'BIAN',
  'YEAR',
  'NULL'
);

ALTER TABLE commande_module ADD tarification priperiod;
ALTER TABLE commande_module ADD duree integer;

UPDATE config SET valeur = 'QTER' WHERE clef = 'Tarif.base';

ALTER TABLE cours ALTER libelle TYPE varchar(32);

--UPDATE commande_module SET paiement = 'MOIS' WHERE paiement = '0';
--UPDATE commande_module SET paiement = 'TRIM' WHERE paiement = '1';
--UPDATE commande_module SET paiement = 'ANNU' WHERE paiement = 'ANN';
--UPDATE commande_module SET paiement = NULL WHERE paiement = '';

UPDATE config SET clef = 'Periode.tarification' WHERE clef = 'Tarif.base';
