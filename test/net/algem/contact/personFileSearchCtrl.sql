/*
 * @(#) personFileSearchCtrl.sql Algem 2.15.0 29/11/2017
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

DECLARE pc CURSOR FOR
SELECT DISTINCT p.id,p.ptype,p.nom,p.prenom,p.civilite,p.droit_img,p.partenaire,p.pseudo,p.organisation,p.onom,p.oraison
FROM personnevue p
WHERE p.ptype != 6 AND p.ptype != 5
ORDER BY p.nom,p.prenom
