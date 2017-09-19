/*
 * @(#) TrainingService.java Algem 2.15.0 19/09/17
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
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import net.algem.config.PageTemplateIO;
import net.algem.contact.Organization;
import net.algem.contact.OrganizationIO;
import net.algem.course.Module;
import net.algem.edition.PdfHandler;
import net.algem.util.DataConnection;
import net.algem.util.FileUtil;
import net.algem.util.MessageUtil;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 06/09/2017
 */
public class TrainingService {

  private final TrainingContractIO contractIO;
  private final TrainingAgreementIO agreementIO;
  private final OrganizationIO orgIO;
  private final PageTemplateIO templateIO;

  public TrainingService(DataConnection dc) {
    this.contractIO = new TrainingContractIO(dc);
    this.agreementIO = new TrainingAgreementIO(dc);
    this.orgIO = new OrganizationIO(dc);
    this.templateIO = new PageTemplateIO(dc);
  }

  public List<TrainingContract> findContracts(int idper) throws SQLException {
    return contractIO.findAll(idper);
  }

  public List<TrainingAgreement> findAgreements(int idper) throws SQLException {
    return agreementIO.findAll(idper);
  }

  public Organization[] getOrganizations() throws SQLException {
    List<Organization> orgs = orgIO.findAll();
    Organization[] orgArray = new Organization[orgs.size()];
    return orgs.toArray(orgArray);
  }

  public Module getModule(int orderId) throws SQLException {
    return contractIO.getModuleInfo(orderId);
  }

  public float getVolume(int orderId) throws SQLException {
    return contractIO.getVolume(orderId);
  }

  public void createContract(TrainingContract t) throws SQLException {
    contractIO.create(t);
  }

  public void updateContract(TrainingContract t) throws SQLException {
    contractIO.update(t);
  }

  public void deleteContract(int id) throws SQLException {
    contractIO.delete(id);
  }

  public void createAgreement(TrainingAgreement t) throws SQLException {
    agreementIO.create(t);
  }

  public void updateAgreement(TrainingAgreement t) throws SQLException {
    agreementIO.update(t);
  }

  public void deleteAgreement(int id) throws SQLException {
    agreementIO.delete(id);
  }

  void preview(Properties props, String fileName, short templateKey, int idper) throws DocumentException, IOException {
    InputStream tpl = getClass().getResourceAsStream("/resources/doc/tpl/" + fileName + ".html");
    String defPath = "/resources/doc/tpl/def/" + fileName + ".html";
    if (tpl == null) {
      tpl = getClass().getResourceAsStream(defPath);
    }
    if (tpl == null) {
      MessagePopup.warning(null, MessageUtil.getMessage("html.template.not.found.warning", fileName + ".html"));
      return;
    }
    String content = FileUtil.scanContent(tpl, props);
    //tpl = new ByteArrayInputStream(content.getBytes());
    tpl = new ByteArrayInputStream(content.getBytes("UTF-8"));

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    //step 1
    Document doc = new Document(PageSize.A4);
    doc.setMargins(40, 40, 40, 40);

    //step 2
    /*OutputStream printStream = new PrintStream(out, true, "UTF-8");
    PdfWriter writer = PdfWirter.getInstance(doc, printStream);*/
    PdfWriter writer = PdfWriter.getInstance(doc, out);
    //writer.addViewerPreference(PdfName.PRINTSCALING, PdfName.NONE);
    //writer.addViewerPreference(PdfName.PRINTSCALING, PdfName.FIT);
    //writer.addViewerPreference(PdfName.DUPLEX, PdfName.DUPLEXFLIPLONGEDGE);
    doc.open();
    // step 4
    PdfHandler handler = new PdfHandler(templateIO);
    // IMPORTANT !! : set Charset here
    handler.createParser(doc, writer).parse(tpl, Charset.forName("UTF-8"));
    // step 5
    doc.close();
    handler.createPdf(fileName + "-" + idper + "_", out, templateKey);
  }

}
