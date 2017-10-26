var dateDebut = args.debut != null ? utils.sqlDate(args.debut) : utils.sqlDate(utils.getStartOfYear());
var dateFin = args.fin != null ? utils.sqlDate(args.fin) : utils.sqlDate(utils.getEndOfYear());

var query = "SELECT m.titre as formule,p.id as idper,p.nom,p.prenom,cm.debut as for_debut,cm.fin as for_fin,cm.reglement,cm.paiement,cm.prix,(cm.prix + 400) as total"
+ " FROM commande_module cm JOIN commande c ON cm.idcmd = c.id JOIN module m ON cm.module = m.id JOIN personne p ON c.adh = p.id"
+ " WHERE cm.debut BETWEEN '"+dateDebut+"' AND '"+dateFin+"' ORDER BY m.titre,p.nom";

utils.print(query);
out.setQuery(query);
