/*
 * @(#) TestXmltoPdf.java Algem 2.15.0 01/09/2017
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
package net.algem.enrolment;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ElementTags;
import com.lowagie.text.Font;
import com.lowagie.text.html.Markup;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.xml.XmlParser;
import com.lowagie.text.xml.XmlPeer;
import com.sun.org.apache.xerces.internal.parsers.XMLParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 */
public class TestXmltoPdf
{
  
  public TestXmltoPdf() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }
  
  @Test
  public void testFill() throws DocumentException, FileNotFoundException {
    File xml = new File("/tmp/contrat.xml");
      Document document = new Document();
      PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("/tmp/contrat.pdf"));    
      XmlParser.parse(document,xml.getPath(),getTagMap());
      assertTrue(1==1);
  }
  
  private HashMap<String,XmlPeer> getTagMap() {
    HashMap<String,XmlPeer> tagMap = new HashMap<>();
Font largeBold = new Font(Font.COURIER, 32, Font.BOLD);
      XmlPeer peer = new XmlPeer(ElementTags.ITEXT, "contrat");
      tagMap.put(peer.getAlias(), peer);
      
      peer = new XmlPeer(ElementTags.CHUNK, "student_name");
      peer.addValue(ElementTags.COLOR, "#FF0000");
      peer.addValue(ElementTags.SIZE, "10");
      
      peer.setContent("Anthony ALBERT");
      tagMap.put(peer.getAlias(), peer);
      
      peer = new XmlPeer(ElementTags.CHUNK, "birthdate");
      peer.addValue(ElementTags.SIZE, "14");
      peer.addValue(ElementTags.STYLE, Markup.CSS_KEY_FONTFAMILY);
      peer.addValue(Markup.CSS_KEY_FONTFAMILY, "serif");
      peer.addValue(ElementTags.STYLE, Markup.CSS_VALUE_BOLD);
      peer.setContent("15/04/1992");
      tagMap.put(peer.getAlias(), peer);
      
      peer = new XmlPeer(ElementTags.CHUNK, "student_address");
      peer.setContent("86 rue Dejean Castaing - 33470 GUJAN MESTRAS");
      tagMap.put(peer.getAlias(), peer);
      
      peer = new XmlPeer(ElementTags.CHUNK, "org_name");
      peer.setContent("CIAM");
      tagMap.put(peer.getAlias(), peer);
      
      peer = new XmlPeer(ElementTags.CHUNK, "org_ref");
      peer.setContent("Stéphane ALAUX");
      tagMap.put(peer.getAlias(), peer);
      
      peer = new XmlPeer(ElementTags.CHUNK, "org_address");
      peer.setContent("35 rue Leyteire – 33000 BORDEAUX");
      tagMap.put(peer.getAlias(), peer);
      
      peer = new XmlPeer(ElementTags.CHUNK, "org_tel");
      peer.setContent("305 56 91 26 65");
      tagMap.put(peer.getAlias(), peer);
      
      peer = new XmlPeer(ElementTags.CHUNK, "org_mail");
      peer.setContent("info@le-ciam.com");
      tagMap.put(peer.getAlias(), peer);
      
      peer = new XmlPeer(ElementTags.CHUNK, "fp_number");
      peer.setContent("72 33 00947 33");
      tagMap.put(peer.getAlias(), peer);
      
      peer = new XmlPeer(ElementTags.CHUNK, "siret");
      peer.setContent("334 008 653 00027");
      tagMap.put(peer.getAlias(), peer);
      
      peer = new XmlPeer(ElementTags.CHUNK, "ape");
      peer.setContent("8552Z");
      tagMap.put(peer.getAlias(), peer);
      
      return tagMap;
  }
}
