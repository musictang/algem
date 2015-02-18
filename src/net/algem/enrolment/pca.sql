SELECT p.id,p.prenom,p.nom,p.pseudo,cm.* from commande_module cm, commande c, personne p 
WHERE tarification = 'HOUR' AND debut BETWEEN '01-01-2015' AND '31-12-2015'
AND cm.idcmd = c.id
AND c.adh = p.id;