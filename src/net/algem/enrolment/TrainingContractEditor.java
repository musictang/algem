/*
 * @(#) TrainingContractEditor.java Algem 2.15.0 12/09/17
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

import com.itextpdf.text.DocumentException;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.algem.accounting.AccountUtil;
import net.algem.config.Company;
import net.algem.config.PageTemplate;
import net.algem.contact.Address;
import net.algem.contact.Email;
import net.algem.contact.OrganizationIO;
import net.algem.contact.PersonFile;
import net.algem.contact.Telephone;
import net.algem.planning.DateFrField;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GemToolBar;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 30/08/2017
 */
public class TrainingContractEditor
  extends JDialog
  implements ActionListener {

  private JTextField trainingLabel;
  private JTextField funding;
  private JTextField season;
  private JFormattedTextField total;
  private JFormattedTextField amount;
  private DateFrField startDate;
  private DateFrField endDate;
  private DateFrField signDate;
  private JFormattedTextField internalLength;
  private JFormattedTextField length;
  private GemButton btSave;
  private GemButton btCancel;
  private GemButton btPreview;
  private GemButton btCertificate;

  private TrainingContract contract;
  private final PersonFile dossier;
  private final TrainingContractHistory history;
  private final TrainingService trainingService;
  private final GemDesktop desktop;

  public TrainingContractEditor(TrainingContractHistory history, TrainingService trainingService, PersonFile dossier, GemDesktop desktop) {
    super(desktop.getFrame(), BundleUtil.getLabel("Training.contract.label"), false);
    this.trainingService = trainingService;
    this.history = history;
    this.dossier = dossier;
    this.desktop = desktop;
  }

  public void createUI() {
    GemToolBar bar = new GemToolBar();
    bar.setFloatable(false);
    btPreview = bar.addIcon(BundleUtil.getLabel("Training.contract.pdf.icon"),
      GemCommand.PRINT_CMD,
      BundleUtil.getLabel("Preview.pdf.label"));
    btPreview.addActionListener(this);

    btCertificate = bar.addIcon(BundleUtil.getLabel("Training.contract.certificate.icon"),
      BundleUtil.getLabel("Training.contract.certificate.label"),
      BundleUtil.getLabel("Training.contract.certificate.tip"));
    btCertificate.addActionListener(this);

    JPanel p = new JPanel(new GridBagLayout());
    trainingLabel = new JTextField(20);
    startDate = new DateFrField();
    endDate = new DateFrField();
    funding = new JTextField(20);
    season = new JTextField(20);
    NumberFormat nf = AccountUtil.getNumberFormat(2, 2);
    int minSize = 100;
    total = new JFormattedTextField(nf);
    total.setPreferredSize(new Dimension(minSize, total.getPreferredSize().height));
    amount = new JFormattedTextField(nf);

    amount.setPreferredSize(new Dimension(minSize, amount.getPreferredSize().height));
    internalLength = new JFormattedTextField(AccountUtil.getDefaultNumberFormat());
    internalLength.setPreferredSize(new Dimension(minSize, internalLength.getPreferredSize().height));
    length = new JFormattedTextField(AccountUtil.getDefaultNumberFormat());
    length.setPreferredSize(new Dimension(minSize, length.getPreferredSize().height));

    signDate = new DateFrField();

    GridBagHelper gb = new GridBagHelper(p);
    gb.add(new JLabel(BundleUtil.getLabel("Heading.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Season.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Start.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("End.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Funding.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    JLabel totalLabel = new JLabel(BundleUtil.getLabel("Total.label"));
    totalLabel.setToolTipText(BundleUtil.getLabel("Training.total.amount.tip"));
    gb.add(totalLabel, 0, 5, 1, 1, GridBagHelper.WEST);
    JLabel amountLabel = new JLabel(BundleUtil.getLabel("Amount.label"));
    amountLabel.setToolTipText(BundleUtil.getLabel("Amount.supported.tip"));
    gb.add(amountLabel, 0, 6, 1, 1, GridBagHelper.WEST);

    JLabel intVolumeLabel = new JLabel(BundleUtil.getLabel("Internal.training.length.label"));
    intVolumeLabel.setToolTipText(BundleUtil.getLabel("Internal.training.length.tip"));
    gb.add(intVolumeLabel, 0, 7, 1, 1, GridBagHelper.WEST);
    JLabel extVolumeLabel = new JLabel(BundleUtil.getLabel("External.training.length.label"));
    extVolumeLabel.setToolTipText(BundleUtil.getLabel("External.training.length.tip"));
    gb.add(extVolumeLabel, 0, 8, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Signature.date.label")), 0, 9, 1, 1, GridBagHelper.WEST);

    gb.add(trainingLabel, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(season, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(startDate, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(endDate, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(funding, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(total, 1, 5, 1, 1, GridBagHelper.WEST);
    gb.add(amount, 1, 6, 1, 1, GridBagHelper.WEST);
    gb.add(internalLength, 1, 7, 1, 1, GridBagHelper.WEST);
    gb.add(length, 1, 8, 1, 1, GridBagHelper.WEST);
    gb.add(signDate, 1, 9, 1, 1, GridBagHelper.WEST);

    setLayout(new BorderLayout());

    GemPanel buttons = new GemPanel(new GridLayout(1, 2));
    btSave = new GemButton(GemCommand.SAVE_CMD);
    btSave.addActionListener(this);

    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    buttons.add(btSave);
    buttons.add(btCancel);

    add(bar, BorderLayout.NORTH);
    add(p, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setSize(new Dimension(420, 440));

    setLocationRelativeTo(desktop.getFrame());
    setVisible(true);

  }

  public void setContract(TrainingContract c) {
    this.contract = c;
    if (c != null) {
      trainingLabel.setText(c.getLabel());
      season.setText(c.getSeason());
      startDate.setDate(c.getStart());
      endDate.setDate(c.getEnd());
      total.setValue(c.getTotal());
      internalLength.setValue(c.getInternalVolume());
      signDate.setDate(c.getSignDate());
      if (c.getId() > 0) {
        funding.setText(c.getFunding());
        amount.setValue(c.getAmount());
        length.setValue(c.getExternalVolume());
      }
    }

  }

  private TrainingContract getContract() {
    TrainingContract c = new TrainingContract();
    c.setId(contract.getId());
    c.setType((byte) 2);
    c.setOrderId(contract.getOrderId());
    c.setPersonId(contract.getPersonId());
    c.setLabel(trainingLabel.getText());
    c.setSeason(season.getText().trim());
    c.setStart(startDate.getDate());
    c.setEnd(endDate.getDate());
    c.setSignDate(signDate.getDate());
    c.setFunding(funding.getText().trim());
    c.setTotal(total.getValue() == null ? 0.0 : ((Number) total.getValue()).doubleValue());
    c.setAmount(amount.getValue() == null ? 0.0 : ((Number) amount.getValue()).doubleValue());
    c.setInternalVolume(internalLength.getValue() == null ? 0.0f : ((Number) internalLength.getValue()).floatValue());
    c.setExternalVolume(length.getValue() == null ? 0.0f : ((Number) length.getValue()).floatValue());

    return c;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    TrainingContract t = null;
    try {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if (src == btSave) {
        t = getContract();
        if (t.getId() == 0) {
          trainingService.createContract(t);
          history.updateHistory(t, true);
        } else {
          trainingService.updateContract(t);
          history.updateHistory(t, false);
        }

      } else if (src == btPreview) {
        t = getContract();
        if (t.getId() == 0) {
          MessagePopup.warning(this, MessageUtil.getMessage("contract.not.saved.warning"));
          return;
        }
        //preview(fillProperties(getContract()), "contrat", PageTemplate.CONTRACT_PAGE_MODEL);
        trainingService.preview(fillProperties(getContract()), "contrat", PageTemplate.CONTRACT_PAGE_MODEL, dossier.getId());
      } else if (src == btCertificate) {
        t = getContract();
        if (t.getId() == 0) {
          MessagePopup.warning(this, MessageUtil.getMessage("contract.not.saved.warning"));
          return;
        }
        //preview(fillProperties(getContract()),"attestation", PageTemplate.CONTRACT_PAGE_MODEL);
        trainingService.preview(fillProperties(getContract()), "attestation", PageTemplate.CONTRACT_PAGE_MODEL, dossier.getId());
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    } catch (DocumentException | IOException ex) {
      GemLogger.logException(ex);
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }
    close();
  }

  /*private void preview(Properties props, String fileName, short templateKey) throws DocumentException, IOException {
    InputStream tpl = getClass().getResourceAsStream("/resources/doc/"+fileName+".html");
    if (tpl == null) {
      tpl = getClass().getResourceAsStream("/resources/doc/def/"+fileName+".html");
    }
    if (tpl == null) {
      return;
    }
    String content = scanContent(tpl, props);
    tpl = new ByteArrayInputStream(content.getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    //step 1
    Document doc = new Document(PageSize.A4);
    doc.setMargins(40, 40, 40, 40);

    //step 2
    PdfWriter writer = PdfWriter.getInstance(doc, out);
//    writer.addViewerPreference(PdfName.PRINTSCALING, PdfName.NONE);
//    writer.addViewerPreference(PdfName.PRINTSCALING, PdfName.FIT);
//    writer.addViewerPreference(PdfName.DUPLEX, PdfName.DUPLEXFLIPLONGEDGE);
    doc.open();
    // step 4
    //XMLWorkerHelper.getInstance().parseXHtml(writer, document, tpl);
    PdfHandler handler = trainingService.getPdfHandler();
    handler.createParser(doc, writer).parse(tpl);
    // step 5
    doc.close();
    handler.createPdf(fileName+"-" + dossier.getId() + "_", out, templateKey);
  }*/

  private Properties fillProperties(TrainingContract contract) throws SQLException {
    Properties props = new Properties();
    Company comp = new OrganizationIO(DataCache.getDataConnection()).getDefault();
    Address compAddress = comp.getContact().getAddress();
    String city = (compAddress != null ? (compAddress.getCity() != null ? compAddress.getCity() : "") : "");
    NumberFormat nf = AccountUtil.getNumberFormat(2, 2);
    DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    props.put("__student_name__", dossier.getContact().getFirstnameName());
    props.put("__student_birth__", dossier.getMember().getBirth().toString());
    props.put("__student_address__", dossier.getContact().getAddress() == null ? "NC" : dossier.getContact().getAddress().toString());
    props.put("__company_name__", comp.getOrg().getName());
    props.put("__company_ref__", comp.getReferent().getFirstnameName());
    props.put("__company_address__", compAddress == null ? "NC" : compAddress.toString());
    List<Telephone> tels = comp.getContact().getTele();
    props.put("__company_tel__", tels != null && tels.size() > 0 ? tels.get(0).getNumber() : "NC");
    List<Email> emails = comp.getContact().getEmail();
    props.put("__company_email__", emails != null && emails.size() > 0 ? emails.get(0).getEmail() : "NC");
    props.put("__company_fpcode__", comp.getOrg().getFpCode());
    props.put("__company_siret__", comp.getOrg().getSiret());
    props.put("__company_ape__", comp.getOrg().getNafCode());
    props.put("__company_city__", city);
    props.put("__company_stamp__", getStampPath(comp));
    props.put("__training_title__", contract.getLabel());
    props.put("__season__", contract.getSeason());
    props.put("__training_start__", df.format(contract.getStart()));
    props.put("__training_end__", df.format(contract.getEnd()));
    props.put("__internal_volume__", nf.format(contract.getInternalVolume()));
    props.put("__external_volume__", nf.format(contract.getExternalVolume()));
    props.put("__funding__", contract.getFunding());
    props.put("__total_cost__", nf.format(contract.getTotal()));
    props.put("__total_funding__", nf.format(contract.getAmount()));
    double rest = contract.getTotal() - contract.getAmount();
    props.put("__total_student__", nf.format(rest));
    props.put("__date_sign__", df.format(contract.getSignDate()));

    return props;
  }

  private String getStampPath(Company comp) {
    try {
      File logo = File.createTempFile("comp-stamp_", ".png");
      byte[] data = comp.getStamp();
      if (data == null) {
        return "";
      }
      ByteArrayInputStream in = new ByteArrayInputStream(data);
      BufferedImage img = ImageIO.read(in);

      ImageIO.write(img, "png", logo);
      return logo.getPath();
    } catch (IOException ex) {
      GemLogger.logException(ex);
      return "";
    }
  }

  private void close() {
    setVisible(false);
    dispose();
  }
}
