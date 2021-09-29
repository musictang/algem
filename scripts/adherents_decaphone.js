var dateDebut = args.dateDebut != null ? utils.sqlDate(args.dateDebut) : utils.sqlDate(utils.getStartOfYear());
var dateFin = args.dateFin != null ? utils.sqlDate(args.dateFin) : utils.sqlDate(utils.getEndOfYear());

var query = "SELECT DISTINCT p.id AS \"Id\", p.nom || ' ' || p.prenom AS \"NOM\", e.datenais as \"DATE NAISSANCE\", e.pratique as \"PRATIQUE\", i.nom as \"INSTRUMENT\", Cast(p.droit_img as integer) as \"DROIT IMG\","
+ " COALESCE(m1.email, m2.email) AS \"EMAIL1\","
+ " COALESCE(m3.email, m4.email) AS \"EMAIL2\","
+ " COALESCE(t1.numero, t2.numero) AS \"TELEPHONE1\","
+ " COALESCE(t3.numero, t4.numero) AS \"TELEPHONE2\""
+ " FROM eleve e"
+ " LEFT JOIN email m1 ON (e.idper = m1.idper AND m1.idx=0)"
+ " LEFT JOIN email m2 ON (e.payeur = m2.idper AND m2.idx=0)"
+ " LEFT JOIN email m3 ON (e.idper = m3.idper AND m3.idx=1)"
+ " LEFT JOIN email m4 ON (e.payeur = m4.idper AND m4.idx=1)"
+ " LEFT JOIN telephone t1 ON (e.idper = t1.idper AND t1.idx=0)"
+ " LEFT JOIN telephone t2 ON (e.payeur = t2.idper AND t2.idx=0)"
+ " LEFT JOIN telephone t3 ON (e.idper = t3.idper AND t3.idx=1)"
+ " LEFT JOIN telephone t4 ON (e.payeur = t4.idper AND t4.idx=1)"
+ " LEFT JOIN person_instrument pi ON (e.idper = pi.idper AND pi.idx=0)"
+ " LEFT JOIN instrument i ON (pi.instrument = i.id)"
+ " JOIN personne p ON (e.idper = p.id)"
+ " JOIN echeancier2 c ON (e.idper = c.adherent)"
+ " WHERE c.echeance BETWEEN '" + dateDebut + "' AND '" + dateFin + "'"
+ " ORDER BY \"NOM\"";

utils.print(query);
out.setQuery(query);
