/*
 * @(#)HtmlViewer.java	2.6.a 25/09/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class HtmlViewer
        extends JFrame
        implements HyperlinkListener
{

  JEditorPane html;
  Stack pile;
  URL current;

  public HtmlViewer(String urlPage) {
    super("Documentation HTML Mustang");
    pile = new Stack();

    try {
      URL url = new URL(urlPage);
      html = new JEditorPane(url);
      current = url;
      html.setEditable(false);
      html.addHyperlinkListener(this);
    } catch (MalformedURLException e) {
      System.out.println("Malformed URL: " + e);
    } catch (IOException e) {
      System.out.println("IOException: " + e);
    }


    JScrollPane scroller = new JScrollPane();
    //scroller.setBorder(swing.loweredBorder);
    JViewport vp = scroller.getViewport();
    vp.add(html);
    vp.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);


    JButton back = new JButton("Retour");
    back.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent e) {
        try {
          URL u = (URL) pile.pop();
          linkActivated(u);
        } catch (Exception ignore) {
        };
      }
    });

    JButton index = new JButton("Index");
    index.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent e) {
        try {
          URL u = new URL("http://localhost/~eric/mustang/indexaide.html");
          linkActivated(u);
        } catch (Exception ignore) {
        };
      }
    });

    JButton cherche = new JButton("Recherche");
    cherche.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent e) {
        try {
          URL u = new URL("frame de recherche");
          linkActivated(u);
        } catch (Exception ignore) {
        };
      }
    });

    JButton table = new JButton("Table des Matières");
    table.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent e) {
        try {
          URL u = new URL("http://localhost/~eric/mustang/index.html");
          linkActivated(u);
        } catch (Exception ignore) {
        };
      }
    });

    getContentPane().add(scroller, BorderLayout.CENTER);
    JPanel p = new JPanel();
    p.add(back);
    p.add(table);
    p.add(index);
    p.add(cherche);
    getContentPane().add(p, BorderLayout.NORTH);

    setSize(650, 700);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }

  @Override
  public void hyperlinkUpdate(HyperlinkEvent e) {
    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      URL u = e.getURL();
      pile.push(current);
      linkActivated(e.getURL());
    }
  }

  public void linkActivated(URL u) {
    Cursor c = html.getCursor();
    Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    html.setCursor(waitCursor);
    current = u;
    SwingUtilities.invokeLater(new PageLoader(u, c));
  }

  class PageLoader
          implements Runnable
  {

    URL url;
    Cursor cursor;

    PageLoader(URL u, Cursor c) {
      url = u;
      cursor = c;
    }

    public void run() {
      if (url == null) {
        // restore the original cursor
        html.setCursor(cursor);

        Container parent = html.getParent();
        parent.repaint();
      } else {
        Document doc = html.getDocument();
        try {
          html.setPage(url);
        } catch (IOException ioe) {
          html.setDocument(doc);
          getToolkit().beep();
        } finally {
          url = null;
          SwingUtilities.invokeLater(this);
        }
      }
    }
  }

}
