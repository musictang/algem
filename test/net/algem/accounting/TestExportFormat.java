/*
 * @(#)TestExportFormat.java	2.15.0 21/07/17
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
package net.algem.accounting;

//import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfReader;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Paragraph;
//
//import com.itextpdf.io.font.FontConstants;
//import com.itextpdf.kernel.color.Color;
//import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
//import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
//import com.itextpdf.kernel.utils.PdfMerger;
//import com.itextpdf.layout.property.TextAlignment;
//import com.itextpdf.layout.property.VerticalAlignment;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfStamper;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.text.NumberFormat;
import java.util.Locale;
import javax.imageio.ImageIO;
import net.algem.util.ImageUtil;

import net.algem.util.TextUtil;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.7.a 17/12/2013
 */
public class TestExportFormat {

  public TestExportFormat() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testFormat() {
    //AccountExportService exportService = new ExportCiel(dc);
    NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
    nf.setGroupingUsed(false);
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
    long m = 15020050L;
    String formatted = TextUtil.padWithLeadingSpaces(nf.format(m / 100.0), 13);
    assertEquals("    150200.50", formatted);
    m = 30015020050L;
    formatted = TextUtil.padWithLeadingSpaces(nf.format(m / 100.0), 13);
    assertEquals(" 300150200.50", formatted);
    m = 153;
    formatted = TextUtil.padWithLeadingSpaces(nf.format(m / 100.0), 13);
    assertEquals("         1.53", formatted);
  }

  @Ignore
  public void testParentPath() {
    String path = "/home/jm/export.txt";
    String dir = new File(path).getParent();
    assertEquals(dir, "/home/jm", dir);
  }

//  @Ignore
//  public void testPdfHeader() throws IOException {
//    final String DEST = "/tmp/Merged.pdf";
//    final String SRC = "/tmp/Right.pdf";
//    PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(DEST));
//    Document doc = new Document(pdfDoc);
//    Paragraph header = new Paragraph("Copy")
//      .setFont(PdfFontFactory.createFont(FontConstants.HELVETICA))
//      .setFontSize(14)
//      .setFontColor(Color.RED);
//    for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
//      float x = pdfDoc.getPage(i).getPageSize().getWidth() / 2;
//      float y = pdfDoc.getPage(i).getPageSize().getTop() - 20;
//      doc.showTextAligned(header.setFontColor(Color.RED), x, y, i,
//        TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
//    }
//    doc.close();
//  }
  
//  @Ignore
//  /**
//   * Test iText 7 surimposition pdf.
//   */
//  public void testPdfMerge() throws IOException {
//
//    final String SRC = "/tmp/memo.pdf";
//    final String DEST = "/tmp/memo-et.pdf";
//    final String MODEL = "/tmp/model.pdf";
//
//    try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(DEST));
//      PdfDocument model = new PdfDocument(new PdfReader(MODEL))) {
//
//      PdfFormXObject page = model.getFirstPage().copyAsFormXObject(pdfDoc);
//      for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
//        PdfCanvas canvas = new PdfCanvas(
//          pdfDoc.getPage(i).newContentStreamBefore(),
//          pdfDoc.getPage(i).getResources(),
//          pdfDoc);
//        canvas.addXObject(page, 0, 0);
//      }
//    }
//  }
  
  /**
   * Test surimposition pdf iText 2.1.
   *
   * @throws IOException
   * @throws DocumentException
   */
  @Ignore
  public void testPdfMerge2() throws IOException, DocumentException {
    final String SRC = "/tmp/memo.pdf";
    final String DEST = "/tmp/memo-et.pdf";
    final String MODEL = "/tmp/model.pdf";
    com.lowagie.text.pdf.PdfReader reader = new com.lowagie.text.pdf.PdfReader(SRC);
    PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(DEST));

    com.lowagie.text.pdf.PdfReader model = new com.lowagie.text.pdf.PdfReader(MODEL);
    PdfImportedPage template = stamper.getImportedPage(model, 1);
    for (int i = 1; i <= reader.getNumberOfPages(); i++) {
      PdfContentByte canvas = stamper.getUnderContent(i);
      canvas.addTemplate(template, 0, 0);
    }

    stamper.getWriter().freeReader(model);
    model.close();
    stamper.close();

  }

  @Test
  public void testPdfPreviewAsImage() throws FileNotFoundException, IOException {
    //  load a pdf from a file

    File file = new File("/tmp/Papier entete 2010.pdf");
    RandomAccessFile raf = new RandomAccessFile(file, "r");
    //ReadableByteChannel ch = Channels.newChannel(new FileInputStream(file));

    FileChannel channel = raf.getChannel();
    MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
    PDFFile pdffile = new PDFFile(buf);
    PDFPage page = pdffile.getPage(1);

    //  create new image
    Rectangle rect = new Rectangle(0, 0, (int) (page.getBBox().getWidth()), (int) (page.getBBox().getHeight()));

    Image img = page.getImage(
      rect.width, rect.height, //width & height
      rect, // clip rect
      null, // null for the ImageObserver
      true, // fill background with white
      true // block until drawing is done
    );

    BufferedImage bufferedImage = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
    Graphics g = bufferedImage.createGraphics();
    g.drawImage(img, 0, 0, null);
    g.dispose();

    BufferedImage scaled = rescale(bufferedImage, rect.width/4, rect.height/4);
    File asd = new File("/tmp/testmemo.png");
    if (asd.exists()) {
      asd.delete();
    }
    ImageIO.write(scaled, "png", asd);

  }

  private BufferedImage rescale(BufferedImage img, int nw, int nh) {
    Image imgSmall = img.getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
    BufferedImage dimg = new BufferedImage(nw, nh, img.getType());

    Graphics2D g = dimg.createGraphics();
    g.drawImage(imgSmall, 0, 0, null);
    g.dispose();
    return dimg;
  }

}
