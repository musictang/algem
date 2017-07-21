/*
 * @(#) ConfigTemplates.java Algem 2.15.0 21/07/17
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

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import net.algem.util.BundleUtil;
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.ImageUtil;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 19/07/17
 */
public class ConfigTemplates
  extends ConfigPanel {

  private JLabel quoteThumb;
  private JLabel contractThumb;
  private JLabel agreementThumb;

  private JButton btQuote;
  private JButton btAgreement;
  private JButton btContract;
  private File file; //new File("/tmp/Papier entete 2010.pdf");
  private PageTemplateIO templateIO;
  private final static short QUOTE_PAGE_MODEL = 1;
  private final static short AGREEMENT_PAGE_MODEL = 2;
  private final static short CONTRACT_PAGE_MODEL = 3;

  private Map<Short,PageTemplate> templates = new HashMap<>();

  public ConfigTemplates(String title, PageTemplateIO pageIO) {
    super(title);
    this.templateIO = pageIO;
  }

  @Override
  public List<Config> get() {
    return null;
  }

  public void init() {
    ImageIcon icon = null;
    try {
      BufferedImage bi = getDefaultThumbnail();
      if (bi != null) {
        icon = new ImageIcon(getDefaultThumbnail());
      }
    } catch (IOException ex) {
      GemLogger.logException(ex);
    }
    content = new GemPanel(new GridLayout(1, 3, 10, 10));

    ActionListener listener = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        file = FileUtil.getFile(
          ConfigTemplates.this,
          BundleUtil.getLabel("FileChooser.selection"),
          null,
          MessageUtil.getMessage("filechooser.pdf.filter.label"),
          "pdf"
        );
        Object src = e.getSource();
        if (file != null && src instanceof JButton) {
          if (src == btQuote) {
            importTemplate(quoteThumb);
          } else if (src == btAgreement) {
            importTemplate(agreementThumb);
          } else {
            importTemplate(contractThumb);
          }
        }
      }
    };

    JPanel p1 = new JPanel(new BorderLayout());
    p1.add(new JLabel(BundleUtil.getLabel("Quotations.Invoices.label"), SwingConstants.CENTER), BorderLayout.NORTH);
    quoteThumb = new JLabel("", SwingConstants.CENTER);

//    quoteThumb.setIcon(icon);
    p1.add(quoteThumb, BorderLayout.CENTER);
    btQuote = new GemButton(GemCommand.DEFINE_CMD);
    btQuote.addActionListener(listener);

    p1.add(btQuote, BorderLayout.SOUTH);

    JPanel p2 = new JPanel(new BorderLayout());
    p2.add(new JLabel(BundleUtil.getLabel("Internship.agreements.label"), SwingConstants.CENTER), BorderLayout.NORTH);
    agreementThumb = new JLabel("", SwingConstants.CENTER);
    agreementThumb.setIcon(icon);
    p2.add(agreementThumb, BorderLayout.CENTER);
    btAgreement = new GemButton(GemCommand.DEFINE_CMD);
    btAgreement.addActionListener(listener);
    p2.add(btAgreement, BorderLayout.SOUTH);

    JPanel p3 = new JPanel(new BorderLayout());
    p3.add(new JLabel(BundleUtil.getLabel("Training.contracts.label"), SwingConstants.CENTER), BorderLayout.NORTH);
    contractThumb = new JLabel("", SwingConstants.CENTER);
    contractThumb.setIcon(icon);
    p3.add(contractThumb, BorderLayout.CENTER);
    btContract = new GemButton(GemCommand.DEFINE_CMD);
    btContract.addActionListener(listener);
    p3.add(btContract, BorderLayout.SOUTH);

    try {
      load();
    } catch (SQLException | IOException ex) {
      GemLogger.logException(ex);
    }

    content.add(p1);
    content.add(p2);
    content.add(p3);
    add(content);
  }

  private void importTemplate(JLabel label) {

    try {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      byte[] ba = getByteArrayFromFile(file);
      save(ba, label);
      PDFFile pdffile = new PDFFile(ByteBuffer.wrap(ba));
      BufferedImage img = getThumbnailFromPdf(pdffile);
      if (img == null) {
        img = getDefaultThumbnail();
      }

      ImageIcon icon = (img == null ? null : new ImageIcon(img));
      label.setIcon(icon);
    } catch (IOException | SQLException ex) {
      GemLogger.logException(ex);
      //label.setIcon(null);
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }

  }

  private ImageIcon getIconFromFile(PDFFile pdffile) throws IOException {
    BufferedImage img = getThumbnailFromPdf(pdffile);
    if (img == null) {
      img = getDefaultThumbnail();
    }

    return (img == null ? null : new ImageIcon(img));
  }

  private void load() throws SQLException, IOException {
//    PageModel pages = pageModelIO.find(QUOTE_PAGE_MODEL);
    List<PageTemplate> pages = templateIO.findAll();
    PDFFile pdffile = null;
    for (PageTemplate p : pages) {
      templates.put(p.getType(), p);
      switch (p.getType()) {
        case QUOTE_PAGE_MODEL:
          pdffile = new PDFFile(ByteBuffer.wrap(p.getContent()));
          quoteThumb.setIcon(getIconFromFile(pdffile));
          break;
        case AGREEMENT_PAGE_MODEL:
          pdffile = new PDFFile(ByteBuffer.wrap(p.getContent()));
          agreementThumb.setIcon(getIconFromFile(pdffile));
          break;
        case CONTRACT_PAGE_MODEL:
          pdffile = new PDFFile(ByteBuffer.wrap(p.getContent()));
          contractThumb.setIcon(getIconFromFile(pdffile));
          break;
      }
    }

  }

  private void save(byte[] bytes, JLabel label) throws SQLException, IOException {
    if (quoteThumb == label) {
        if (templates.get(QUOTE_PAGE_MODEL) == null) {
          templateIO.insert(QUOTE_PAGE_MODEL, bytes);
        } else {
          templateIO.update(QUOTE_PAGE_MODEL, bytes);
      }
    } else if (agreementThumb == label) {//XXX à terminer
      templateIO.insert(AGREEMENT_PAGE_MODEL, bytes);
    } else {
      templateIO.insert(CONTRACT_PAGE_MODEL, bytes);
    }
  }

  private void preview() {

  }

  private BufferedImage getDefaultThumbnail() throws IOException {
    InputStream input = getClass().getResourceAsStream(ImageUtil.DEFAULT_PDF_TEMPLATE);
    return input == null ? null : ImageIO.read(input);
  }

  private ByteBuffer getBufferFromFile(File file) throws IOException {
    RandomAccessFile raf = new RandomAccessFile(file, "r");
    //ReadableByteChannel ch = Channels.newChannel(new FileInputStream(file));
    FileChannel channel = raf.getChannel();
    return channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
  }

  private byte[] getByteArrayFromFile(File file) throws IOException {
    return Files.readAllBytes(file.toPath());
  }

  private BufferedImage getThumbnailFromPdf(PDFFile pdffile) throws IOException {
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

    return rescale(bufferedImage, rect.width / 4, rect.height / 4);
//    return null;
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
