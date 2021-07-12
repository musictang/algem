var dateDebut = args.dateDebut != null ? utils.sqlDate(args.dateDebut) : utils.sqlDate(utils.getStartOfYear());
var dateFin = args.dateFin != null ? utils.sqlDate(args.dateFin) : utils.sqlDate(utils.getEndOfYear());

var query = "SELECT p1.id AS \"Id\",p1.nom || ' ' || p1.prenom AS \"NOM\","
+ " c.titre AS \"COURS\", count(*) as \"NB\","
+ " p2.nom || ' ' || p2.prenom AS \"PROF\""
+ " FROM personne p1 JOIN plage pg ON p1.id = pg.adherent"
+ " JOIN suivi s ON pg.note = s.id"
+ " JOIN planning pl ON pg.idplanning = pl.id"
+ " JOIN personne p2 ON pl.idper = p2.id"
+ " JOIN action a ON pl.action = a.id"
+ " JOIN cours c ON a.cours = c.id"
+ " WHERE pl.jour BETWEEN '" + dateDebut + "' AND '" + dateFin + "'"
+ " AND pg.adherent > 0"
+ " AND s.statut = 1"
+ " GROUP BY c.titre, p1.id, \"PROF\", s.statut HAVING count(*) > 1"
+ " ORDER BY \"NOM\",\"NB\" DESC,\"COURS\"";

utils.print(query);
out.setQuery(query);
