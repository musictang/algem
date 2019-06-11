var dateDebut = args.dateDebut != null ? utils.sqlDate(args.dateDebut) : utils.sqlDate(utils.getStartOfYear());
var dateFin = args.dateFin != null ? utils.sqlDate(args.dateFin) : utils.sqlDate(utils.getEndOfYear());

var query = "SELECT DISTINCT p.id AS \"Id\", p.nom || ' ' || p.prenom AS \"NOM\","
+ " COALESCE(m1.email, m2.email) AS \"EMAIL\""
+ " FROM eleve e LEFT JOIN email m1 ON (e.idper = m1.idper)"
+ " LEFT JOIN email m2 ON (e.payeur = m2.idper)"
+ " JOIN personne p ON (e.idper = p.id)"
+ " JOIN echeancier2 c ON (e.idper = c.adherent)"
+ " WHERE c.echeance BETWEEN '" + dateDebut + "' AND '" + dateFin + "'"
+ " ORDER BY \"NOM\"";

utils.print(query);
out.setQuery(query);
