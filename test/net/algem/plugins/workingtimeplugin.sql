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

SELECT pg.fin-pg.debut,p.idper FROM plage pg
JOIN planning p ON (pg.idplanning = p.id)
JOIN personne per ON (pg.adherent = per.id)
JOIN suivi s ON (pg.note = s.id)
WHERE pg.adherent = 16094
AND s.texte ~* 'coordination'
AND pg.idplanning IN (
SELECT id FROM planning
WHERE ptype = 9 
AND jour BETWEEN '24-11-2016' AND '25-11-2016');

SELECT p.id,p.jour,pg.fin-pg.debut,9,pg.adherent,per.nom,per.prenom,p.action,false,'coordination'

SELECT p.id,p.jour,p.fin-p.debut,p.ptype,p.idper,per.nom,per.prenom,p.action,c.collectif,a.statut,s.texte
FROM planning p  JOIN personne per ON (p.idper = per.id)
JOIN action a ON (p.action = a.id) LEFT JOIN cours c ON (a.cours = c.id)
LEFT JOIN plage pl ON (p.id = pl.idplanning AND p.idper = pl.adherent) LEFT JOIN suivi s ON (pl.note = s.id)           
WHERE p.ptype IN (1,5,6,9)
AND p.jour BETWEEN '28-11-2016' AND '02-12-2016'
AND (p.ptype = 9 OR (p.id IN (SELECT idplanning FROM plage)))
AND p.idper = 20351
ORDER BY per.nom,per.prenom,p.jour,p.debut;

SELECT DISTINCT ON (pl.id) pl.fin-pl.debut,m.id,m.code,e.analytique,pl.id
FROM plage pl JOIN planning p ON (pl.idplanning = p.id)
JOIN commande_cours cc ON (cc.idaction = p.action)
JOIN commande_module cm ON (cc.module = cm.id)
JOIN commande c ON (cm.idcmd = c.id AND pl.adherent = c.adh)
JOIN module m ON (cm.module = m.id)
LEFT JOIN echeancier2 e ON (cm.idcmd = e.commande AND e.adherent = pl.adherent)
WHERE pl.idplanning = 29435;
--25025;