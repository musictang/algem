/*
 * @(#)PostitCanvas.java	2.11.5 11/01/17
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
 *
 */
package net.algem.util.postit;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import net.algem.util.ui.GemPanel;

/**
 * Main postit layout.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.5
 */
public class PostitCanvas
        extends GemPanel
        implements MouseListener, MouseMotionListener
{

  private static final Color INTERNAL_PUBLIC_COLOR = Color.YELLOW;
  private static final Color INTERNAL_PRIVATE_COLOR = new Color(230, 255, 68);
  private static final Color EXTERNAL_PUBLIC_COLOR = new Color(254,210,68);//"fed244"
  private static final Color EXTERNAL_PRIVATE_COLOR = new Color(255,123,123);//#ff7b7b
  int nextx = 50;
  int nexty = 40;
  int initialDragPos;
  private Vector<PostitPosition> postits;
  private PostitPosition pick;
  private int clickx;
  private int clicky;
  private Dimension dim;
  private ActionListener actionListener;
  private Font font = new Font("Helvetica",Font.PLAIN,11);

  public PostitCanvas() {
    postits = new Vector<PostitPosition>();
    addMouseListener(this);
    addMouseMotionListener(this);
    dim = new Dimension(200, 400); // espace du canevas
  }

  /**
   * Adds a postit to canvas.
   * Used in {@link net.algem.util.postit.PostitModule}.
   * @param p the postit
   */
  public void add(Postit p) {
    PostitPosition pos = new PostitPosition(nextx, nexty, p);
    postits.addElement(pos);
    /*if ((nextx+=90) > dim.width)
    {
    nextx=90;
    nexty+=50;
    }*/
    if ((nexty += 55) > dim.height) // si espace vertical insuffisant
    {
      nextx = 70; // décalage horizontal
      nexty = 30; // décalage vertical
    }
    repaint();
  }

  /**
   * Deletes a postit from the canvas.
   * @param p postit position
   */
  public void remove(PostitPosition p) {
    postits.removeElement(p);
    repaint();
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }
  static final int MAXW = 100;
  static final int MAXH = 50;

  @Override
  public void paint(Graphics g) {
    dim = getSize();
    g.setColor(getBackground());
    g.fillRect(0, 0, dim.width, dim.height);
    g.setFont(font);
    draw(g);
  }

  /**
   * Draw the postits.
   * @param g
   */
  public void draw(Graphics g) {
    int x = 10;
    int y = 10;
    FontMetrics fm = g.getFontMetrics();
//		int h = fm.getHeight() + 4;
    int h = MAXH;

    Postit p;
    Enumeration enu = postits.elements();
    while (enu.hasMoreElements()) {
      PostitPosition pp = (PostitPosition) enu.nextElement();
      x = pp.getX();
      y = pp.getY();
      p = pp.getPostit();

      int w = MAXW;
      // background color
      switch(p.getType()) {
        case Postit.INTERNAL:
          g.setColor(p.getReceiver() > 0 ? INTERNAL_PRIVATE_COLOR : INTERNAL_PUBLIC_COLOR);
          break;
        case Postit.EXTERNAL:
          g.setColor(p.getReceiver() > 0 ? EXTERNAL_PRIVATE_COLOR : EXTERNAL_PUBLIC_COLOR);
          break;
        default:
          g.setColor(INTERNAL_PUBLIC_COLOR);

      }
      g.fillRoundRect(x - w / 2, y - h / 2, w, h, 10, 10);
      // border
      if (p.getType() != Postit.INTERNAL_URGENT) {
        g.setColor(Color.black);
        g.drawRoundRect(x - w / 2, y - h / 2, w - 1, h - 1, 10, 10);
      } else {
        g.setColor(Color.RED);
        g.drawRoundRect(x - w / 2, y - h / 2, w - 1, h - 1, 10, 10);
      }

      //text
      g.setColor(Color.black);
      StringTokenizer tk = new StringTokenizer(p.getText());
      int pos = 0;
      String msg = "";
      while (tk.hasMoreElements()) {
        String s = tk.nextToken();
        int n = fm.stringWidth(s) + 4;
        if (pos + n < MAXW) {
          msg += s + " ";
          pos += n;
        } else // si depassement largeur
        {
          g.drawString(msg, x - (w - 10) / 2, (y - 10));
          msg = s + " ";
          pos = n;
          y += 10; // decalage vertical (anciennement 10)
          if (y - pp.getY() >= MAXH - 10) {
            break;
          }
        }
      }
      if (y - pp.getY() < MAXH - 10 && msg.length() > 0) {
        g.drawString(msg, x - (w - 10) / 2, (y - 10));
      }
    }
  }

	@Override
  public void mouseEntered(MouseEvent e) {
  }

	@Override
  public void mouseExited(MouseEvent e) {
  }

	@Override
  public void mousePressed(MouseEvent e) {
    if (postits.size() < 1) {
      return;
    }
    int x = e.getX();
    int y = e.getY();

    double bestdist = Double.MAX_VALUE;
    for (int i = 0; i < postits.size(); i++) {
      PostitPosition p = (PostitPosition) postits.elementAt(i);
      double dist = (p.getX() - x) * (p.getX() - x) + (p.getY() - y) * (p.getY() - y);
      if (dist < bestdist) {
        pick = p;
        bestdist = dist;
      }
    }
    if (pick != null) {
      int dx = (x > pick.x ? x - pick.x : pick.x - x);
      int dy = (y > pick.y ? y - pick.y : pick.y - y);
      if (dx < 40 && dy < 30) {
        //pick.x = x;
        //pick.y = y;
      } else {
        pick = null;
      }
    }
    clickx = x;
    clicky = y;
  }

	@Override
  public void mouseReleased(MouseEvent e) {
    /*		if (clickx == e.getX() && clicky == e.getY())
    {
    Postit p = pick.getPostit();
    if (actionListener != null)
    actionListener.actionPerformed(new ActionEvent(pick,ActionEvent.ACTION_PERFORMED,"postit"));
    } */
  }

	@Override
  public void mouseClicked(MouseEvent e) {
    if (postits.size() < 1) {
      return;
    }
    if (clickx == e.getX() && clicky == e.getY()) {
      if (pick == null) {
        return;
      }

      if (actionListener != null) {
        actionListener.actionPerformed(new ActionEvent(pick, ActionEvent.ACTION_PERFORMED, "postit"));
      }
    }
  }

	@Override
  public void mouseMoved(MouseEvent e) {
  }

  /**
   * Gestion des déplacements des postits à l'intérieur du module.
   * @param e
   */
	@Override
  public void mouseDragged(MouseEvent e) {
    if (pick != null) {
      pick.x = e.getX();
      if (pick.x < 50) {
        pick.x = 50;
      }
      if (pick.x > PostitModule.POSTIT_SIZE.width) {// largeur de la fenetre du module
        pick.x = PostitModule.POSTIT_SIZE.width - 10;
      }
      pick.y = e.getY();
      if (pick.y < 10) {
        pick.y = 10;
      }
      if (pick.y > PostitModule.POSTIT_SIZE.height) {// hauteur de la fenetre du module
        pick.y = PostitModule.POSTIT_SIZE.height - 50;
      }
      repaint();
    }
  }
}
