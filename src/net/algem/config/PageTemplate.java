/*
 * @(#) PageModel.java Algem 2.15.0 19/07/2017
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

package net.algem.config;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 19/07/2017
 */
public class PageTemplate {

  public final static short DEFAULT_MODEL = 0;
  public final static short QUOTE_PAGE_MODEL = 1;
  public final static short AGREEMENT_PAGE_MODEL = 2;
  public final static short CONTRACT_PAGE_MODEL = 3;
  private short type;
  private byte[] content;

  public PageTemplate(short type, byte[] content) {
    this.type = type;
    this.content = content;
  }

  public short getType() {
    return type;
  }

  public void setType(short type) {
    this.type = type;
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }


}
