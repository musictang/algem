SELECT DISTINCT p.id, p.nom, p.prenom
FROM commande c JOIN personne p ON (c.adh = p.id)
  JOIN commande_cours cc ON (c.id = cc.idcmd)
  JOIN commande_module cm ON (cc.module = cm.id)
  JOIN module m ON (cm.module = m.id)
  JOIN planning pl ON (cc.idaction = pl.action)
  JOIN salle s ON (pl.lieux = s.id)
  WHERE cc.datedebut >= '01-09-2014' AND cc.datefin <= '30-06-2015'
  ORDER BY p.nom, p.prenom;
--
--     if (pro != null) {
--       query +=  AND m.code LIKE ' + (pro ? P%' : L%');
--     }
--     if (estab > 0) {
--       query +=  AND cc.idaction = pl.action AND s.etablissement =  + estab;
--     }
--     query +=  AND c.adh = p.id ORDER BY p.nom, p.prenom;
SELECT DISTINCT e.email FROM email e
 WHERE e.idper = 21525
 AND e.archive = false
 UNION
 SELECT DISTINCT e.email FROM email e, eleve  m
 WHERE m.idper =  21525 AND m.payeur = e.idper
 AND m.idper NOT IN (SELECT idper FROM email)
 AND e.archive = false;

SELECT DISTINCT m.email
FROM email m JOIN eleve e ON (m.idper = e.idper OR m.idper = e.payeur)
where e.idper = 21525 -- important : eleve.idper
AND e.archive = false;
