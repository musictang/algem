/* 
 * @(#) scripts.sql Algem 2.11.0 06/10/2016
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
 * Created: 6 oct. 2016
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