/*
 * @(#) ConfigTemplates.java Algem 2.17.0 29/03/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import net.algem.edition.PdfHandler;
import net.algem.util.BundleUtil;
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.ImageUtil;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.0
 * @since 2.15.0 19/07/17
 */
public class ConfigTemplates
  extends ConfigPanel {

  private JLabel quoteThumb;
  private JLabel contractThumb;
  private JLabel agreementThumb;
  private JLabel defaultThumb;
  private JLabel wishThumb; //ERIC 29/03/2019
  private JLabel wishConfirmThumb;  //ERIC 29/03/2019

  private JButton btQuote;
  private JButton btAgreement;
  private JButton btContract;
  private JButton btDefault;
  private JButton btWish;  //ERIC 29/03/2019
  private JButton btWishConfirm;  //ERIC 29/03/2019
  
  private File file;
  private PdfHandler pdfHandler;
  private Map<Short, PageTemplate> templates = new HashMap<>();

  public ConfigTemplates(String title, PageTemplateIO templateIO) {
    super(title);
    pdfHandler = new PdfHandler(templateIO);
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
    content = new GemPanel(new GridLayout(2, 3, 10, 10));

    ActionListener listener = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src instanceof JButton) {
          try {
            if (src == btQuote) {
              resetTemplate(PageTemplate.QUOTE_PAGE_MODEL);
              quoteThumb.setIcon(new ImageIcon(getDefaultThumbnail()));
            } else if (src == btAgreement) {
              resetTemplate(PageTemplate.AGREEMENT_PAGE_MODEL);
              agreementThumb.setIcon(new ImageIcon(getDefaultThumbnail()));
            } else if (src == btContract) {
              resetTemplate(PageTemplate.CONTRACT_PAGE_MODEL);
              contractThumb.setIcon(new ImageIcon(getDefaultThumbnail()));
            } else if (src == btWish) {
              resetTemplate(PageTemplate.ENROLMENTWISH_PAGE_MODEL);
              contractThumb.setIcon(new ImageIcon(getDefaultThumbnail()));
            } else if (src == btWishConfirm) {
              resetTemplate(PageTemplate.WISHCONFIRM_PAGE_MODEL);
              contractThumb.setIcon(new ImageIcon(getDefaultThumbnail()));
            } else {
              resetTemplate(PageTemplate.DEFAULT_MODEL);
              defaultThumb.setIcon(new ImageIcon(getDefaultThumbnail()));
            }
          } catch (IOException ex) {
            GemLogger.logException(ex);
          }
        }
      }
    };
    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        file = FileUtil.getFile(
          ConfigTemplates.this,
          BundleUtil.getLabel("FileChooser.selection"),
          null,
          MessageUtil.getMessage("filechooser.pdf.filter.label"),
          "pdf"
        );
        Object src = e.getSource();
        if (file != null && src instanceof JLabel) {
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          if (src == quoteThumb) {
            importTemplate(file, quoteThumb);
          } else if (src == agreementThumb) {
            importTemplate(file, agreementThumb);
          } else if (src == contractThumb) {
            importTemplate(file, contractThumb);
          } else if (src == wishThumb) {
            importTemplate(file, wishThumb);
          } else if (src == wishConfirmThumb) {
            importTemplate(file, wishConfirmThumb);
          } else {
            importTemplate(file, defaultThumb);
          }
          setCursor(Cursor.getDefaultCursor());
        }
      }

    };
    JPanel p1 = new JPanel(new BorderLayout());
    p1.add(new JLabel(BundleUtil.getLabel("Quotations.Invoices.label"), SwingConstants.CENTER), BorderLayout.NORTH);
    Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    quoteThumb = new JLabel("", SwingConstants.CENTER);
    quoteThumb.setToolTipText(GemCommand.DEFINE_CMD);
    quoteThumb.setIcon(icon);
    quoteThumb.setCursor(hand);
    quoteThumb.addMouseListener(mouseAdapter);

    p1.add(quoteThumb, BorderLayout.CENTER);

    btQuote = new GemButton(GemCommand.DELETE_CMD);
    btQuote.addActionListener(listener);
    p1.add(btQuote, BorderLayout.SOUTH);

    JPanel p2 = new JPanel(new BorderLayout());
    p2.add(new JLabel(BundleUtil.getLabel("Training.contracts.label"), SwingConstants.CENTER), BorderLayout.NORTH);
    contractThumb = new JLabel("", SwingConstants.CENTER);
    contractThumb.setToolTipText(GemCommand.DEFINE_CMD);
    contractThumb.setIcon(icon);
    contractThumb.setCursor(hand);
    contractThumb.addMouseListener(mouseAdapter);
    p2.add(contractThumb, BorderLayout.CENTER);
    btContract = new GemButton(GemCommand.DELETE_CMD);
    btContract.addActionListener(listener);
    p2.add(btContract, BorderLayout.SOUTH);

    JPanel p3 = new JPanel(new BorderLayout());
    p3.add(new JLabel(BundleUtil.getLabel("Internship.agreements.label"), SwingConstants.CENTER), BorderLayout.NORTH);
    agreementThumb = new JLabel("", SwingConstants.CENTER);
    agreementThumb.setIcon(icon);
    agreementThumb.setCursor(hand);
    agreementThumb.setToolTipText(GemCommand.DEFINE_CMD);
    agreementThumb.addMouseListener(mouseAdapter);
    p3.add(agreementThumb, BorderLayout.CENTER);
    btAgreement = new GemButton(GemCommand.DELETE_CMD);
    btAgreement.addActionListener(listener);
    p3.add(btAgreement, BorderLayout.SOUTH);

    JPanel p4 = new JPanel(new BorderLayout());
    p4.add(new JLabel(BundleUtil.getLabel("Default.template.label"), SwingConstants.CENTER), BorderLayout.NORTH);
    defaultThumb = new JLabel("", SwingConstants.CENTER);
    defaultThumb.setToolTipText(GemCommand.DEFINE_CMD);
    defaultThumb.setIcon(icon);
    defaultThumb.setCursor(hand);
    defaultThumb.addMouseListener(mouseAdapter);
    p4.add(defaultThumb, BorderLayout.CENTER);
    btDefault = new GemButton(GemCommand.DELETE_CMD);
    btDefault.addActionListener(listener);
    p4.add(btDefault, BorderLayout.SOUTH);

    JPanel p5 = new JPanel(new BorderLayout()); // ERIC 29/03/2019
    p5.add(new JLabel(BundleUtil.getLabel("Enrolment.wish.template.label"), SwingConstants.CENTER), BorderLayout.NORTH);
    wishThumb = new JLabel("", SwingConstants.CENTER);
    wishThumb.setIcon(icon);
    wishThumb.setCursor(hand);
    wishThumb.setToolTipText(GemCommand.DEFINE_CMD);
    wishThumb.addMouseListener(mouseAdapter);
    p5.add(wishThumb, BorderLayout.CENTER);
    btWish = new GemButton(GemCommand.DELETE_CMD);
    btWish.addActionListener(listener);
    p5.add(btWish, BorderLayout.SOUTH);

    JPanel p6 = new JPanel(new BorderLayout()); // ERIC 29/03/2019
    p6.add(new JLabel(BundleUtil.getLabel("Enrolment.wish.confirm.template.label"), SwingConstants.CENTER), BorderLayout.NORTH);
    wishConfirmThumb = new JLabel("", SwingConstants.CENTER);
    wishConfirmThumb.setIcon(icon);
    wishConfirmThumb.setCursor(hand);
    wishConfirmThumb.setToolTipText(GemCommand.DEFINE_CMD);
    wishConfirmThumb.addMouseListener(mouseAdapter);
    p6.add(wishConfirmThumb, BorderLayout.CENTER);
    btWishConfirm = new GemButton(GemCommand.DELETE_CMD);
    btWishConfirm.addActionListener(listener);
    p6.add(btWishConfirm, BorderLayout.SOUTH);

    try {
      load();
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }

    content.add(p1);
    content.add(p2);
    content.add(p3);
    content.add(Box.createHorizontalBox());
    content.add(p5);
    content.add(p6);
    content.add(p4);
    content.add(Box.createHorizontalBox());
    add(content);
  }

  /**
   * Saves a template and preview it.
   *
   * @param file the template to save
   * @param label thumbnail component
   */
  private void importTemplate(File file, JLabel label) {

    try {
      byte[] ba = Files.readAllBytes(file.toPath());
      if (ba.length > (2*1024*1024)) { // 2Mo
        MessagePopup.warning(this, MessageUtil.getMessage("file.too.large.warning", "2 Mo"));
        return;
      }
      PDFFile pdffile = new PDFFile(ByteBuffer.wrap(ba));
      BufferedImage img = pdfHandler.getThumbnail(pdffile);
      if (img == null) {
        img = getDefaultThumbnail();
      }

      ImageIcon icon = (img == null ? null : new ImageIcon(img));
      label.setIcon(icon);
      save(ba, label);
    } catch (IOException | SQLException ex) {
      GemLogger.logException(ex);
      //label.setIcon(null);
    }

  }

  private void resetTemplate(short type) {
    if (templates.get(type) != null) {
      try {
        pdfHandler.resetTemplate(type, templates);
      } catch (SQLException ex) {
        GemLogger.logException(ex);
      }
    }
  }

  private ImageIcon getIconFromFile(PDFFile pdffile) throws IOException {
    BufferedImage img = pdfHandler.getThumbnail(pdffile);
    if (img == null) {
      img = getDefaultThumbnail();
    }

    return (img == null ? null : new ImageIcon(img));
  }

  private void load() throws SQLException {
    List<PageTemplate> tpl = pdfHandler.getTemplates();
    for (PageTemplate p : tpl) {
      switch (p.getType()) {
        case PageTemplate.QUOTE_PAGE_MODEL:
          loadTemplate(quoteThumb, p);
          break;
        case PageTemplate.AGREEMENT_PAGE_MODEL:
          loadTemplate(agreementThumb, p);
          break;
        case PageTemplate.CONTRACT_PAGE_MODEL:
          loadTemplate(contractThumb, p);
          break;
        case PageTemplate.ENROLMENTWISH_PAGE_MODEL:
          loadTemplate(wishThumb, p);
          break;
        case PageTemplate.WISHCONFIRM_PAGE_MODEL:
          loadTemplate(wishConfirmThumb, p);
          break;
        default :
          loadTemplate(defaultThumb, p);
      }
    }
  }

  private void loadTemplate(JLabel thumb, PageTemplate p) {
    try {
      PDFFile pdffile = new PDFFile(ByteBuffer.wrap(p.getContent()));
      thumb.setIcon(getIconFromFile(pdffile));
      templates.put(p.getType(), p);
    } catch (IOException ex) {
      GemLogger.logException(ex);
    }
  }

  private void save(byte[] bytes, JLabel label) throws SQLException, IOException {
    if (quoteThumb == label) {
      pdfHandler.saveTemplate(new PageTemplate(PageTemplate.QUOTE_PAGE_MODEL, bytes), templates);
    } else if (agreementThumb == label) {
      pdfHandler.saveTemplate(new PageTemplate(PageTemplate.AGREEMENT_PAGE_MODEL, bytes), templates);
    } else if (contractThumb == label) {
      pdfHandler.saveTemplate(new PageTemplate(PageTemplate.CONTRACT_PAGE_MODEL, bytes), templates);
    } else if (wishThumb == label) {
      pdfHandler.saveTemplate(new PageTemplate(PageTemplate.ENROLMENTWISH_PAGE_MODEL, bytes), templates);
    } else if (wishConfirmThumb == label) {
      pdfHandler.saveTemplate(new PageTemplate(PageTemplate.WISHCONFIRM_PAGE_MODEL, bytes), templates);
    } else {
      pdfHandler.saveTemplate(new PageTemplate(PageTemplate.DEFAULT_MODEL, bytes), templates);
    }
  }

  private BufferedImage getDefaultThumbnail() throws IOException {
    InputStream input = getClass().getResourceAsStream(ImageUtil.DEFAULT_PDF_TEMPLATE);
    return input == null ? null : ImageIO.read(input);
  }

}
