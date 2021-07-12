var dateDebut = args.debut != null ? utils.sqlDate(args.debut) : utils.sqlDate(utils.getStartOfYear());
var dateFin = args.fin != null ? utils.sqlDate(args.fin) : utils.sqlDate(utils.getEndOfYear());
var update=1;

var query = "update eleve set pratique = pratique+1 where pratique > 0 and idper in (select distinct commande.adh from commande, commande_cours where commande.id=commande_cours.idcmd and commande_cours.code=1 and commande.adh=eleve.idper and commande_cours.datedebut >= '"+dateDebut+"')";

utils.print(query);
out.setQuery(query);
