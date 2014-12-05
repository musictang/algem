SELECT DISTINCT ON (p1.nom, p1.prenom, p.jour, pg.debut) p.idper, pg.adherent, p1.nom, c.id, c.titre, p2.prenom, p2.nom, p.jour, pg.debut, pg.fin, (pg.fin - pg.debut) AS duree
  FROM planning p, plage pg, action a, cours c, personne p1, personne p2
  WHERE pg.idplanning = p.id
  AND p.jour BETWEEN '01-11-2014' AND '31-12-2014'
  AND p.ptype IN (1,5,6)
AND p.action = a.id
AND a.cours = c.id
AND p.idper = p1.id
AND pg.adherent = p2.id
ORDER BY p1.nom, p1.prenom,p.jour,pg.debut,a.cours;
