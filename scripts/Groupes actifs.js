var dateDebut = args.dateDebut != null ? utils.sqlDate(args.dateDebut) : utils.sqlDate(utils.getStartOfYear());
var dateFin = args.dateFin != null ? utils.sqlDate(args.dateFin) : utils.sqlDate(utils.getEndOfYear());

var query = "SELECT DISTINCT g.id AS \"ID\",g.nom AS \"GROUPE\",p.nom || ' ' || p.prenom AS \"REFERENT\", s.libelle AS \"STYLE\""
+ " FROM groupe g JOIN stylemus s ON (g.style = s.id)"
+ " JOIN planning pl ON pl.idper = g.id"
+ " JOIN personne p ON (g.referent = p.id)"
+ " WHERE pl.ptype = 3"
+ " AND pl.jour BETWEEN '" + dateDebut + "' AND '" + dateFin + "'"
+ " AND g.id > 0"
+ " ORDER BY s.libelle";

utils.print(query);
out.resultSet(dc.executeQuery(query));
