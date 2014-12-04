ALTER TABLE personne ADD pseudo varchar(64);

ALTER SEQUENCE action_id_seq MINVALUE 0;
INSERT INTO action VALUES(0,0,0,0,0,0);

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

-- POUR LES BASES EN PRODUCTION (anciennement trimestre par défaut)
UPDATE commande_module SET tarification = 'QTER';
--update commande_module set paiement = 'TRIM' where paiement is null and necheance = 3;
--update commande_module set paiement = 'MOIS' where paiement is null and necheance = 9;
--update commande_module set paiement = 'ANNU' where paiement is null and necheance = 1;

ALTER TABLE cours ALTER libelle TYPE varchar(32);

UPDATE commande_module SET paiement = 'MOIS' WHERE paiement = '0';
UPDATE commande_module SET paiement = 'TRIM' WHERE paiement = '1';
UPDATE commande_module SET paiement = 'ANNU' WHERE paiement = 'ANN';
update commande_module set paiement = 'MOIS' where paiement is null and necheance NOT in (0,1,3,9);

UPDATE commande_module SET paiement = NULL WHERE paiement = '';

UPDATE config SET clef = 'Periode.tarification' WHERE clef = 'Tarif.base';
-- alpha 5
INSERT INTO  suivi VALUES ( 0,null);

-- alpha 8
DELETE FROM categorie_salarie where id = 0; 
