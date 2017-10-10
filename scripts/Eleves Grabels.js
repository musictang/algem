var dateDebut = args.dateDebut != null ? utils.sqlDate(args.dateDebut) : utils.sqlDate(utils.getStartOfYear());
var dateFin = args.dateFin != null ? utils.sqlDate(args.dateFin) : utils.sqlDate(utils.getEndOfYear());

var query = "SELECT DISTINCT p1.id AS \"Id\",p1.nom || ' ' || p1.prenom AS \"ELEVE\","
+ " to_char(e.datenais, 'dd-mm-yyyy') AS \"DATE NAIS.\","
+ " COALESCE(m1.email, m2.email) AS \"EMAIL\","
+ " c.titre AS \"COURS\","
+ " s.nom AS \"SALLE\","
+ " pg.debut AS \"DEBUT\","
+ " pg.fin AS \"FIN\","
+ " p2.nom || ' ' || p2.prenom AS \"PROF\""
+ " FROM personne p1 JOIN eleve e ON (p1.id = e.idper)"
+ " LEFT JOIN email m1 ON (e.idper = m1.idper AND m1.idx = 0)"
+ " LEFT JOIN email m2 ON (e.payeur = m2.idper AND m2.idx = 0)"
+ " JOIN plage pg ON (e.idper = pg.adherent)"
+ " JOIN planning pl ON (pg.idplanning = pl.id)"
+ " JOIN personne p2 ON (pl.idper = p2.id)"
+ " JOIN salle s ON (pl.lieux = s.id)"
+ " JOIN action a ON (pl.action = a.id)"
+ " JOIN cours c ON (a.cours = c.id)"
+ " WHERE pl.jour BETWEEN '" + dateDebut + "' AND '" + dateFin + "'"
+ " ORDER BY \"ELEVE\", pg.debut";

utils.print(query);
out.setQuery(query);
