/*
 * @(#)InvoiceView.java 2.9.4.13 05/10/2015
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.billing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JFormattedTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.algem.accounting.AccountUtil;
import net.algem.accounting.OrderLine;
import net.algem.contact.Address;
import net.algem.contact.Contact;
import net.algem.contact.ContactIO;
import net.algem.contact.Person;
import net.algem.edition.*;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.room.EstabChoice;
import net.algem.room.Establishment;
import net.algem.room.EstablishmentIO;
import net.algem.util.*;
import net.algem.util.event.CancelEvent;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Invoice / quotation view.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.3.a 07/02/12
 */
public class InvoiceView
        extends GemPanel
        implements ActionListener, GemEventListener, Printable
{

  public static final int MARGIN = ImageUtil.toPoints(15);
  //private final static Font sans = new Font("Helvetica", Font.PLAIN, 8);
  private final static Font sans = new Font(Font.SANS_SERIF, Font.PLAIN, 8);
  //private final static Font serif = new Font("TimesRoman", Font.PLAIN, 9);
  private final static Font serif = new Font(Font.SERIF, Font.PLAIN, 9);
  private GemField invoiceId;
  private GemField invoiceLabel;
  private DateFrField date;
  private GemField ref;
  private EstabChoice estab;
  private GemField payerName;
  private GemNumericField payerId;
  private GemField memberName;
  private GemNumericField issuerId;
  private GemField issuerName;
  private JFormattedTextField downPayment;
  private JFormattedTextField totalET;
  private JFormattedTextField totalATI;
  private JFormattedTextField net;
  private ItemListCtrl itemList;
  private Quote invoice;
  private final DataCache dataCache;
  private final DataConnection dc;
  private final GemDesktop desktop;
  private GemButton btAdd;
  private GemButton btModify;
  private GemButton btDelete;
  private InvoiceItemSearchCtrl browser;
  private NumberFormat nf;
  private final BillingService service;
  private Collection<OrderLine> orderLines;
  private boolean isInvoice;

  public InvoiceView(GemDesktop desktop, BillingService service) {
    this.desktop = desktop;
    this.dataCache = desktop.getDataCache();
    this.dc = DataCache.getDataConnection();
    this.service = service;
    init();
  }

  private void init() {
    GemPanel head = new GemPanel(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(head);
    gb.insets = GridBagHelper.SMALL_INSETS;

    invoiceId = new GemField(6);
    invoiceId.setMinimumSize(new Dimension(65, invoiceId.getPreferredSize().height));
    invoiceId.setCaretPosition(0);
    invoiceId.setEditable(false);

    invoiceLabel = new GemField(25);
    invoiceLabel.setMinimumSize(new Dimension(250, invoiceLabel.getPreferredSize().height));
    
    date = new DateFrField();
    ref = new GemField(15);
    ref.setMinimumSize(new Dimension(150, ref.getPreferredSize().height));
    estab = new EstabChoice(dataCache.getList(Model.Establishment));
    payerId = new GemNumericField(5);
    payerId.setMinimumSize(new Dimension(50, payerId.getPreferredSize().height));
    payerId.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          int id = Integer.parseInt(payerId.getText());
          Person p = (Person) DataCache.findId(id, Model.Person);
          payerName.setText(p.getOrganization() != null && p.getOrganization().length() > 0 ? p.getOrganization() : p.getFirstnameName());
        } catch (Exception ex) {
          GemLogger.log(ex.getMessage());
        }
      }

    });
    payerName = new GemField(20);
    payerName.setMinimumSize(new Dimension(200, payerName.getPreferredSize().height));
    payerName.setEditable(false);
    memberName = new GemField(20);
    memberName.setMinimumSize(new Dimension(200, memberName.getPreferredSize().height));
    memberName.setEditable(false);

    nf = AccountUtil.getDefaultCurrencyFormat();
    downPayment = new JFormattedTextField(nf);
    downPayment.setColumns(10);
    downPayment.getDocument().addDocumentListener(new DownPaymentListener());

    totalET = new JFormattedTextField(nf);
    totalET.setColumns(10);
    totalET.setEditable(false);
    totalATI = new JFormattedTextField(nf);
    totalATI.setColumns(10);
    totalATI.setFont(getFont().deriveFont(Font.BOLD));
    totalATI.setEditable(false);

    net = new JFormattedTextField(nf);
    net.setColumns(10);
    net.setFont(getFont().deriveFont(Font.BOLD));
    net.setEditable(false);

    itemList = new ItemListCtrl(new InvoiceItemTableModel(), false);
   
    gb.add(new GemLabel(BundleUtil.getLabel("Number.abbrev.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(invoiceId, 1, 0, 1, 1);
    gb.add(new GemLabel(BundleUtil.getLabel("Invoice.description.label")), 2, 0, 1, 1, GridBagHelper.WEST);
    gb.add(invoiceLabel, 3, 0, 1, 1);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.label")), 4, 0, 1, 1, GridBagHelper.WEST);
    gb.add(date, 5, 0, 1, 1);
    gb.add(new GemLabel(BundleUtil.getLabel("Invoice.reference.label")),6,0,1,1, GridBagHelper.WEST);
    gb.add(ref,7,0,1,1);
    
    gb.add(new GemLabel(BundleUtil.getLabel("Payer.label")), 0,1,1,1, GridBagHelper.WEST);
    gb.add(payerId,1,1,1,1, GridBagHelper.WEST);
    gb.add(payerName,2,1,2,1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Member.label")),4,1,1,1, GridBagHelper.WEST);
    gb.add(memberName,5,1,3,1, GridBagHelper.WEST);
    
    issuerId = new GemNumericField(5);
    issuerId.setMinimumSize(new Dimension(50, issuerId.getPreferredSize().height));
    issuerId.setEditable(false);
    issuerName = new GemField(20);
    issuerName.setMinimumSize(new Dimension(200, issuerName.getPreferredSize().height));
    issuerName.setEditable(false);
    gb.add(new GemLabel(BundleUtil.getLabel("Issuer.label")),0,2,1,1, GridBagHelper.WEST);
    gb.add(issuerId,1,2,2,1, GridBagHelper.WEST);
    gb.add(issuerName,2,2,2,1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Establishment.label").substring(0,5)),4,2,1,1, GridBagHelper.WEST);
    gb.add(estab,5,2,3,1, GridBagHelper.WEST);

    GemPanel buttons = new GemPanel(new GridLayout(1, 3));
    btDelete = new GemButton(GemCommand.DELETE_CMD);
    btDelete.addActionListener(this);
    btModify = new GemButton(GemCommand.MODIFY_CMD);
    btModify.addActionListener(this);
    btAdd = new GemButton(GemCommand.ADD_CMD);
    btAdd.addActionListener(this);

    buttons.add(btDelete);
    buttons.add(btModify);
    buttons.add(btAdd);

    GemPanel body = new GemPanel(new BorderLayout());
    body.add(itemList, BorderLayout.CENTER);

    GemPanel footer = new GemPanel(new BorderLayout());

    GemPanel total = new GemPanel();
    total.add(new GemLabel(BundleUtil.getLabel("Invoice.down.payment.label")));
    total.add(downPayment);
    total.add(new GemLabel(BundleUtil.getLabel("Invoice.et.label")));
    total.add(totalET);
    total.add(new GemLabel(BundleUtil.getLabel("Invoice.ati.label")));
    total.add(totalATI);
    total.add(new GemLabel(BundleUtil.getLabel("Invoice.net.label")));
    total.add(net);

    footer.add(buttons, BorderLayout.NORTH);
    footer.add(total, BorderLayout.SOUTH);

    setLayout(new BorderLayout());
    add(head, BorderLayout.NORTH);
    add(body, BorderLayout.CENTER);
    add(footer, BorderLayout.SOUTH);

  }

  public void setId(String n) {
    invoiceId.setText(n);
    invoiceId.setCaretPosition(0);
    if (invoice != null) {
      invoice.setNumber(n);
    }
  }

/**
 * Sets the different field values.
 * @param <Q>
 * @param quote
 * @param m member name
 * @param p payer name
 * @param i issuer name
 */
  <Q extends Quote> void set(Q quote, String m, String p, String i) {
    if (quote == null) {
      return;
    }
    invoice = quote;
    
    isInvoice = quote.isInvoice();
    invoiceId.setText(quote.getNumber());
    invoiceId.setCaretPosition(0);
    invoiceLabel.setText(quote.getDescription());
    invoiceLabel.setCaretPosition(0);
    date.setText(quote.getDate().toString());
    ref.setText(quote.getReference());
    estab.setKey(quote.getEstablishment());
    payerId.setText(String.valueOf(quote.getPayer()));
    payerName.setText(p);
    memberName.setText(m);
    
    issuerId.setText(String.valueOf(quote.getIssuer()));
    issuerName.setText(i);

    if (quote.getItems() != null) {
      itemList.loadResult(new Vector<InvoiceItem>(quote.getItems()));
    }

    if (!isInvoice) {
      downPayment.setEditable(false);
      // TODO set payer editable
    }
    downPayment.setValue(quote.getDownPayment());
    totalET.setValue(quote.getTotalET());
    double ttc = quote.getTotalATI();
    totalATI.setValue(ttc);
    net.setValue(Double.valueOf(ttc - quote.getDownPayment()));
    
    orderLines = quote.getOrderLines();
    
    if (!isInvoice && !quote.isEditable()) {
      btDelete.setEnabled(false);
      btModify.setEnabled(false);
      btAdd.setEnabled(false);
    } else {
      btDelete.setEnabled(true);
      btModify.setEnabled(true);
      btAdd.setEnabled(true);
    }
  }

  /**
   * Retrieves the current quote/invoice (possibly updated).
   *
   * @return a quote/invoice
   */
   Quote get() {
      Quote q;
      try {
        q = invoice.getClass().newInstance();
        q.setNumber(invoiceId.getText());
        //Quote inv = new Invoice(invoiceId.getText());
      } catch (ReflectiveOperationException ex) {
        GemLogger.log(ex.getMessage());
        q = new Invoice(invoiceId.getText());
      } 
    
      q.setDescription(invoiceLabel.getText());
      q.setDate(new DateFr(date.getText()));
      q.setReference(ref.getText());
      q.setEstablishment(estab.getKey());
      try {
         q.setPayer(Integer.parseInt(payerId.getText()));
       } catch (NumberFormatException ex) {
         GemLogger.log(ex.getMessage());
       }
      double a = ((Number) downPayment.getValue()).doubleValue();
      q.setDownPayment(Math.abs(a));
      
      q.setItems(getItems());
      q.setOrderLines(orderLines);
      
      if (invoice != null) {
//        inv.setPayer(invoice.getPayer());
        q.setMember(invoice.getMember());
        q.setEstablishment(invoice.getEstablishment());
        q.setIssuer(invoice.getIssuer());
        q.setUser(invoice.getUser());
      }

    return q;
  }

  /**
   * Retrieves the different items (possibly updated).
   *
   * @return a collection of invoice items
   */
  Collection<InvoiceItem> getItems() {
    return itemList.getData();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    try {
      if (GemCommand.ADD_CMD.equals(cmd)) {
        browser = new InvoiceItemSearchCtrl(desktop);
        browser.init();
        browser.addGemEventListener(this);
        browser.addActionListener(this);// pour le bouton abandon du browser
        if (!desktop.hasModule(InvoiceItemSearchCtrl.INVOICE_ITEM_BROWSER_KEY)) {
          desktop.addPanel(InvoiceItemSearchCtrl.INVOICE_ITEM_BROWSER_KEY, browser);
        }
      } else if (GemCommand.MODIFY_CMD.equals(cmd)) {
        InvoiceItem selectedItem = itemList.getSelectedItem();
        InvoiceItemEditor dlg = new InvoiceItemEditor(desktop, selectedItem, service);
        dlg.setVisible(true);
        if (dlg.hasChanged()) {
          OrderLine ol = selectedItem.getOrderLine();
          InvoiceItem updatedItem = dlg.get();
          // l'article à modifier pourrait être référencé dans un devis (on aurait créé une facture à partir de ce devis)
          try {
            if (isInvoice && updatedItem.getItem() != null && QuoteIO.isQuotationItem(updatedItem.getItem().getId(), dc)) {
              if (!MessagePopup.confirm(this, MessageUtil.getMessage("existing.estimate.item"), BundleUtil.getLabel("Warning.label"))) {
                return;
              }
            }
          } catch (SQLException ex) {
            System.err.println(ex.getMessage());
          }
          if (ol != null && ol.getId() > 0) {
//          if (ol != null && !AccountUtil.isRevenueAccount(ol.getAccount())
//                  && updatedItem.getQuantity() > 0.0f
//                  && Math.abs(ol.getDoubleAmount()) != updatedItem.getTotal(true)) {
            MessagePopup.warning(this, MessageUtil.getMessage("invoice.item.modification.warning"));
            // modification des items déjà payés
            if (ol.isPaid()) {
              if (!MessagePopup.confirm(this, MessageUtil.getMessage("invoice.item.modification.confirmation"), "")) {
                return;
              }
            }
          }
          update(updatedItem);
          postEvent(new CancelEvent());
        }
      } else if (GemCommand.DELETE_CMD.equals(cmd)) {
        InvoiceItem item = itemList.getSelectedItem();
        itemList.deleteRow(item);
        if (isInvoice) {
          MessagePopup.warning(this, MessageUtil.getMessage("invoice.item.suppression.warning"));
//          ((Invoice) invoice).getOrderLines().remove(item.getOrderLine());
          orderLines.remove(item.getOrderLine());
        }
        updateTotal();
      } else if (GemCommand.CANCEL_CMD.equals(cmd)) {
        postEvent(new CancelEvent());
      }
    } catch (IndexOutOfBoundsException ix) {
      MessagePopup.warning(this, MessageUtil.getMessage("no.line.selected"));
    }
  }

  @Override
  public void postEvent(GemEvent evt) {

    if (evt instanceof InvoiceItemCreateEvent) {
      add(((InvoiceItemCreateEvent) evt).getArticleFacture());
    } else if (evt instanceof CancelEvent) {
      if (browser != null) {
        desktop.removeGemEventListener((GemEventListener) browser);
      }
      desktop.removeModule(InvoiceItemSearchCtrl.INVOICE_ITEM_BROWSER_KEY);
    }
  }

  /**
   * Reset the fields.
   */
  void reset() {
    invoice = null;
    itemList = null;
    invoiceLabel.setText(null);
    date.set(new DateFr());
    estab.setSelectedIndex(0);
    totalET.setValue(null);
    totalATI.setValue(null);
    memberName.setText(null);
    payerName.setText(null);
    issuerName.setText(null);
    downPayment.setValue(null);
    ref.setText(null);
  }

  /**
   * Invoice item modification.
   *
   * @param a the item
   */
  private void update(InvoiceItem a) {
    if (a == null) {
      return;
    }

    itemList.updateRow(a);
    updateTotal();
  }

  /**
   * Adds an item.
   *
   * @param a the item
   */
  private void add(InvoiceItem a) {
    if (a == null) {
      return;
    }
    itemList.addRow(a);
    updateTotal();
  }

  /**
   * Updates total in the main view.
   */
  private void updateTotal() {
    invoice.setItems(getItems());
    totalET.setValue(invoice.getTotalET());
    double ttc = invoice.getTotalATI();
    totalATI.setValue(ttc);
    net.setValue(ttc - ((Number) downPayment.getValue()).doubleValue());

  }

  @Override
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {

    Quote quote = get();
    
    if (pageIndex > 0) { /* We have only one page, and 'page' is zero-based */
      return NO_SUCH_PAGE;
    }
    int left = ImageUtil.toPoints(110);
    int top = ImageUtil.toPoints(50);
    int bottom = ImageUtil.toPoints(297 - 20);// hauteur de page - 20 mm de marge
    int margin = ImageUtil.toPoints(15);

    Contact c = ContactIO.findId(quote.getPayer(), dc);
    Address a = null;
    if (c != null) {
      a = c.getAddress();
    }
      
    IdentityElement name = new IdentityElement(c, left, top + 10);
    AddressElement address = new InvoiceAddressElement(a, left, top + 30);
    // nom et adresse
    name.draw(g);
    address.draw(g);

    g.setFont(serif);    
    // numéro invoice
    String invoiceNumber = invoice.getClass() == Quote.class
            ? BundleUtil.getLabel("Quotation.label") : BundleUtil.getLabel("Invoice.label");
    g.drawString(invoiceNumber + " : " + quote.getNumber(), margin, top + 100);
    // nom établissement
    g.drawString(getEstabName(quote) + ", le " + quote.getDate(), left, top + 85);
    // référence
    g.drawString("Ref. : " + quote.getReference(), left, top + 100);
    /*
    // @since 2.9.4.6 ne plus afficher l'émetteur à l'impression
    Person issuer = null;
    try {
      // émetteur
      issuer = (Person) DataCache.findId(quote.getIssuer(), Model.Person);
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
    }
    g.drawString(BundleUtil.getLabel("Issuer.label") + " : " + (issuer != null && issuer.getId() > 0 ? issuer.getFirstnameName(): ""), margin, top + 120);
    */
    // description
    g.drawString(BundleUtil.getLabel("Invoice.description.label") + " : " + quote.getDescription(), margin, top + 140);

    int tableY = top + 160;
    int tabletop = tableY;
    int end = margin + InvoiceItemElement.TABLE_WIDTH;
    // entete tableau
    new InvoiceHeaderElement(margin, tableY).draw(g);
    g.drawLine(margin, tableY + 20, margin + InvoiceItemElement.TABLE_WIDTH, tableY + 20);
    // items
    tableY += 5;
    for (InvoiceItem invoiceItem : quote.getItems()) {
      InvoiceItemElement item = new InvoiceItemElement(margin, tableY + 20, invoiceItem);
      item.draw(g);
      tableY = tableY + 20 + item.getOffset();
    }
    tableY += 5;
    int tablebottom = tableY + 20;
    // encadrement du tableau d'items
    g.drawRect(margin, tabletop, InvoiceItemElement.TABLE_WIDTH, tablebottom - tabletop);
    // lignes séparatrices verticales des colonnes
    g.drawLine(InvoiceItemElement.xColPrice, tabletop, InvoiceItemElement.xColPrice, tablebottom); // colonne prix
    g.drawLine(InvoiceItemElement.xColVAT, tabletop, InvoiceItemElement.xColVAT, tablebottom); // colonne tva
    g.drawLine(InvoiceItemElement.xColQty, tabletop, InvoiceItemElement.xColQty, tablebottom); // colonne quantité
    g.drawLine(InvoiceItemElement.xColHT, tabletop, InvoiceItemElement.xColHT, tablebottom); // colonne total HT

    // pied tableau
    new InvoiceFooterElement(margin, tablebottom + 20, quote).draw(g);
    // infos légales
    drawFooter(g, margin, bottom);

    return PAGE_EXISTS;
  }

  /**
   * Prints the invoice.
   */
  void print() {
    
    if (invoice.getClass() != Quote.class && (invoice.getNumber() == null || invoice.getNumber().isEmpty())) {
      MessagePopup.warning(this, MessageUtil.getMessage("invoice.printing.warning"));
      return;
    }
    PrinterJob job = PrinterJob.getPrinterJob();
    job.setPrintable(this);

    try {
      if (job.printDialog()) {
        job.print(FileUtil.getAttributeSet(MediaSizeName.ISO_A4, OrientationRequested.PORTRAIT));
      }
    } catch (PrinterException e) {
      System.err.println(e.getMessage());
    }

  }

  /**
   * Search establishment name.
   *
   * @param q quote/invoice
   * @return a name or null
   */
  private String getEstabName(Quote q) {
    try {
      Establishment e = EstablishmentIO.findId(q.getEstablishment(), dc);
      return e == null ? "" : e.getName();
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      return "";
    }
  }

  /**
   * Prinst footer.
   *
   * @param g graphics
   * @param x horizontal position
   * @param y vertical position
   */
  private void drawFooter(Graphics g, int x, int y) {

    int yPos = y;
    List<String> lines = InvoiceFooterEditor.getFooter();
    g.setFont(sans);
    // on commence par la dernière ligne afin de partir de la marge inférieure
    for (int i = lines.size() - 1; i >= 0; i--) {
      g.drawString(lines.get(i), x, yPos);
      yPos -= 10;
    }

  }

  class DownPaymentListener
          implements DocumentListener
  {

    @Override
    public void insertUpdate(DocumentEvent e) {
      this.changedUpdate(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      this.changedUpdate(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

      String a = downPayment.getText();
      double ac = 0.0;
      if (invoice != null && a != null && !a.isEmpty()) {
        try {
          ac = nf.parse(a).doubleValue();
        } catch (ParseException ex) {
          System.err.println(ex.getMessage());
        }
        net.setValue(invoice.getTotalATI() - Math.abs(ac));
      }
    }
  }

}
