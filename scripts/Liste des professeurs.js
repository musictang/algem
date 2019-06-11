var dateDebut = args.dateDebut != null ? utils.sqlDate(args.dateDebut) : utils.sqlDate(utils.getStartOfYear());
var dateFin = args.dateFin != null ? utils.sqlDate(args.dateFin) : utils.sqlDate(utils.getEndOfYear());
var onPlan = args.onPlan;

if (args.onPlan) {
    var query = "SELECT DISTINCT p.id AS \"Id\",p.nom || ' ' || p.prenom AS \"NOM\", CASE WHEN pr.actif = true THEN 'OUI' ELSE 'NON' END AS \"ACTIF\",i.nom AS \"INSTRUMENT\",e.email AS \"EMAIL\",t.numero AS \"PORTABLE\""
        + " FROM prof pr JOIN personne p ON (pr.idper = p.id)"
        + " LEFT JOIN email e ON (p.id = e.idper AND e.idx <=0)"
        + " LEFT JOIN telephone t ON (p.id = t.idper AND t.typetel = 8)"
        + " LEFT JOIN person_instrument pi ON (pr.idper = pi.idper)"
        + " JOIN instrument i ON (pi.instrument = i.id)"
        + " JOIN planning pl ON (pr.idper = pl.idper)"        
        + " WHERE pl.ptype IN(1,5,6)"
        + " AND pl.jour BETWEEN '" + dateDebut + "' AND '" + dateFin + "'"
        + " AND pl.idper > 0"
        + " AND pi.ptype = 2 AND pi.idx = 0"
        + " ORDER BY \"NOM\"";
}
else {
    var query = "SELECT DISTINCT p.id AS \"ID\",p.nom || ' ' || p.prenom AS \"NOM\", CASE WHEN pr.actif = true THEN 'OUI' ELSE 'NON' END AS \"ACTIF\",i.nom AS \"INSTRUMENT\",e.email AS \"EMAIL\",t.numero AS \"PORTABLE\""
        + " FROM prof pr JOIN personne p ON (pr.idper = p.id)"
        + " LEFT JOIN email e ON (p.id = e.idper AND e.idx <=0)"
        + " LEFT JOIN telephone t ON (p.id = t.idper AND t.typetel = 8)"
        + " LEFT JOIN person_instrument pi ON (pr.idper = pi.idper)"
        + " JOIN instrument i ON (pi.instrument = i.id)"
        + " WHERE pi.ptype = 2 AND pi.idx = 0 AND p.id > 0"
        + " ORDER BY \"INSTRUMENT\",\"NOM\"";
}

utils.print(query);
out.setQuery(query);
