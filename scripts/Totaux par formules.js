var dateDebut = args.debut != null ? utils.sqlDate(args.debut) : utils.sqlDate(utils.getStartOfYear());
var dateFin = args.fin != null ? utils.sqlDate(args.fin) : utils.sqlDate(utils.getEndOfYear());

var query = "SELECT m.titre AS \"FORMULE\", (sum(montant) / 100.00)::numeric(10,2) AS \"TOTAL\" FROM echeancier2 e JOIN commande c ON e.commande = c.id"
+ " JOIN commande_module cm on c.id = cm.idcmd  JOIN module m on cm.module = m.id"
+ " WHERE e.echeance BETWEEN '"+dateDebut+"' AND '"+dateFin+"'"
+ " AND e.montant > 0"
+ " AND e.reglement <> 'FAC'"
+ " AND e.compte not in (19,20)"
+ " GROUP BY m.titre ORDER BY m.titre";

utils.print(query);
out.setQuery(query);
