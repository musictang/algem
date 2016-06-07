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
JOIN commande_module cm on (cm.module = m.id)
JOIN commande_cours cc on (cm.id = cc.module)
JOIN commande c on (cc.idcmd = c.id)
JOIN plage pl on (c.adh = pl.adherent)
JOIN planning p on (pl.idplanning = p.id and p.action = cc.idaction)
WHERE c.adh = 18584
AND p.jour BETWEEN '21-09-2015' AND '16-05-2016'
-- AND p.action = cc.idaction;


-- dernière date de cours programmée pour tel module
SELECT jour FROM planning p
join commande_cours cc on (p.action = cc.idaction)
join commande_module cm on (cc.module = cm.id)
join commande c on (cm.idcmd = c.id)
join plage pl on (p.id = pl.idplanning and c.adh = pl.adherent)
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
from planning p
join personne p1 on (p.idper = p1.id)
join salle s on (p.lieux = s.id)
join action a on (p.action = a.id)
join cours c on (a.cours = c.id)
join plage pl on (p.id = pl.idplanning)

left join suivi n1 on (pl.note = n1.id)
left join suivi n2 on (p.note = n2.id)
-- where pl.adherent in (21753,13827,21611)
where pl.adherent in (
select distinct e.idper
from eleve e
join commande c on (e.idper = c.adh)
join commande_module cm on (c.id = cm.idcmd)
join module m on (cm.module = m.id)
where c.creation between '01-07-2015' and '30-06-2016'
and m.code like 'P%'
)
and p.jour >= '21-09-2015'
order by pl.adherent,p.jour,pl.debut;

select * from commande
where  creation between '01-07-2015' and '30-06-2016'
AND id IN(
SELECT idcmd FROM commande_module cm join module m on (cm.module = m.id)
where m.code like 'P%')
ORDER BY id;
