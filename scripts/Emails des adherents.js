var dateDebut = utils.sqlDate(args.dateDebut);
var dateFin = utils.sqlDate(args.dateFin);

var query = "SELECT DISTINCT e.email FROM email e WHERE e.idper IN (SELECT DISTINCT p.id FROM personne p, eleve e, echeancier2 c"
+ " WHERE e.idper = c.adherent "
+ " AND c.echeance >= '" + dateDebut + "' AND c.echeance < '" + dateFin + "'"
+ " AND c.ecole = 0"
+ " AND (p.id = c.adherent OR p.id = c.payeur))";

utils.print(query);
out.resultSet(dc.executeQuery(query));
