var prof = args.prof == null ? 0 : args.prof.getId();
var dateDebut = args.debut != null ? utils.sqlDate(args.debut) : utils.sqlDate(utils.getStartOfYear());
var dateFin = args.fin != null ? utils.sqlDate(args.fin) : utils.sqlDate(utils.getEndOfYear());

var query = "SELECT DISTINCT p1.nom AS \"NOM\",p1.prenom AS \"PRENOM\",p.jour AS \"DATE\", to_char(pl.debut,'HH24:MI') AS \"DEBUT\",to_char(pl.fin,'HH24:MI') AS \"FIN\","
+ " CASE" 
+ " WHEN c.code = 12 THEN 'Stage'"
+ " WHEN c.collectif = false OR (c.code = 1 AND (pl.debut = p.debut AND pl.fin = p.fin)) THEN 'Individuel'"
+ " ELSE 'Collectif' END AS \"Type\","
+ " to_char((pl.fin - pl.debut)::time, 'HH24:MI') AS \"Durée\","
+ " CASE WHEN c.code = 1 AND (c.collectif = false OR (pl.debut = p.debut AND pl.fin = p.fin)) THEN p2.nom || ' ' || p2.prenom ELSE '' END AS \"Elève\""
+ " FROM planning p JOIN plage pl ON (p.id = pl.idplanning)"
+ " JOIN personne p1 ON (p.idper = p1.id)"
+ " JOIN personne p2 ON (pl.adherent = p2.id)"
+ " JOIN action a ON (p.action = a.id)"
+ " JOIN cours c ON (a.cours = c.id)"
+ " JOIN salle s ON (p.lieux = s.id)"
+ (prof == 0 ? " WHERE p.idper > 0" : " WHERE p.idper = " + prof)
+ " AND p.ptype IN(1,5,6)" 
+ " AND p.jour BETWEEN '"+dateDebut+"' AND '"+dateFin+"'"
+ " AND s.nom like 'RATTRAP%'"
+ " ORDER BY p1.nom,p1.prenom,p.jour";

utils.print(query);
out.resultSet(dc.executeQuery(query));
