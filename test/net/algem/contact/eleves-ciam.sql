/*
 * @(#) eleves-ciam.sql Algem 2.15.9 25/09/2018
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem.
 * Algem is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * Author:  <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * Created: 25 sept. 2018
 */

-- var dateDebut = args.dateDebut != null ? utils.sqlDate(args.dateDebut) : utils.sqlDate(utils.getStartOfYear());
-- var dateFin = args.dateFin != null ? utils.sqlDate(args.dateFin) : utils.sqlDate(utils.getEndOfYear());

var query = " SELECT DISTINCT p1.id AS \"Id\", p1.civilite AS \"Civilité\", p1.nom || ' ' || p1.prenom AS \"ELEVE\","
+ " to_char(e.datenais, 'dd-mm-yyyy') AS \"DATE NAIS.\",i.nom AS \"INSTRUMENT\","
+ " COALESCE(m1.cdp, m2.cdp) AS \"CP\""
+ " FROM personne p1 JOIN eleve e ON (p1.id = e.idper)"
+ " LEFT JOIN person_instrument pi ON (e.idper = pi.idper AND pi.ptype = 1)"
+ " LEFT JOIN instrument i ON (pi.instrument = i.id)"
+ " LEFT JOIN adresse m1 ON (e.idper = m1.idper)"
+ " LEFT JOIN adresse m2 ON (e.payeur = m2.idper)"
+ " JOIN plage pg ON (e.idper = pg.adherent)"
+ " JOIN planning pl ON (pg.idplanning = pl.id)"
+ " JOIN personne p2 ON (pl.idper = p2.id)"
+ " WHERE pl.jour BETWEEN '" + dateDebut + "' AND '" + dateFin + "'"
+ " ORDER BY \"CP\",\"ELEVE\";"
-- utils.print(query);
-- out.setQuery(query);

SELECT DISTINCT p1.id AS "Id", p1.civilite AS "Civilité",
       CASE p1.civilite WHEN 'M' THEN 'Homme'
              WHEN 'Mme' THEN 'Femme'
              WHEN 'Mlle' THEN 'Femme'
              ELSE ''
       END AS "Civilité", p1.nom || ' ' || p1.prenom AS "ELEVE",
to_char(e.datenais, 'dd-mm-yyyy') AS "DATE NAIS.",i.nom AS "INSTRUMENT",
COALESCE(m1.cdp, m2.cdp) AS "CP"
FROM personne p1 JOIN eleve e ON (p1.id = e.idper)
LEFT JOIN person_instrument pi ON (e.idper = pi.idper AND pi.ptype = 1)
LEFT JOIN instrument i ON (pi.instrument = i.id)
LEFT JOIN adresse m1 ON (e.idper = m1.idper)
LEFT JOIN adresse m2 ON (e.payeur = m2.idper)
JOIN plage pg ON (e.idper = pg.adherent)
JOIN planning pl ON (pg.idplanning = pl.id)
JOIN personne p2 ON (pl.idper = p2.id)
WHERE pl.jour BETWEEN '01-09-2017' AND '30-06-2018'
ORDER BY "CP","ELEVE";
