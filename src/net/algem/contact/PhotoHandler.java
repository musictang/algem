/*
 * @(#) PhotoHandler.java Algem 2.9.4.14 09/12/2015
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

package net.algem.contact;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import net.algem.util.model.DataException;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.14
 * @since 2.9.4.14 09/12/2015
 */
public interface PhotoHandler {

  public int importFilesFromDir(File dir);

  public BufferedImage load(int idper);

  public BufferedImage save(int idper, File file) throws DataException;

  public BufferedImage resize(BufferedImage img);



}
