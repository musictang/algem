DROP VIEW planningvue;
-- modification taille nom salle
ALTER TABLE salle ALTER nom type varchar(32);
ALTER INDEX slx2 RENAME TO salle_nom_idx; 

CREATE VIEW planningvue AS 
SELECT pl.id, pl.jour, pl.debut, pl.fin, pl.action, p.id AS profid, p.prenom AS prenomprof, p.nom AS nomprof, s.id AS salleid, s.nom AS salle, c.id AS coursid, c.titre AS cours, c.ecole
   FROM planning pl, personne p, salle s, cours c, action a
  WHERE (pl.ptype = ANY (ARRAY[1, 5, 6])) AND pl.action = a.id AND a.cours = c.id AND pl.lieux = s.id AND pl.idper = p.id;

---- nettoyage salle 0
DELETE FROM sallequip where idsalle = 0;


ALTER TABLE horaires DROP CONSTRAINT jour_semaine;
ALTER TABLE horaires ADD CONSTRAINT jour_semaine check(jour >= 1 and jour <= 7);

INSERT INTO config VALUES ('Heure.ouverture','09:00');

--INSERT INTO horaires VALUES (2,3,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (2,4,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (2,5,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (2,6,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (2,7,'10:00:00','18:00:00');

--INSERT INTO horaires VALUES (3,1,'00:00:00','00:00:00');
--INSERT INTO horaires VALUES (3,2,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (3,3,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (3,4,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (3,5,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (3,6,'09:00:00','22:00:00');
--INSERT INTO horaires VALUES (3,7,'10:00:00','18:00:00');

--INSERT INTO horaires VALUES (4,1,'00:00:00','00:00:00');
--INSERT INTO horaires VALUES (4,2,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (4,3,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (4,4,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (4,5,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (4,6,'09:00:00','22:00:00');
--INSERT INTO horaires VALUES (4,7,'10:00:00','18:00:00');

--INSERT INTO horaires VALUES (5,1,'00:00:00','00:00:00');
--INSERT INTO horaires VALUES (5,2,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (5,3,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (5,4,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (5,5,'10:00:00','22:00:00');
--INSERT INTO horaires VALUES (5,6,'09:00:00','22:00:00');
--INSERT INTO horaires VALUES (5,7,'10:00:00','18:00:00');

INSERT INTO config VALUES ('Tarif.base', 'TRIM');
INSERT INTO config VALUES ('Afficher.noms.plages', 't');

-- -- Ajout colonne commande_module
ALTER TABLE commande_module ADD arret boolean DEFAULT FALSE;


