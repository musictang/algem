var query = "SELECT DISTINCT p.id AS \"ID\",p.nom || ' ' || p.prenom AS \"NOM\", e.email AS \"EMAIL\""
        + " FROM personne p "
        + " LEFT JOIN email e ON (p.id = e.idper AND e.idx <=0)"
        + " LEFT JOIN login  l ON (p.id = l.idper)"
        + " WHERE p.ptype=1 AND p.organisation=0 AND p.id > 1 AND l.idper IS NULL"
        + " ORDER BY \"NOM\"";

utils.print(query);
out.setQuery(query);
