var dateDebut = args.dateDebut != null ? utils.sqlDate(args.dateDebut) : utils.sqlDate(utils.getStartOfYear());
var dateFin = args.dateFin != null ? utils.sqlDate(args.dateFin) : utils.sqlDate(utils.getEndOfYear());

var query = "SELECT p1.id AS \"ID\",p1.nom || ' ' || p1.prenom AS \"NOM\","
+ " to_char(pl.jour, 'dd-mm-yyyy') AS \"JOUR\",pg.debut AS \"DEBUT\",pg.fin AS \"FIN\",c.titre AS \"COURS\",p2.nom || ' ' || p2.prenom AS \"PROF\","
+ " CASE WHEN s.statut = 0 THEN NULL"
+ " WHEN s.statut = 1 THEN 'ABS'"
+ " WHEN s.statut = 2 THEN 'EXC'" 
+ " END AS \"STATUT\","
+ " s.texte AS \"SUIVI\""
+ " FROM personne p1 JOIN plage pg ON p1.id = pg.adherent"
+ " JOIN suivi s ON pg.note = s.id"
+ " JOIN planning pl ON pg.idplanning = pl.id"
+ " JOIN personne p2 ON pl.idper = p2.id"
+ " JOIN action a ON pl.action = a.id"
+ " JOIN cours c ON a.cours = c.id"
+ " WHERE pl.jour BETWEEN '" + dateDebut + "' AND '" + dateFin + "'"
+ " AND pg.adherent > 0"
+ " AND s.statut in (1,2)"
+ " ORDER BY \"NOM\",\"JOUR\",pg.debut";

utils.print(query);
out.resultSet(dc.executeQuery(query));