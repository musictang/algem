/*
 * @(#) TrainingAgreementEditor.java Algem 2.15.0 06/09/17
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

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.algem.accounting.AccountUtil;
import net.algem.config.Company;
import net.algem.config.PageTemplate;
import net.algem.config.PageTemplateIO;
import net.algem.contact.Address;
import net.algem.contact.Email;
import net.algem.contact.Organization;
import net.algem.contact.OrganizationIO;
import net.algem.contact.Person;
import net.algem.contact.PersonFile;
import net.algem.contact.Telephone;
import net.algem.edition.PdfHandler;
import net.algem.planning.DateFrField;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
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
public class TrainingAgreementEditor
  extends JDialog
  implements ActionListener {

  private JTextField trainingLabel;
  private JTextField orgName;
  private JComboBox<Organization> org;
  private JTextField season;
  private JTextField insurance;
  private JTextField insuranceRef;
  private DateFrField startDate;
  private DateFrField endDate;
  private DateFrField signDate;

  private GemButton btSave;
  private GemButton btCancel;
  private GemButton btPreview;
  private GemButton btCertificate;

  private TrainingAgreement agreement;
  private final PersonFile dossier;
  private final TrainingAgreementHistory history;
  private final TrainingService trainingService;
  private final GemDesktop desktop;

  public TrainingAgreementEditor(TrainingAgreementHistory history, TrainingService trainingService, PersonFile dossier, GemDesktop desktop) {
    super(desktop.getFrame(), BundleUtil.getLabel("Internship.agreements.label"), false);
    this.trainingService = trainingService;
    this.history = history;
    this.dossier = dossier;
    this.desktop = desktop;
  }

  public void createUI() {
    GemToolBar bar = new GemToolBar();
    bar.setFloatable(false);
    btPreview = bar.addIcon(BundleUtil.getLabel("Training.contract.print.icon"),
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
//    orgName = new JTextField(20);
    try {
      org = new JComboBox<>(trainingService.getOrganizations());
    } catch (SQLException ex) {
      org.addItem(new Organization());
    }
    season = new JTextField(20);
    insurance = new JTextField(20);
    insurance.setToolTipText(BundleUtil.getLabel("Insurance.tip"));
    insuranceRef = new JTextField(20);
    insurance.setToolTipText(BundleUtil.getLabel("Insurance.ref.tip"));
    signDate = new DateFrField();

    GridBagHelper gb = new GridBagHelper(p);
    gb.add(new JLabel(BundleUtil.getLabel("Heading.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Season.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Start.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("End.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Organization.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Insurance.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Insurance.ref.label")), 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Signature.date.label")), 0, 7, 1, 1, GridBagHelper.WEST);

    gb.add(trainingLabel, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(season, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(startDate, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(endDate, 1, 3, 1, 1, GridBagHelper.WEST);
    org.setPreferredSize(new Dimension(season.getPreferredSize().width, org.getPreferredSize().height));
    gb.add(org, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(insurance, 1, 5, 1, 1, GridBagHelper.WEST);
    gb.add(insuranceRef, 1, 6, 1, 1, GridBagHelper.WEST);
    gb.add(signDate, 1, 7, 1, 1, GridBagHelper.WEST);

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
    setSize(GemModule.S_SIZE);

    setLocationRelativeTo(desktop.getFrame());
    setVisible(true);

  }

  public void setAgreement(TrainingAgreement t) {
    this.agreement = t;
    if (t != null) {
      trainingLabel.setText(t.getLabel());
      season.setText(t.getSeason());
      startDate.setDate(t.getStart());
      endDate.setDate(t.getEnd());
      insurance.setText(t.getInsurance());
      insuranceRef.setText(t.getInsuranceRef());
      signDate.setDate(t.getSignDate());
      if (t.getId() > 0) {
          org.setSelectedItem(t.getOrg());
      }
    }
  }

  private TrainingAgreement getAgreement() {
    TrainingAgreement t = new TrainingAgreement();
    t.setId(agreement.getId());
    t.setType((byte) 2);
    t.setOrg((Organization) org.getSelectedItem());
    t.setPersonId(agreement.getPersonId());
    t.setLabel(trainingLabel.getText());
    t.setSeason(season.getText().trim());
    t.setStart(startDate.getDate());
    t.setEnd(endDate.getDate());
    t.setInsurance(insurance.getText());
    t.setInsuranceRef(insuranceRef.getText());

    return t;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    TrainingAgreement t = null;
    try {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if (src == btSave) {
        t = getAgreement();
        if (t.getId() == 0) {
          trainingService.createAgreement(t);
          history.updateHistory(t, true);
        } else {
          trainingService.updateAgreement(t);
          history.updateHistory(t, false);
        }

      } else if (src == btPreview) {
        t = getAgreement();
        if (t.getId() == 0) {
          MessagePopup.warning(this, MessageUtil.getMessage("contract.not.saved.warning"));
          return;
        }
        preview(fillProperties(getAgreement()));
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

  private void preview(Properties props) throws DocumentException, IOException {
    InputStream tpl = getClass().getResourceAsStream("/resources/doc/model/custom/convention.html");
    if (tpl == null) {
      tpl = getClass().getResourceAsStream("/resources/doc/model/convention.html");
    }
    if (tpl == null) {
      return;
    }
    Scanner scanner = new Scanner(tpl);
    StringBuilder content = new StringBuilder();
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      for (Map.Entry<Object, Object> entry : props.entrySet()) {
        String k = (String) entry.getKey();
        String v = (String) entry.getValue();
        if (line.contains(k)) {
          line = line.replaceAll(k, v);
        }
      }
      content.append(line);
    }
    tpl = new ByteArrayInputStream(content.toString().getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    //step 1
    Document document = new Document();
    document.setMargins(40, 40, 40, 40);
    //step 2
    PdfWriter writer = PdfWriter.getInstance(document, out);
    document.open();
    // step 4
    //XMLWorkerHelper.getInstance().parseXHtml(writer, document, tpl);
    PdfHandler handler = new PdfHandler(new PageTemplateIO(DataCache.getDataConnection()));
    handler.createParser(document, writer).parse(tpl);
    // step 5
    document.close();
    handler.createPdf("convention-" + dossier.getId() + "_", out, PageTemplate.AGREEMENT_PAGE_MODEL);
  }

  private Properties fillProperties(TrainingAgreement agreement) throws SQLException {
    Properties props = new Properties();
    Company comp = new OrganizationIO(DataCache.getDataConnection()).getDefault();
    Address compAddress = comp.getContact().getAddress();
    String city = (compAddress != null ? (compAddress.getCity() != null ? compAddress.getCity() : "") : "");
    NumberFormat nf = AccountUtil.getNumberFormat(2, 2);
    DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    Organization o = agreement.getOrg();
    if (o != null) {
      props.put("__org_name__", o.getName());
      Person ref = (Person) DataCache.findId(o.getReferent(), Model.Person);
      PersonFile pf = (PersonFile) DataCache.findId(o.getId(), Model.PersonFile);
      props.put("__org_ref__", ref.getFirstnameName() == null ? "NC" : ref.getFirstnameName());
      props.put("__org_siret__", o.getSiret() == null ? "NC" : o.getSiret());
      props.put("__org_ape__", o.getNafCode() == null ? "NC" : o.getNafCode());
      
      props.put("__org_address__", pf.getContact().getAddress() == null ? "NC" : ref.getFirstnameName());
      List<Telephone> pTels = pf.getContact().getTele();
      props.put("__org_tel__", pTels != null && pTels.size() > 0 ? pTels.get(0).getNumber() : "NC");
      List<Email> pMails = pf.getContact().getEmail();
      props.put("__org_mail__", pMails != null && pMails.size() > 0 ? pMails.get(0).getEmail() : "NC");
    }

    props.put("__student_name__", dossier.getContact().getFirstnameName());
    props.put("__student_birth__", dossier.getMember().getBirth().toString());
    props.put("__student_address__", dossier.getContact().getAddress() == null ? "NC" : dossier.getContact().getAddress().toString());
    props.put("__insurance__", dossier.getMember() == null ? "NC" : dossier.getMember().getInsurance());
    props.put("__insurance_ref__", dossier.getMember() == null ? "NC" : dossier.getMember().getInsuranceRef());
    
    Organization compOrg = comp.getOrg();
    props.put("__company_name__", compOrg.getName());
    props.put("__company_ref__", comp.getReferent() == null ? "" : comp.getReferent().getFirstnameName());
    props.put("__company_address__", compAddress == null ? "NC" : compAddress.toString());
    List<Telephone> tels = comp.getContact().getTele();
    props.put("__company_tel__", tels != null && tels.size() > 0 ? tels.get(0).getNumber() : "NC");
    List<Email> emails = comp.getContact().getEmail();
    props.put("__company_mail__", emails != null && emails.size() > 0 ? emails.get(0).getEmail() : "NC");
    props.put("__company_fpcode__", compOrg.getFpCode() == null ? "NC" : compOrg.getFpCode());
    props.put("__company_siret__", compOrg.getSiret() == null ? "NC" : compOrg.getSiret());
    props.put("__company_ape__", compOrg.getNafCode() == null ? "NC" : compOrg.getNafCode());
    props.put("__company_city__", city);
    props.put("__company_stamp__", getStampPath(comp));
    props.put("__training_title__", agreement.getLabel());
    props.put("__season__", agreement.getSeason());
    props.put("__training_start__", df.format(agreement.getStart()));
    props.put("__training_end__", df.format(agreement.getEnd()));

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
