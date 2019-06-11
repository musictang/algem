-- Pour une semaine type
-- -> pour chaque prof
-- ->-> pour chaque jour de la semaine
-- ->->-> pour chaque cours
-- ->->->-> nombre total d'élèves
-- ->->->-> liste des élèves (nom, prénom)

select distinct per.prenom,per.nom,date_part('dow',p.jour) as j,to_char(p.jour, 'TMDAY') as jour,c.titre,count(pl.adherent) as eleves
from planning p join plage pl on p.id = pl.idplanning
join personne per on p.idper = per.id
join action a on p.action = a.id
join cours c on a.cours = c.id
where p.jour between '2018-04-02' and '2018-04-08'
group by per.prenom,per.nom,jour,c.titre
order by per.nom,per.prenom,j;
