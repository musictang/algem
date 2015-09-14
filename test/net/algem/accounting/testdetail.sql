
SELECT p.jour, p.idper, p.debut, p.fin, (p.fin - p.debut) AS duree
 FROM planning p
 WHERE p.jour BETWEEN '01-07-2015' AND '31-07-2015'
 AND p.ptype = 9
 ORDER BY p.idper,p.jour,p.debut;