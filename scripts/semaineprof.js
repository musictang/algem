var dateDebut = utils.sqlDate(args.dateDebut);
var dateFin = utils.sqlDate(args.dateFin);

var query = "SELECT prof.nom, prof.prenom,p.jour,pl.debut,pl.fin,adh.nom,adh.prenom " +
	"FROM plage pl " +
	"JOIN planning p on pl.idplanning=p.id " +
	"JOIN personne prof on p.idper=prof.id "+
	"JOIN personne adh on adh.id=pl.adherent " +
	"WHERE jour >= '" + dateDebut + "' "+
	"AND   jour <= '" + dateFin + "' "+
	"ORDER by prof.nom,p.jour,pl.debut";


utils.print(query);
out.setQuery(query);
