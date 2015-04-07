var dateDebut = utils.sqlDate(args.dateDebut);
var dateFin = utils.sqlDate(args.dateFin);

var query;
if (args.showDetail) {
    query = "select per.id, per.nom, per.prenom, plan.jour, plan.debut, plan.fin, sal.nom, act.statut, act.niveau from prof prof " +
    "join personne per on prof.idper = per.id " +
    "join planning plan on plan.idper = per.id " +
    "join action act on plan.action = act.id " +
    "join salle sal on plan.lieux = sal.id " +
    "where " +
    "prof.actif is true " +
    "and plan.jour >= '2015-02-01' " +
    "and plan.jour <= '2015-02-28' " +
    "and sal.nom not like 'RATTRAPAGE%' " +
    "and act.cours != 0 " +
    "order by per.nom, per.prenom, plan.jour asc";
} else {
    query = "select per.id, per.nom, per.prenom, act.statut, ( case when act.statut = 2 then act.niveau else null end) as niv" +
    ", extract(epoch from sum(plan.fin - plan.debut)) / 3600.0 as total from prof prof " +
    "join personne per on prof.idper = per.id " +
    "join planning plan on plan.idper = per.id " +
    "join action act on plan.action = act.id " +
    "join salle sal on plan.lieux = sal.id " +
    "where " +
    "prof.actif is true " +
    "and plan.jour >= '" + dateDebut + "' " +
    "and plan.jour <= '" + dateFin + "' " +
    "and sal.nom not like 'RATTRAPAGE%' " +
    "and act.cours != 0 " +
    "GROUP BY per.id, per.nom, per.prenom, act.statut, niv " +
    "order by per.nom, per.prenom, act.statut, niv";
}

utils.print(query);

out.resultSet(dc.executeQuery(query));