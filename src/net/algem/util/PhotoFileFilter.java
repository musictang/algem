/*
 * @(#) PhotoFileFilter.java Algem 2.13.3 16/05/2017
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

package net.algem.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.3
 * @since 2.13.3 16/05/2017
 */
public class PhotoFileFilter implements FileFilter
  {
    private final Pattern pattern;

    public PhotoFileFilter(int idper) {
      pattern = Pattern.compile("^[a-zA-ZàâäéèêëîïôöùûüÀÂÉÈËÊÎÏÔÙÜ_ \\(\\)-]*" + idper + "\\.(jpg|jpeg|JPG|JPEG|png|PNG)$");
    }

    @Override
    public boolean accept(File pathname) {
      return pattern.matcher(pathname.getName()).matches();
    }
}
