var dateDebut = utils.sqlDate(args.dateDebut);
var dateFin = utils.sqlDate(args.dateFin);

var query = "SELECT p.id, p.nom, p.prenom, f.type, f.statut, ( case when f.statut = 2 then f.niveau else null end) as niv, " +
        "extract(epoch from sum(f.duree)) / 3600.0 as total " +
        "FROM planning_fact f " +
        "JOIN personne p on f.prof = p.id " +
        "JOIN prof prof on prof.idper = p.id " +
        "WHERE prof.actif IS TRUE " +
        "AND date >= '" + dateDebut + "' " +
        "AND date < (DATE '" + dateFin + "' + 1) " +
        "GROUP BY p.id, p.nom, p.prenom, f.type, f.statut, niv";

utils.print(query);
out.setQuery(query);
