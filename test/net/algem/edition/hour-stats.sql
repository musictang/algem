-- 21651
-- Recherche des emails des adhérents
-- solution 1 7,706 ms
SELECT DISTINCT m.email FROM email m
WHERE m.idper = 21651
AND m.archive = false
UNION
SELECT DISTINCT m.email FROM email m JOIN eleve e ON (m.idper = e.payeur) JOIN personne p ON (e.payeur = p.id)
WHERE e.idper = 21651
AND e.idper NOT IN (SELECT idper FROM  email) -- on ne cherche pas le payeur si l'adhérent a déjà une adresse
AND p.organisation IS NULL -- on ne cherche le payeur que s'il n'appartient pas à une organisation
AND m.archive = false;

-- solution 2  3,402 ms ne fonctionne pas correctement
-- SELECT DISTINCT e.email FROM email e JOIN eleve m
-- ON (e.idper = m.idper OR (e.idper = m.payeur AND m.payeur IN (
-- SELECT id FROM personne WHERE organisation IS NULL)
-- )))
-- WHERE m.idper = 21651 -- 18584(gambier)
-- AND e.archive = false;


SELECT p.* FROM planning p
			WHERE jour = '30-05-2016' AND idper = 19657
			AND ((debut >= '10:00' AND debut < '12:00') OR (fin > '10:00' AND fin <= '12:00')
			OR (debut <= '10:00' AND fin >= '12:00'));

-- HEURES PROFS JAZZ A TOURS
-- eleves FP (formation financée)
--  BEAUDOUIN | Olivier
--  BOUDIN    | Charles
--  BOULADOUX | SIMON
--  DESBORDES | Clément
--  GRESSIER  | Gaëlle
--  LAMY      | Matthieu
--  MEHL      | Marin
--  MILLET    | Théophile
--  PETITE    | Basile
--  PUYRAUD   | Léo
--  REMAUD    | Simon
--  REYMOND   | Jean
--  SILLY     | Théodore
--  THOMAS    | Lucine
--  VIALA     | Rémi
-- DEM = Parcours DEM Jazz (149)
-- + Licence musicologie = L1 Musicologie/Jazz à Tours (151)
-- + L2 Musicologie / Jazz à Tours (157)+ L3 Musicologie / Jazz à Tours (158)
-- + Loisirs = tous les cours donnés par le prof inclus dans une formule Loisir ?

-- profs : -- 56 : nicolas huon



-- BONNE REQUETE !!!
-- DEM + L1 + L2 + Loisirs
select nom,prenom,sum(q1.fin-q1.debut) from
(select distinct on (p.jour,pl.debut)
pl.debut as debut, pl.fin as fin, per.nom as nom,per.prenom as prenom
from plage pl join planning p on (pl.idplanning = p.id)
join personne per on (p.idper = per.id)
where p.ptype in(1,5,6)
and p.idper = 106
and p.jour BETWEEN  '01-05-2016' AND '31-05-2016'
and pl.adherent in(select adh from commande d
join commande_cours cc on (d.id =cc.idcmd)
join commande_module cm on (cc.module = cm.id)
join module m on (cm.module = m.id)
join echeancier2 e on (cm.idcmd = e.commande)
 where (m.id in(149,151,157,158) or m.code like 'L%')
and cc.idaction = p.action
AND e.analytique != '15000'
)) as q1
group by nom,prenom order by nom,prenom;


SELECT sum(q1.fin-q1.debut) FROM
(SELECT DISTINCT ON (p.jour,pl.debut)
p.id,p.jour,p.action,p.lieux,pl.debut AS debut, pl.fin AS fin
FROM  plage  pl JOIN  planning p ON (pl.idplanning = p.id)
JOIN  personne  per ON (p.idper = per.id)
WHERE p.ptype IN ( 1,5,6 )
AND p.idper = 75
AND p.jour between  '02-05-2016' and '09-05-2016'
AND pl.adherent in(SELECT adh FROM  commande  d
JOIN  commande_cours  cc ON (d.id =cc.idcmd)
JOIN  commande_module  cm ON (cc.module = cm.id)
JOIN  module  m ON (cm.module = m.id)
JOIN  echeancier2  e ON (cm.idcmd = e.commande)
 WHERE (m.id in(149,151,157,158) OR m.code like 'L%')
AND cc.idaction = p.action
AND e.analytique != '15000')) AS q1;

-- Parcours Brevet = Parcours BREVET Jazz Cours 30 mn (141)
-- + Parcours BREVET Jazz Cours 45 mn (142)
-- + Parcours MIMA = Parcours MIMA 1 Jazz (143)
--  + Parcours MIMA 2 Jazz(144)
--  + Parcours MIMA 1 MAA (145)
--  + Parcours MIMA 2 MAA (146)
-- + Parcours BREVET MAA Cours 30 mn (147)
-- + Parcours BREVET MAA Cours 45 mn (148)
-- + Formation Financées = ????

select nom,prenom,sum(q1.fin-q1.debut) from
(select distinct on (p.jour,pl.debut)
pl.debut as debut, pl.fin as fin, per.nom as nom,per.prenom as prenom
from plage pl join planning p on (pl.idplanning = p.id)
join personne per on (p.idper = per.id)
where p.ptype in(1,5,6)
and p.idper = 75 --46
and p.jour BETWEEN  '02-05-2016' AND '09-05-2016'
and pl.adherent in(select adh from commande d
join commande_cours cc on (d.id =cc.idcmd)
join commande_module cm on (cc.module = cm.id)
join module m on (cm.module = m.id)
join echeancier2 e on (cm.idcmd = e.commande)
 where cc.idaction = p.action
and ((m.id between 141 and 148 AND e.analytique != '15000') or e.analytique = '15000')
)) as q1
group by nom,prenom order by nom,prenom;

select distinct on (pl.debut)
pl.debut as debut, pl.fin as fin, per.nom as nom,per.prenom as prenom
from plage pl join planning p on (pl.idplanning = p.id)
join personne per on (p.idper = per.id)
where p.ptype in(1,5,6)
and p.idper = 57 --46
and p.jour =  '07-06-2016'
and pl.adherent in(select adh from commande d
join commande_cours cc on (d.id =cc.idcmd)
join commande_module cm on (cc.module = cm.id)
join module m on (cm.module = m.id)
join echeancier2 e on (cm.idcmd = e.commande)
 where cc.idaction = p.action
and ((m.id between 141 and 148 AND e.analytique != '15000') or e.analytique = '15000')
);

SELECT sum(q1.fin-q1.debut) FROM
(SELECT DISTINCT ON (pl.debut) pl.debut AS debut, pl.fin AS fin
 FROM  plage pl JOIN  planning p ON (pl.idplanning = p.id)
 JOIN personne per ON (p.idper = per.id)
 WHERE p.ptype IN ( 1 ,5, 6 )
 AND p.idper =  57
 AND p.jour BETWEEN  '01-06-2016' AND '30-06-2016
' AND pl.adherent IN(SELECT adh FROM commande  d
 JOIN  commande_cours  cc ON (d.id = cc.idcmd)
 JOIN  commande_module  cm ON (cc.module = cm.id)
 JOIN  module m ON (cm.module = m.id)
 JOIN echeancier2  e ON (cm.idcmd = e.commande)
 WHERE cc.idaction = p.action
 AND ((m.id BETWEEN 141 AND 148 AND e.analytique != '15000') OR e.analytique = '15000')
)) AS q1;


-- planning administratif réunion
select  per.nom,per.prenom, sum(p.fin-p.debut) as duree
from planning p join personne per on (p.idper = per.id)
where p.ptype = 9
and p.jour = '06-06-2016'
group by per.nom,per.prenom;

select sum(p.fin-p.debut) from planning p join personne per on (p.idper = per.id)
where p.ptype = 9
and p.jour = '06-06-2016'
and p.idper = 46;


-- stats
SELECT cours.titre, count(distinct plage.adherent)
FROM plage, planning, cours, action,commande_cours,commande_module,module
WHERE plage.idplanning = planning.id
AND planning.ptype IN(1,5,6)
-- AND planning.action = commande_cours.idaction
-- AND commande_cours.module = commande_module.module
-- AND commande_module.module = module.id
-- AND module.code LIKE  'L%'
AND planning.jour BETWEEN '01-09-2015' AND '30-06-2016'
-- AND plage.debut >= planning.debut
-- AND plage.fin <= planning.fin
AND planning.action = action.id
AND action.cours = cours.id
-- AND cours.collectif = false
GROUP BY cours.titre;

SELECT cours.titre, count(distinct plage.adherent)
FROM plage, planning, cours, action
WHERE plage.idplanning = planning.id
AND planning.ptype IN(1,5,6)
AND planning.jour BETWEEN '01-09-2015' AND '30-06-2016'
AND planning.action = action.id
AND action.cours = cours.id
AND action.id in(select idaction from commande_cours cc
 join commande_module cm on(cc.module = cm.id) join module m on (cm.module = m.id)
where m.code like 'P%')
GROUP BY cours.titre;


SELECT DISTINCT eleve.idper,personne.prenom || ' ' || personne.nom as nom_prenom FROM commande_cours, commande, eleve, personne
WHERE commande_cours.datedebut >= '01-09-2015' AND commande_cours.datedebut <= '30-06-2016'
AND commande_cours.idcmd = commande.id
AND commande.adh = eleve.idper
AND eleve.idper = personne.id
AND (extract(year from age(eleve.datenais)) > 100
OR extract(year from age(eleve.datenais)) < 1
OR eleve.datenais is null) ORDER BY nom_prenom;

-- plannings courants
SELECT p.id,p.jour,p.debut,p.fin,p.idper,per.nom,per.prenom,p.action ,c.collectif,a.statut
FROM planning p  JOIN personne per ON (p.idper = per.id) JOIN action a ON (p.action = a.id) join cours c on (a.cours=c.id)
WHERE p.ptype IN (1,5,6)
AND p.jour BETWEEN  '01-06-2016' AND '30-06-2016'
ORDER BY per.nom,per.prenom,p.jour,p.debut;

-- recherche du statut du cours
select c.collectif
from cours c join action a on (c.id=a.cours) join planning p on (a.id=p.action)
where p.id = 24834;

-- si cours individuel loisir
SELECT distinct on (pl.id) pl.id,pl.fin-pl.debut,m.id,m.code,e.analytique
from plage pl join planning p on (pl.idplanning=p.id)
join commande_cours cc on (cc.idaction=p.action)
join commande_module cm on (cc.module=cm.id)
join module m on (cm.module=m.id)
join commande d on (cm.idcmd = d.id)
join echeancier2 e on (d.id=e.commande)
where pl.idplanning = 24834;
-- group by pl.id,m.id,m.code,e.analytique;
-- and p.action = 8519;

JOIN " + CourseOrderIO.TABLE + " cc ON (d.id =cc.idcmd)
JOIN " + ModuleOrderIO.TABLE + " cm ON (cc.module = cm.id)
JOIN " + ModuleIO.TABLE + " m ON (cm.module = m.id)
JOIN " + OrderLineIO.TABLE + " e ON (cm.idcmd = e.commande)
 WHERE (m.id in(149,151,157,158) OR m.code like 'L%')
AND cc.idaction = p.action
AND e.analytique != '15000')) AS q1


SELECT p.id,p.fin-p.debut,p.ptype,p.idper,per.nom,per.prenom,p.action,c.collectif,a.statut
 FROM planning p  JOIN personne per ON (p.idper = per.id)
JOIN action a ON (p.action = a.id)
LEFT JOIN cours c ON (a.cours = c.id)
 WHERE p.ptype IN (1,5,6,9)
 AND p.jour BETWEEN '01-06-2016' AND '30-06-2016'
AND (p.ptype = 9 OR p.id IN (SELECT idplanning FROM plage))
AND p.idper = 63
ORDER BY per.nom,per.prenom,p.jour,p.debut;

SELECT p.id,p.fin-p.debut,p.ptype,p.idper,per.nom,per.prenom,p.action,c.collectif,a.statut
 FROM planning p  JOIN personne per ON (p.idper = per.id)
JOIN action a ON (p.action = a.id)
left JOIN cours c ON (a.cours = c.id)
 WHERE p.ptype IN (1,5,6,9)
 AND p.jour BETWEEN '01-06-2016' AND '30-06-2016'
AND p.ptype = 9
AND p.idper = 63
ORDER BY per.nom,per.prenom,p.jour,p.debut;