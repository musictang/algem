SELECT m.titre,e.echeance,e.compte,c.creation AS "FORMULE", trim(to_char((montant / 100.00)::numeric(10,2), '999999999D99')) AS total FROM echeancier2 e JOIN commande c ON (e.commande > 0  and e.commande = c.id) JOIN commande_module cm on c.id = cm.idcmd  JOIN module m on cm.module = m.id WHERE e.echeance BETWEEN '2017-10-01' AND '2017-10-14' AND e.montant > 0 AND e.reglement <> 'FAC' AND e.compte in (22,23,30) order by m.titre,e.echeance;


SELECT m.titre AS "FORMULE", trim(to_char((sum(montant) / 100.00)::numeric(10,2), '999999999D99')) AS "TOTAL" FROM echeancier2 e JOIN commande c ON (e.commande = c.id) JOIN commande_module cm on c.id = cm.idcmd  JOIN module m on cm.module = m.id WHERE e.echeance BETWEEN '2017-10-01' AND '2017-10-14' AND e.montant > 0 AND e.reglement <> 'FAC' AND e.compte in (22,23,30) GROUP BY m.titre ORDER BY m.titre;


SELECT e.commande AS "FORMULE", trim(to_char((sum(montant) / 100.00)::numeric(10,2), '999999999D99')) AS "TOTAL" FROM echeancier2 e WHERE e.echeance BETWEEN '2017-10-01' AND '2017-10-14' AND e.montant > 0 AND e.reglement <> 'FAC' AND e.compte in (22,23,30) GROUP BY e.commande ORDER BY e.commande;

SELECT m.titre AS "FORMULE", trim(to_char((sum(montant) / 100.00)::numeric(10,2), '999999999D99')) AS "TOTAL" FROM echeancier2 e JOIN commande c ON (e.commande > 0  AND e.commande = c.id) JOIN commande_module cm on c.id = cm.idcmd  JOIN module m on cm.module = m.id WHERE e.echeance BETWEEN '2017-10-01' AND '2017-10-14' AND e.montant > 0 AND e.reglement <> 'FAC' AND e.compte in (22,23,30) and m.id = 11 GROUP BY m.titre ORDER BY m.titre

select m.titre, trim(to_char(sum(sub.total), '999999999D99'))  AS "TOTAL" from (SELECT e.commande AS commande, sum(montant) / 100.00::numeric(10,2) AS total FROM echeancier2 e WHERE e.echeance BETWEEN '2017-10-01' AND '2017-10-14' AND e.montant > 0 AND e.reglement <> 'FAC' AND e.compte in (22,23,30) and e.commande > 0 GROUP BY e.commande)  as sub, module m join commande_module cm on m.id = cm.module where cm.idcmd = sub.commande group by m.titre;

SELECT e.commande, montant/ 100.00::numeric(10,2) FROM echeancier2 e WHERE e.echeance BETWEEN '2017-10-01' AND '2017-10-14' AND e.montant > 0 AND e.reglement <> 'FAC' AND e.compte in (22,23,30) and e.commande > 0 order by e.commande;
