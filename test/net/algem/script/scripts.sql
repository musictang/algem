/* 
 * @(#) scripts.sql Algem 2.11.1 07/10/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
 * Created: 06/10/16
 */

SELECT DISTINCT p.id AS "ID",p.nom || ' ' || p.prenom AS "NOM",
CASE WHEN pr.actif = true THEN 'OUI' ELSE 'NON' END AS "ACTIF",
i.nom AS "INSTRUMENT", e.email AS "EMAIL",t.numero AS "PORTABLE"
FROM prof pr JOIN personne p ON (pr.idper = p.id)
LEFT JOIN email e ON (p.id = e.idper AND e.idx <=0)
LEFT JOIN telephone t ON (p.id = t.idper AND t.typetel = 8)
LEFT JOIN person_instrument pi ON (pr.idper = pi.idper)
JOIN instrument i ON (pi.instrument = i.id)
WHERE pi.ptype = 2 AND pi.idx = 0
ORDER BY "INSTRUMENT","NOM";
-- AND e.idx <= 0
-- AND t.typetel = 8

-- emails adherents
SELECT DISTINCT p.nom || ' ' || p.prenom AS "NOM",
COALESCE(m1.email, m2.email) AS "EMAIL"
FROM eleve e LEFT JOIN email m1 ON (e.idper = m1.idper)
LEFT JOIN email m2 ON (e.payeur = m2.idper)
JOIN personne p ON (e.idper = p.id)
JOIN echeancier2 c ON (e.idper = c.adherent)
WHERE c.echeance BETWEEN '2016-09-19' AND '2017-07-01'
ORDER BY "NOM";

-- export prelevements jav
SELECT e.* FROM echeancier2 e
join personne p1 on (e.payeur = p1.id)
join personne p2 on (e.adherent = p2.id)
join compte c on (e.compte = c.id)
join analytique a on (e.analytique = a.id)

WHERE echeance >= '15-11-2016' AND echeance <= ''15-11-2016'
-- AND ecole = ' + school
-- AND paye = 't' AND transfert = 'f' AND reglement = 'PRL'

-- erreurs prelevements musichalle