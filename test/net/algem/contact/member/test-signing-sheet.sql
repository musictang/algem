/*
 * @(#) test-signing-sheet.sql Algem 2.15.6 29/11/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
 * Created: 29 nov. 2017
 */

-- heures de repet individuelles
SELECT p.id, p.jour, p.debut,p.fin,p.action
FROM planning p
WHERE p.ptype = 4 -- member
AND p.jour BETWEEN '01-11-2017' AND '30-11-2017'
AND p.idper = 96
ORDER BY p.jour;

SELECT sum(p.fin-p.debut),p.jour
FROM planning p
WHERE p.ptype = 4 -- member
AND p.jour BETWEEN '01-11-2017' AND '30-11-2017'
AND p.idper = 96
GROUP BY p.jour
ORDER BY p.jour;


-- SELECT pg.id, pg.idplanning, pg.debut, pg.fin, pg.adherent, pg.note
-- FROM plage pg JOIN planning p ON pg.idplanning = p.id
-- WHERE p.ptype = 3
-- AND p.jour BETWEEN '01-11-2017' AND '30-11-2017'
-- AND pg.adherent = 96;

-- heures de repet groupe
SELECT p.id, p.jour, p.debut,p.fin,p.action
FROM planning p JOIN groupe g ON p.idper = g.id JOIN groupe_det gd ON gd.id = g.id
WHERE p.ptype = 3 -- GROUP
AND p.jour BETWEEN '01-11-2017' AND '30-11-2017'
AND gd.musicien = 96
ORDER BY p.jour;