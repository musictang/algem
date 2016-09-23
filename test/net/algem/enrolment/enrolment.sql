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

-- modules associés au suivi sur une période
SELECT DISTINCT m.titre FROM module m
JOIN commande_module cm ON (cm.module = m.id)
JOIN commande_cours cc ON (cm.id = cc.module)
JOIN commande c ON (cc.idcmd = c.id)
JOIN plage pl ON (c.adh = pl.adherent)
JOIN planning p ON (pl.idplanning = p.id and p.action = cc.idaction)
WHERE c.adh = 18584
AND p.jour BETWEEN '21-09-2015' AND '16-05-2016'
-- AND p.action = cc.idaction;


-- dernière date de cours programmée pour tel module
SELECT jour FROM planning p
JOIN commande_cours cc ON (p.action = cc.idaction)
JOIN commande_module cm ON (cc.module = cm.id)
JOIN commande c ON (cm.idcmd = c.id)
JOIN plage pl ON (p.id = pl.idplanning and c.adh = pl.adherent)
where cm.id = 2227 -- commande 1744
and c.adh = 996
order by jour desc limit 1; -- Stella Douglas

-- suivi des élèves en forpro
-- liste des forpro
-- Activité	Professeur	Salle	Suivi	Date	Début	Fin	Durée
-- AND p.jour >= '" + date + "' AND p.action IN (" + actions + ")"
--     AND (pg.note >= 0 OR p.note > 0)"
--     AND pg.note = s1.id"
--     AND p.note = s2.id"
--     AND pg.adherent = " + memberId
--     ORDER BY p.jour, pg.debut";
select pl.adherent,c.titre,p1.nom,p1.prenom,s.nom,
case
when n1.texte is null or n1.texte = '' then n2.texte
end,
p.jour,pl.debut,pl.fin
FROM planning p
JOIN personne p1 ON (p.idper = p1.id)
JOIN salle s ON (p.lieux = s.id)
JOIN action a ON (p.action = a.id)
JOIN cours c ON (a.cours = c.id)
JOIN plage pl ON (p.id = pl.idplanning)

left JOIN suivi n1 ON (pl.note = n1.id)
left JOIN suivi n2 ON (p.note = n2.id)
-- where pl.adherent in (21753,13827,21611)
where pl.adherent in (
select distinct e.idper
FROM eleve e
JOIN commande c ON (e.idper = c.adh)
JOIN commande_module cm ON (c.id = cm.idcmd)
JOIN module m ON (cm.module = m.id)
where c.creation between '01-07-2015' and '30-06-2016'
and m.code like 'P%'
)
and p.jour >= '21-09-2015'
order by pl.adherent,p.jour,pl.debut;

select * FROM commande
where  creation between '01-07-2015' and '30-06-2016'
AND id IN(
SELECT idcmd FROM commande_module cm JOIN module m ON (cm.module = m.id)
where m.code like 'P%')
ORDER BY id;

-- recherche de tous les cours du même type sur la première semaine dispo
-- critères code cours, durée cours, numéro action, début, fin

SELECT DISTINCT on (dow,p.jour,p.debut,a.id)
 p.id,p.jour,extract('dow' from p.jour) AS dow,p.debut,p.fin,p.idper,a.id,a.statut,c.titre,per.nom,per.prenom 
FROM planning p JOIN action a ON (p.action = a.id)
 JOIN cours c ON (a.cours = c.id) 
JOIN salle s ON (p.lieux = s.id)
 JOIN personne per ON (p.idper = per.id)
 WHERE p.ptype in(1,6) 
AND p.jour BETWEEN '05-01-2016' AND '11-01-2016'
AND c.code = 3 AND 
(p.fin-p.debut) = '02:00' 
AND a.id != 8261 
AND s.nom NOT LIKE 'RATTRAP%' 
ORDER BY dow,p.jour,p.debut,a.id;

SELECT pg.id, pg.idplanning, pg.debut, pg.fin, pg.adherent, pg.note,
 p.jour, p.action, p.idper, p.lieux, p.ptype,
 s1.texte, case when pg.note = 0 and s1.note = '0' then null else s1.note  end as note1, s1.statut, s2.texte 
FROM plage pg, planning p, suivi s1, suivi s2 
WHERE p.ptype IN (1,5,6) 
AND p.id = pg.idplanning 
AND p.jour BETWEEN '19-09-2016' AND '12-11-2016' 
AND (pg.note >= 0 OR p.note > 0) 
AND pg.note = s1.id 
AND p.note = s2.id 
AND pg.adherent = 21967 
ORDER BY p.jour, pg.debut;

SELECT pl.id,pl.idplanning,pl.debut,pl.fin,n1.id,n1.texte,n1.note,n1.statut,n2.id,n2.texte,n2.statut
FROM planning p
-- JOIN personne per on p.idper = per.id
JOIN action a ON p.action = a.id
-- JOIN cours c ON a.cours = c.id
JOIN plage pl ON p.id = pl.idplanning
-- JOIN salle s ON p.lieux = s.id
LEFT JOIN suivi n1 ON pl.note = n1.id
LEFT JOIN suivi n2 ON p.note = n2.id
WHERE p.ptype IN (1,5,6)
AND pl.adherent = 21967 
AND p.jour BETWEEN '19-09-2016' AND '12-11-2016' 
ORDER BY p.jour,pl.debut;

SELECT p.id,p.ptype,p.nom,p.prenom,p.civilite,p.droit_img,p.organisation,p.partenaire,p.pseudo,
idper,profession,datenais,payeur,nadhesions,pratique,niveau 
FROM personne p, eleve 
WHERE payeur=18547
AND p.id = eleve.idper 
ORDER BY p.prenom,p.nom

SELECT DISTINCT ON (p.nom) p.id,p.ptype,p.nom,p.prenom,p.civilite,p.droit_img,p.organisation,p.partenaire,p.pseudo, e.actif
 FROM personne p  JOIN etablissement e ON (p.id = e.id) 
WHERE p.ptype = 5 AND e.actif = TRUE
 AND e.idper = 16094 
ORDER BY p.nom

SELECT DISTINCT ON (p.nom) p.id,p.ptype,p.nom,p.prenom,p.civilite,p.droit_img,p.organisation,p.partenaire,p.pseudo, e.actif
 FROM personne p  JOIN etablissement e ON (p.id = e.id) 
WHERE p.ptype = 5 AND e.actif = TRUE
--  AND e.idper = 16094 
ORDER BY p.nom