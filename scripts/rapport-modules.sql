SELECT m.titre, (sum(montant) / 100.00)::numeric(10,2) from echeancier2 e join commande c on e.commande = c.id join commande_module cm on c.id = cm.idcmd  join module m on cm.module = m.id where e.echeance >= '01-09-2017' and e.montant > 0 and e.reglement <> 'FAC' and e.compte not in (19,20) group by m.titre;

SELECT m.titre, (e.montant / 100.00)::numeric(10,2),e.compte,e.reglement from echeancier2 e join commande c on e.commande = c.id join commande_module cm on c.id = cm.idcmd  join module m on cm.module = m.id where e.echeance >= '01-09-2017' and e.montant > 0 and e.reglement <> 'FAC' and m.titre='(AD) Atelier Découverte';

-- GOOD
SELECT m.titre, (sum(montant) / 100.00)::numeric(10,2) from echeancier2 e join commande c on e.commande = c.id join commande_module cm on c.id = cm.idcmd  join module m on cm.module = m.id where e.echeance >= '18-04-2017' and e.montant > 0 and e.reglement <> 'FAC' and e.compte not in (19,20) and e.paye = true group by m.titre order by m.titre;

SELECT m.titre, (sum(montant) / 100.00)::numeric(10,2) from echeancier2 e join commande c on e.commande = c.id join commande_module cm on c.id = cm.idcmd  join module m on cm.module = m.id where e.echeance >= '18-04-2017' and e.montant > 0 and e.reglement <> 'FAC' and e.compte not in (19,20) group by m.titre;
