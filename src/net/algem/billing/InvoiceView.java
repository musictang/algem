/*
 * @(#)InvoiceView.java 2.7.h 22/02/13
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
package net.algem.billing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
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
import net.algem.util.module.GemModule;
import net.algem.util.ui.*;

/**
 * Invoice / quotation view.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.h
 * @since 2.3.a 07/02/12
 */
public class InvoiceView
        extends GemBorderPanel
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
  private EstabChoice etabChoix;
  private GemField payer;
  private GemField member;
  private JFormattedTextField downPayment;
  private JFormattedTextField totalET;
  private JFormattedTextField totalATI;
  private JFormattedTextField net;
  private ItemListCtrl itemList;
  private Quote invoice;
  private DataCache dataCache;
	private DataConnection dc;
  private GemDesktop desktop;
  private GemButton btAdd;
  private GemButton btModify;
  private GemButton btDelete;
  private InvoiceItemSearchCtrl browser;
  private NumberFormat nf;
	private BillingServiceI service;

  public <F extends Quote> InvoiceView(F f, GemDesktop desktop, BillingServiceI service) {

    this.desktop = desktop;
    this.dataCache = desktop.getDataCache();
		this.dc = dataCache.getDataConnection();
		this.service = service;

    init(f);
  }

  public <F extends Quote> void init(F f) {

    GemPanel head = new GemPanel(new BorderLayout());

    GemPanel head1 = new GemPanel();
    GemPanel head2 = new GemPanel();

    invoiceId = new GemField(6);
    invoiceId.setEditable(false);

    invoiceLabel = new GemField(20);
    date = new DateFrField();
    ref = new GemField(15);
    etabChoix = new EstabChoice(dataCache.getList(Model.Establishment));
    payer = new GemField(20);
    payer.setEditable(false);
    member = new GemField(20);
    member.setEditable(false);

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

    set(f); // corrélation invoice
    head1.add(invoiceId);
    head1.add(new GemLabel(BundleUtil.getLabel("Invoice.description.label")));
    head1.add(invoiceLabel);
    head1.add(new GemLabel(BundleUtil.getLabel("Date.label")));
    head1.add(date);
    head1.add(new GemLabel(BundleUtil.getLabel("Invoice.reference.label")));
    head1.add(ref);
    head2.add(new GemLabel(BundleUtil.getLabel("Establishment.label")));
    head2.add(etabChoix);
    head2.add(new GemLabel(BundleUtil.getLabel("Payer.label")));
    head2.add(payer);
    head2.add(new GemLabel(BundleUtil.getLabel("Member.label")));
    head2.add(member);

    head.add(head1, BorderLayout.NORTH);
    head.add(head2, BorderLayout.SOUTH);

    GemPanel boutons = new GemPanel(new GridLayout(1, 3));
    btDelete = new GemButton(GemCommand.DELETE_CMD);
    btDelete.addActionListener(this);
    btModify = new GemButton(GemCommand.MODIFY_CMD);
    btModify.addActionListener(this);
    btAdd = new GemButton(GemCommand.ADD_CMD);
    btAdd.addActionListener(this);

    if (!f.isInvoice() && !f.isEditable()) {
      btDelete.setEnabled(false);
      btModify.setEnabled(false);
      btAdd.setEnabled(false);
    }

    boutons.add(btDelete);
    boutons.add(btModify);
    boutons.add(btAdd);

    GemPanel body = new GemPanel(new BorderLayout());
    body.add(itemList, BorderLayout.CENTER);

    GemPanel footer = new GemPanel(new BorderLayout());

    GemPanel total = new GemPanel();
    total.add(new GemLabel(BundleUtil.getLabel("Invoice.down.payment.label")));
    total.add(downPayment);
//    total.add(new GemLabel(BundleUtil.getLabel("Invoice.total.label")));
    total.add(new GemLabel(BundleUtil.getLabel("Invoice.et.label")));
    total.add(totalET);
    total.add(new GemLabel(BundleUtil.getLabel("Invoice.ati.label")));
    total.add(totalATI);
    total.add(new GemLabel(BundleUtil.getLabel("Invoice.net.label")));
    total.add(net);

    footer.add(boutons, BorderLayout.NORTH);
    footer.add(total, BorderLayout.SOUTH);

    setLayout(new BorderLayout());
    add(head, BorderLayout.NORTH);
    add(body, BorderLayout.CENTER);
    add(footer, BorderLayout.SOUTH);

  }

  public void setMember(String n) {
    member.setText(n);
  }

  public void setPayer(String p) {
    payer.setText(p);
  }

  public void setId(String n) {
    invoiceId.setText(n);
  }

  /**
   * Set the differents fields.
   *
   * @param f
   */
  public <F extends Quote> void set(F f) {
    if (f == null) {
      return;
    }
    invoice = f;

    invoiceId.setText(f.getNumber());
    invoiceLabel.setText(f.getDescription());
    date.setText(f.getDate().toString());
    ref.setText(f.getReference());
    etabChoix.setKey(f.getEstablishment());
    payer.setText(String.valueOf(f.getPayer()));
    member.setText(String.valueOf(f.getMember()));

    if (f.getItems() != null) {
      itemList.loadResult(new Vector<InvoiceItem>(f.getItems()));
    }

    if (!f.isInvoice()) {
      downPayment.setEditable(false);
    }
    downPayment.setValue(f.getDownPayment());
    totalET.setValue(f.getTotalET());
    double ttc = invoice.getTotalATI();
    totalATI.setValue(ttc);
    net.setValue(Double.valueOf(ttc - f.getDownPayment()));
  }

  /**
   * Retrieves the current quote/invoice (possibly updated).
   *
   * @return a quote/invoice
   */
  public Quote get() {

    if (invoice != null) {
      invoice.setDescription(invoiceLabel.getText());
      invoice.setDate(new DateFr(date.getText()));
      invoice.setReference(ref.getText());
      invoice.setEstablishment(etabChoix.getKey());
      double a = ((Number) downPayment.getValue()).doubleValue();
      invoice.setDownPayment(Math.abs(a));
    }

    return invoice;
  }

  /**
   * Retrieves the different items (possibly updated).
   *
   * @return a collection of invoice items
   */
  public Collection<InvoiceItem> getItems() {
    return itemList.getData();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    //System.out.println("InvoiceView.actionPerformed");
    String cmd = e.getActionCommand();
    try {
      if (GemCommand.ADD_CMD.equals(cmd)) {
        browser = new InvoiceItemSearchCtrl(desktop);
        browser.init();
        browser.addGemEventListener(this);
        browser.addActionListener(this);// pour le bouton abandon du browser
        if (!desktop.hasModule(GemModule.INVOICE_ITEM_BROWSER_KEY)) {
          desktop.addPanel(GemModule.INVOICE_ITEM_BROWSER_KEY, browser);
        }
      } else if (GemCommand.MODIFY_CMD.equals(cmd)) {
        InvoiceItem selectedItem = itemList.getSelectedItem();
        InvoiceItemEditor dlg = new InvoiceItemEditor(desktop, selectedItem, service);
        dlg.setVisible(true);
        if (dlg.isModified()) {
          OrderLine ol = selectedItem.getOrderLine();
          InvoiceItem updatedItem = dlg.get();
          // l'article à modifier pourrait être référencé dans un devis (on aurait créé une facture à partir de ce devis)
          try {
            if (updatedItem.getItem() != null && QuoteIO.isQuotationItem(updatedItem.getItem().getId(), dc)) {
              if (!MessagePopup.confirm(this, MessageUtil.getMessage("existing.estimate.item"), BundleUtil.getLabel("Warning.label"))) {
                return;
              }
            }
          } catch (SQLException ex) {
            System.err.println(ex.getMessage());
          }
          if (ol != null && !AccountUtil.isRevenueAccount(ol.getAccount())
                  && updatedItem.getQuantity() > 0.0f
                  && Math.abs(ol.getDoubleAmount()) != updatedItem.getTotal(true)) {
            MessagePopup.warning(this, MessageUtil.getMessage("invoice.item.amount.modification.warning"));
            // modification des items déjà payés
            if (ol.isPaid()) {
              if (!MessagePopup.confirm(this, MessageUtil.getMessage("invoice.item.amount.modification.confirmation"), "")) {
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
        if (invoice.isInvoice()) {
          MessagePopup.warning(this, MessageUtil.getMessage("invoice.item.suppression.warning"));
          ((Invoice) invoice).getOrderLines().remove(item.getOrderLine());
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
      desktop.removeModule(GemModule.INVOICE_ITEM_BROWSER_KEY);
    }
  }

  /**
   * Reset the fields.
   */
  public void reset() {
    invoice = null;
    itemList = null;
    invoiceLabel.setText(null);
    date.set(new DateFr());
    etabChoix.setSelectedIndex(0);
    totalET.setValue(null);
    totalATI.setValue(null);
    member.setText(null);
    payer.setText(null);
    downPayment.setValue(null);
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
   * Updates total in the view.
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

    if (pageIndex > 0) { /* We have only one page, and 'page' is zero-based */
      return NO_SUCH_PAGE;
    }
    int left = ImageUtil.toPoints(110);
    int top = ImageUtil.toPoints(50);
    int bottom = ImageUtil.toPoints(297 - 20);// hauteur de page - 20 mm de marge
    int margin = ImageUtil.toPoints(15);

    get();
    Contact c = ContactIO.findId(invoice.getPayer(), dc);
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
    // nom établissement
    g.drawString(getEtabName(invoice) + ", le " + invoice.getDate(), left, top + 80);
    // numéro invoice
    String nlabel = invoice.getClass() == Quote.class
            ? BundleUtil.getLabel("Quotation.label") : BundleUtil.getLabel("Invoice.label");
    g.drawString(nlabel + " : " + invoice.getNumber(), margin, top + 100);
    // référence
    g.drawString("Ref. : " + invoice.getReference(), left, top + 100);
    // émetteur
    g.drawString(BundleUtil.getLabel("Issuer.label") + " : " + invoice.getUser().getFirstnameName(), margin, top + 120);
    // description
    g.drawString(BundleUtil.getLabel("Invoice.description.label") + " : " + invoice.getDescription(), margin, top + 140);

    int tableY = top + 160;
    int tabletop = tableY;
    int end = margin + InvoiceItemElement.TABLE_WIDTH;
    // entete tableau
    new InvoiceHeaderElement(margin, tableY).draw(g);
    g.drawLine(margin, tableY + 20, margin + InvoiceItemElement.TABLE_WIDTH, tableY + 20);
    // items
    tableY += 5;
    for (InvoiceItem af : invoice.getItems()) {
      InvoiceItemElement item = new InvoiceItemElement(margin, tableY + 20, af);
      item.draw(g);
      tableY += 20;
    }
    tableY += 5;
    int tablebottom = tableY + 20;
    // encadrement du tableau d'items
    g.drawRect(margin, tabletop, InvoiceItemElement.TABLE_WIDTH, tablebottom - tabletop);
    // lignes séparatrices verticales des colonnes
    g.drawLine(InvoiceItemElement.xColPrix, tabletop, InvoiceItemElement.xColPrix, tablebottom); // colonne prix
    g.drawLine(InvoiceItemElement.xColTva, tabletop, InvoiceItemElement.xColTva, tablebottom); // colonne tva
    g.drawLine(InvoiceItemElement.xColQte, tabletop, InvoiceItemElement.xColQte, tablebottom); // colonne quantité
    g.drawLine(InvoiceItemElement.xColHT, tabletop, InvoiceItemElement.xColHT, tablebottom); // colonne total HT

    // pied tableau
    new InvoiceFooterElement(margin, tablebottom + 20, invoice).draw(g);
    // infos légales
    drawFooter(g, margin, bottom);

    return PAGE_EXISTS;
  }

  /**
   * Prints the invoice.
   */
  public void print() {

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
   * @param f invoice
   * @return a name or null
   */
  private String getEtabName(Quote f) {
    Establishment e = EstablishmentIO.findId(f.getEstablishment(), dc);
    return e == null ? "" : e.getName();
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
