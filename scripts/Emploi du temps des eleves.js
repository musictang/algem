var etablissement = args.etablissement;
var dateDebut = utils.sqlDate(args.dateDebut);
var dateFin = utils.sqlDate(args.dateFin);

var query = "SELECT DISTINCT p.nom,p.prenom AS \"NOM\",s.nom AS \"SALLE\",pr.nom || ' ' || pr.prenom AS \"PROF.\",c.titre AS \"COURS\",pg.debut AS \"DEBUT\",pg.fin AS \"FIN\""
+ " FROM personne p, personne pr, planning pl, plage pg, salle s, action a, cours c"
+ " WHERE pl.jour BETWEEN '" + dateDebut + "' AND '" + dateFin + "'"
+ " AND pl.action = a.id"
+ " AND a.cours = c.id"
+ " AND pl.lieux = s.id"
+ " AND s.etablissement = " + etablissement
+ " AND pl.idper = pr.id"
+ " AND pl.id = pg.idplanning"
+ " AND pg.adherent = p.id"
+ " AND pg.adherent > 0"
+ " ORDER BY \"NOM\"";

utils.print(query);
out.resultSet(dc.executeQuery(query));
