var dateDebut = utils.sqlDate(args.dateDebut);
var dateFin = utils.sqlDate(args.dateFin);

var query = "SELECT DISTINCT p1.nom || ' ' || p1.prenom AS \"ELEVE\","
+ " to_char(e.datenais, 'dd-mm-yyyy') AS \"DATE NAIS.\","
+ " c.titre AS \"COURS\","
+ " s.nom AS \"SALLE\","
+ " pg.debut AS \"DEBUT\","
+ " pg.fin AS \"FIN\","
+ " p2.nom || ' ' || p2.prenom AS \"PROF\""
+ " FROM personne p1 JOIN eleve e ON (p1.id = e.idper)"
+ " JOIN plage pg ON (e.idper = pg.adherent)"
+ " JOIN planning pl ON (pg.idplanning = pl.id)"
+ " JOIN personne p2 ON (pl.idper = p2.id)"
+ " JOIN salle s ON (pl.lieux = s.id)"
+ " JOIN action a ON (pl.action = a.id)"
+ " JOIN cours c ON (a.cours = c.id)"
+ " WHERE pl.jour BETWEEN '" + dateDebut + "' AND '" + dateFin + "'"
+ " ORDER BY \"ELEVE\", pg.debut";

utils.print(query);
out.resultSet(dc.executeQuery(query));
