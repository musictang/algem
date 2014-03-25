/*
 * @(#)ScheduleRangeIO.java	1.0.0 11/02/13
 *
 * Copyright (c) 2013 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem Agenda.
 * Algem Agenda is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem Agenda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem Agenda. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.planning;

import net.algem.util.AbstractGemDao;
import org.springframework.stereotype.Repository;

/**
 * IO methods for class {@link net.algem.planning.ScheduleRange}.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.0
 * @since 1.0.0 11/02/13
 */
@Repository
public class ScheduleRangeIO
	extends AbstractGemDao {

	public final static String TABLE = "plage";
	public final static String COLUMNS = "pg.id, pg.idplanning, pg.debut, pg.fin, pg.adherent, pg.note";
	public final static String SEQUENCE = "plage_id_seq";
}
