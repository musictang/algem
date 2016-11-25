--reunion
SELECT sum(p.fin-p.debut) FROM planning p JOIN personne per ON (p.idper = per.id)
WHERE p.ptype = 9
AND p.idper = 16094 
AND p.id NOT IN (SELECT idplanning FROM plage pg JOIN suivi s ON (pg.note = s.id) WHERE s.texte ~* 'coordination')
AND p.jour BETWEEN '24-11-2016' AND '25-11-2016';

-- coordination
SELECT sum(pg.fin-pg.debut) FROM plage pg 
JOIN personne per ON (pg.adherent = per.id)
JOIN suivi s ON (pg.note = s.id)
WHERE pg.adherent = 16094
AND s.texte ~* 'coordination'
AND pg.idplanning IN (
SELECT id FROM planning
WHERE ptype = 9 
AND jour BETWEEN '24-11-2016' AND '25-11-2016');

SELECT p.id,p.jour,pg.fin-pg.debut,9,pg.adherent,per.nom,per.prenom,p.action,false,'coordination'