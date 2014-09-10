/*
 * @(#)HtmlEditor.java 2.8.w 10/09/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util.help;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * JEditorPane html extension.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.8.w 10/09/14
 */
public class HtmlEditor 
  extends JEditorPane
{

  public HtmlEditor() {
    this("text/html", null);
  }

  public HtmlEditor(String type, String text) {
    super(type, text);
    HTMLEditorKit kit = new HTMLEditorKit();
    setStyle(kit.getStyleSheet());   
    setEditorKit(kit);
    HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
    setDocument(doc);
  }
  
  private void setStyle(StyleSheet styleSheet) {
    styleSheet.addRule("body {font-family: Arial,sans-serif;}");
//    styleSheet.addRule("h1, h2, h3, h4, h5 {font-family: Arial,sans-serif;}");
    styleSheet.addRule("h1 {margin-top: 0; margin-bottom: 10px; padding:0;font-size: 16px}");
    styleSheet.addRule("h2, h3 {margin-top: 5px; margin-bottom: 6px; padding:0;font-size: 14px}");
    styleSheet.addRule("h3 {font-size: 12px}");
    styleSheet.addRule("p, ul, ol, dl, blockquote, table {margin-top: 0; margin-bottom: 5px; padding:0; font-size: 10px}");
    styleSheet.addRule("dt {margin-top: 0; margin-bottom: 0; padding:0; font-weight: bold}");
    styleSheet.addRule("dd {margin-bottom: 3px}");
  }
 
}
