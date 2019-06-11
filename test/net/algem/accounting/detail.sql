
SELECT p.jour, p.idper, p.debut, p.fin, (p.fin - p.debut) AS duree
 FROM planning p
 WHERE p.jour BETWEEN '01-07-2015' AND '31-07-2015'
 AND p.ptype = 9
 ORDER BY p.idper,p.jour,p.debut;

SELECT s.*, g.bic, p.nom FROM prlsepa s JOIN personne p ON (s.payeur = p.id)
LEFT JOIN rib r ON (s.payeur = r.idper) LEFT JOIN guichet g ON (r.guichetid = g.id)
WHERE s.payeur = 21721
AND s.seqtype != 'LOCK'
ORDER BY id DESC LIMIT 1;