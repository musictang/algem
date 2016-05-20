/* 
 * @(#)cours-a-rattraper.sql 2.9.4.0 20/05/2016
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
 * 
 * This file is part of Algem.
 * Algem is free software: you can redistribute it AND/or modify it
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 * 
 */
/**
 * Author:  <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * Created: 20 mai 2016
 */
-- cours à rattraper par professeur
SELECT DISTINCT p1.nom AS "Nom",p1.prenom AS "Prénom",p.jour AS "Date",pl.debut AS "Début",pl.fin AS "Fin",
CASE 
WHEN c.code = 12 THEN 'Stage'
WHEN c.collectif = false OR (c.code = 1 AND (pl.debut = p.debut AND pl.fin = p.fin)) THEN 'Individuel'
ELSE 'Collectif' END AS "Type",
(pl.fin - pl.debut) AS "Durée",
CASE WHEN c.code = 1 AND (c.collectif = false OR (pl.debut = p.debut AND pl.fin = p.fin)) THEN p2.nom || ' ' || p2.prenom ELSE '' END AS "Elève"
FROM planning p JOIN plage pl ON (p.id = pl.idplanning)
JOIN personne p1 ON (p.idper = p1.id)
JOIN personne p2 ON (pl.adherent = p2.id)
JOIN action a ON (p.action = a.id)
JOIN cours c ON (a.cours = c.id)
JOIN salle s ON (p.lieux = s.id)
WHERE p.idper = 106 --774
AND p.jour BETWEEN '14-09-2015' AND '30-06-2016'
AND s.nom like 'RATTRAP%'
ORDER BY p1.nom,p1.prenom,p.jour;
