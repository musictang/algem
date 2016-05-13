--81 pianotage
--39 batucada
--112 eveil musical
SELECT DISTINCT p.id, p.nom, p.prenom, pi.instrument 
FROM personne p LEFT JOIN person_instrument pi ON (p.id = pi.idper AND pi.ptype = 1 AND pi.idx = 0), 
eleve e, commande c, commande_cours cc, commande_module cm 
WHERE cm.module = 112 
AND cm.id = cc.module AND cc.datedebut BETWEEN 'Mon Sep 21 14:00:00 CEST 2015' AND 'Thu Jun 30 14:00:00 CEST 2016' 
AND cc.idcmd = c.id AND c.adh = p.id 
AND p.id = e.idper 
ORDER BY p.nom,p.prenom;
