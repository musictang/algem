var query = "SELECT DISTINCT p.id AS \"ID\",p.nom || ' ' || p.prenom AS \"NOM\", i.type || ' ' || i.marque || ' ' || i.identification AS \"INSTRUMENT\", l.debut AS \"DATE\", e.email AS \"EMAIL\""
        + " FROM personne p "
        + " LEFT JOIN email e ON (p.id = e.idper AND e.idx <=0)"
        + " LEFT JOIN location  l ON (p.id = l.adherent)"
        + " JOIN objetalouer i ON (l.objet = i.id)"
        + " WHERE l.fin IS NULL"
        + " ORDER BY \"NOM\"";

utils.print(query);
out.setQuery(query);
