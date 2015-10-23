SELECT DISTINCT ON (p1.nom, p1.prenom, p.jour, pg.debut,m.id)
 p.idper, p1.prenom, p1.nom, a.id, c.id, c.titre, m.id, m.titre, p.jour, pg.debut, pg.fin, (pg.fin - pg.debut) AS duree
FROM planning p, plage pg, action a, cours c, personne p1, salle s,commande_cours cc,commande_module cm,module m 
WHERE p.idper > 0 AND p.jour BETWEEN '01-10-2015' AND '31-10-2015' 
AND p.ptype IN (1,5,6) 
AND p.lieux = s.id
AND p.id = pg.idplanning 
AND p.action = a.id 
AND a.id = cc.idaction 
AND cc.module = cm.id 
AND cm.module IN(86,29,100,85,84,30,87,88,58) 
AND cm.module = m.id 
AND a.cours = c.id 
AND c.ecole = 0 
AND p.idper = p1.id 
AND s.nom !~* 'rattrap' 
ORDER BY m.id,p1.nom,p1.prenom,p.jour,pg.debut,a.cours