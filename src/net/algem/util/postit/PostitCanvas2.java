/*
 * @(#)PostitCanvs.java	3.0.0 10/09/2021
 *
 * Copyright (c) 2021 eric@productionlibre.fr. All Rights Reserved.
 * Copyright (c) 1999-2021 Musiques Tangentes. All Rights Reserved./*
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
 *
 */
package net.algem.util.postit;

import java.awt.*;
import java.util.Iterator;
import java.util.StringTokenizer;
import net.algem.util.GemLogger;

/**
 * Main postit layout.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.11
 */
public class PostitCanvas2
        extends PostitCanvas
{

  public PostitCanvas2() {
      super();
      nextx = 110;
      nexty = MAXH / 2;
      dim = new Dimension(800, 110); // espace du canevas !!
  }

  /**
   * Adds a postit to canvas.
   * Used in {@link net.algem.util.postit.PostitModule}.
   * @param p the postit
   */
  @Override
  public void add(Postit p) {
      //GemLogger.info("PostitCanvas2.add postit p:"+p);
      //GemLogger.info("PostitCanvas2.add nexty:"+nexty+" netx:"+nextx);
    if (nextx > dim.width) { // si le bout est atteint
      // retour debut de panneau
      nextx = 30 + xOffset; // décalage horizontal
      nexty = 70 + yOffset; // décalage vertical
      xOffset += 20;
      yOffset += 10;
    }
    PostitPosition pos = new PostitPosition(nextx, nexty, p);
      //GemLogger.info("PostitCanvas2.add postit pos x:"+pos.getX()+" y="+pos.getY());
    postits.add(pos);
    nextx += 115;
    repaint();
  }


  /**
   * Draw the postits.
   * @param g
   */
@Override
  public void draw(Graphics g) {
//      GemLogger.info("PostitCanvas2.draw");
    int x = 10;
    int y = 10;
    FontMetrics fm = g.getFontMetrics();
//		int h = fm.getHeight() + 4;
    int h = MAXH;

    Iterator<PostitPosition> enu = postits.iterator();
    while (enu.hasNext()) {
      PostitPosition pp = enu.next();
      x = pp.getX();
      y = pp.getY();
      Postit p = pp.getPostit();
      //GemLogger.info("PostitCanvas2.draw p="+p+" x="+x+" y="+y);
      //TODOERIC 
      /*
      if (x == 50) {
        x = getWidth() / 2;// LAF adaptation
      }
      */
      int w = MAXW;
      //TODOERIC int w = getWidth();// LAF adaptation
      //GemLogger.info("PostitCanvas.draw x="+x+" w="+w);
      // background color
      switch(p.getType()) {
        case Postit.INTERNAL:
          g.setColor(p.getReceiver() > 0 ? INTERNAL_PRIVATE_COLOR : INTERNAL_PUBLIC_COLOR);
          break;
        case Postit.EXTERNAL:
          g.setColor(p.getReceiver() > 0 ? EXTERNAL_PRIVATE_COLOR : EXTERNAL_PUBLIC_COLOR);
          break;
        case Postit.BOOKING:
          g.setColor(BOOKING_COLOR);
          break;
        default:
          g.setColor(INTERNAL_PUBLIC_COLOR);

      }
      g.fillRoundRect(x - w / 2, y - h / 2, w, h, 4, 4);

      // border
      if (p.getType() != Postit.INTERNAL_URGENT) {
        g.setColor(Color.black);
        g.drawRoundRect(x - w / 2, y - h / 2, w - 1, h - 1, 4, 4);
      } else {
        g.setColor(Color.RED);
        g.drawRoundRect(x - w / 2, y - h / 2, w - 1, h - 1, 4, 4);
      }

      //text
      g.setColor(Postit.BOOKING == p.getType() ? Color.WHITE : Color.BLACK);

      StringTokenizer tk = new StringTokenizer(p.getText());
      int pos = 0;
      String msg = "";
      while (tk.hasMoreElements()) {
        String s = tk.nextToken();
        int n = fm.stringWidth(s) + 4;
        if (n > MAXW && s.length() > 14) {
          s = s.substring(0,14).concat("...");
        }
        if (pos + n < MAXW) {
          msg += s + " ";
          pos += n;
        } else // si depassement largeur
        {
          //g.drawString(msg, x - (w - 10) / 2, (y - 10));
          g.drawString(msg, x - (w - 6) / 2, (y - 14));
          msg = s + " ";
          pos = n;
          y += 10; // decalage vertical
          if (y - pp.getY() >= MAXH - 10) {
            break;
          }
        }
      }
      if (y - pp.getY() < MAXH - 10 && msg.length() > 0) {
        //g.drawString(msg, x - (w - 10) / 2, (y - 10));
        g.drawString(msg, x - (w - 6) / 2, (y - 14));
      } else {
        g.drawString("...", x - (w - 6) / 2, (y - 18));
      }
    }
  }


}
