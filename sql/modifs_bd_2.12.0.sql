--2.12.0
INSERT INTO config VALUES ('Nombre.sessions.par.defaut', '33');
INSERT INTO config VALUES ('Debut.annee.comptable', '01-09-2016');

ALTER TABLE module ADD actif boolean DEFAULT TRUE;

-- UPDATE module set actif=false where id not in(select module from commande_module where debut >='01-07-2016');
-- UPDATE module set actif=true where id in(select module from commande_module where debut >='01-07-2016');
