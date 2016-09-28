var dateDebut = args.dateDebut != null ? utils.sqlDate(args.dateDebut) : utils.sqlDate(utils.getStartOfYear());
var dateFin = args.dateFin != null ? utils.sqlDate(args.dateFin) : utils.sqlDate(utils.getEndOfYear());

var query = "SELECT p1.id AS \"ID\",p1.nom || ' ' || p1.prenom AS \"NOM\","
+ " to_char(pl.jour, 'dd-mm-yyyy') AS \"JOUR\",pg.debut AS \"DEBUT\",pg.fin AS \"FIN\",c.titre AS \"COURS\",p2.nom || ' ' || p2.prenom AS \"PROF\","
+ " CASE WHEN s1.statut = 0 THEN NULL"
+ " WHEN s1.statut = 1 THEN 'ABS'"
+ " WHEN s1.statut = 2 THEN 'EXC'" 
+ " END AS \"STATUT\","
+ " s1.texte AS \"SUIVI IND.\","
+ " s2.texte AS \"SUIVI CO.\""
+ " FROM personne p1 JOIN plage pg ON p1.id = pg.adherent"
+ " JOIN suivi s1 ON pg.note = s1.id"
+ " JOIN planning pl ON pg.idplanning = pl.id"
+ " JOIN suivi s2 ON pl.note = s2.id"
+ " JOIN personne p2 ON pl.idper = p2.id"
+ " JOIN action a ON pl.action = a.id"
+ " JOIN cours c ON a.cours = c.id"
+ " WHERE pl.jour BETWEEN '" + dateDebut + "' AND '" + dateFin + "'"
+ " AND pg.adherent > 0"
+ " AND (pg.note > 0 OR pl.note > 0)"
+ " ORDER BY \"JOUR\",\"NOM\",pg.debut";

utils.print(query);
out.resultSet(dc.executeQuery(query));