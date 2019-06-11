var ageMini = args.ageMini;
var ageMaxi = args.ageMaxi;
var etablissement = args.etablissement;
var dateDebut = utils.sqlDate(args.dateDebut);
var dateFin = utils.sqlDate(args.dateFin);

var query = "SELECT DISTINCT p2.civilite, p2.nom || ' ' || p2.prenom AS \"PAYEUR\", adr1, adr2, cdp, ville, p1.nom || ' ' || p1.prenom AS \"ELEVE\""
+ " FROM personne p1, personne p2, plage pg, planning p, action a, cours c, eleve e, adresse adr, salle s"
+ " WHERE p.jour BETWEEN '" + dateDebut + "' AND '" + dateFin + "'"
+ " AND p.ptype = 1"
+ " AND p.lieux = s.id";
if (etablissement != null) {
query += " AND s.etablissement = " + etablissement
}
query +=  " AND p.action = a.id"
+ " AND a.cours NOT IN (36,314,520)"
+ " AND a.cours = c.id"
+ " AND c.code = 1"
+ " AND pg.idplanning = p.id"
+ " AND pg.adherent = e.idper"
+ " AND e.idper = p1.id"
+ " AND e.payeur = p2.id";
if (ageMini != null && ageMaxi != null) {
query += " AND (extract('year' from age(e.datenais)) >= " + ageMini + " AND extract('year' from age(e.datenais)) < " + ageMaxi + ")";
}
query += " AND p2.id = adr.idper"
+ " ORDER BY \"PAYEUR\"";

utils.print(query);
out.setQuery(query);
