/*
 * @(#) TestPostitModule.java Algem 2.15.11 09/10/2018
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
package net.algem.util.postit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.algem.planning.DateFr;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.11
 * @since 2.15.10 09/10/2018
 */
public class TestPostitModule {

  @Test
  public void testEmptyPostits() {
    int userId = 1234;
    int read = 0;
    List<Postit> postits = new ArrayList<>();

//   when(userService.getPostits(userId, read)).thenReturn(postits);
    PostitModule postitModule = new PostitModule("Test");
    postitModule.loadPostits(filter(postits, userId, read));
    int size = postitModule.getPostitsFromCanvas().size();
    int expected = 0;

    assertTrue("postits.size = " + size, expected == size);
    assertTrue(postitModule.getPostitsFromCanvas().size() == postits.size());
  }

  @Test
  public void testNonEmptyPostits() {
    int userId = 1234;
    int read = 0;
    List<Postit> postits = new ArrayList<>();

    postits.add(createPostit("One", 1, userId, 0, 0));
    postits.add(createPostit("Two", 2, userId, 0, 0));

    PostitModule postitModule = new PostitModule("Test");
    postitModule.loadPostits(filter(postits, userId, read));
    int size = postitModule.getPostitsFromCanvas().size();
    int expected = 2;

    assertTrue("postits.size = " + size, expected == size);
    assertTrue(postitModule.getPostitsFromCanvas().size() == postits.size());
  }

  @Test
  public void testPrivatePostit() {
    int userId = 1234;
    int issuer = 1000;
    int receiver = 1400;
    int read = 0;
    List<Postit> postits = new ArrayList<>();

    postits.add(createPostit("One", 1, issuer, receiver, 0));// this postit should not be read
    postits.add(createPostit("Two", 2, userId, 0, 0));

    PostitModule postitModule = new PostitModule("Test");
    postitModule.loadPostits(filter(postits, userId, read));
    int size = postitModule.getPostitsFromCanvas().size();
    int expected = 1;
    
    assertTrue("postits.size = " + size, expected == size);
    assertTrue(postitModule.getPostitsFromCanvas().size() == size);
  }

  /**
   * Simulates database real request.
   * @param postits all original postits
   * @param userId current user id
   * @param lastRead last postit read
   * @return a list of readable postits
   */
  private List<Postit> filter(List<Postit> postits, int userId, int lastRead){
    // WHERE (dest = 0 OR emet = " + userId + " OR dest = " + userId + ") AND id > " + read;
    List<Postit> readables = new ArrayList<>();
    for (Postit p: postits) {
      if ((p.getReceiver() == 0
        || p.getIssuer() == userId
        || p.getReceiver() == userId)
        && p.getId() > lastRead) {
        readables.add(p);
      }
    }
    return readables;
  }

  private Postit createPostit(String text, int id, int issuer, int receiver, int type) {
    Postit p = new Postit(text);
    Date now = new Date();
    p.setDay(new DateFr(now));
    p.setTerm(new DateFr(now));
    p.setId(id);
    p.setIssuer(issuer);
    p.setReceiver(receiver);
    p.setType(type);

    return p;
  }


}
