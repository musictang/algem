/*
 * @(#)WishInformationLetter.java 2.17.0 20/03/19
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
 *
 */
package net.algem.edition;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.algem.config.PageTemplate;
import net.algem.config.PageTemplateIO;
import net.algem.contact.Person;
import net.algem.planning.wishes.EnrolmentWish;
import net.algem.util.ImageUtil;

/**
 * Wish item element.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0
 * @since 2.17.0 20/03/19
 */
public class WishInformationLetter {

    public static final int DATE_X = 450; //TODO en properties ?
    public static final int DATE_Y = 100;
    public static final int STUDENT_X = 100;
    public static final int STUDENT_Y = 212;
    public static final int PERIODE_X1 = 305;
    public static final int PERIODE_Y1 = 158;
    public static final int PERIODE_X2 = 395;
    public static final int PERIODE_Y2 = 613;
    public static final int WISHTABLE_X = 50;
    public static final int WISHTABLE_Y = 180;

    public static final int MARGIN = 50;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final Font titleFont = new Font(Font.SERIF, Font.BOLD, 12);
    private static final Font textFont = new Font(Font.SERIF, Font.PLAIN, 10);
    private static final Font studentFont = new Font(Font.SERIF, Font.BOLD, 12);
    private static final Font tableFont = new Font(Font.SERIF, Font.PLAIN, 10);

    private PdfHandler pdfHandler;
    private PageTemplateIO ptio;
    private String periode;
    private Person student;
    private List<EnrolmentWish> wishes;

    public WishInformationLetter(PageTemplateIO ptio, String periode, Person student, List<EnrolmentWish> wishes) {
        this.periode = periode;
        this.student = student;
        this.wishes = wishes;

        pdfHandler = new PdfHandler(ptio);

    }

    public String toPDF() throws DocumentException, IOException {

        String date = LocalDate.now().format(dateFormatter);

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfContentByte cb = writer.getDirectContent();
        PdfGraphics2D g = new PdfGraphics2D(cb, document.getPageSize().getWidth(), document.getPageSize().getHeight());

        //int top = ImageUtil.mmToPoints(50);
        int margin = ImageUtil.mmToPoints(15);

        g.setFont(titleFont);
        g.drawString(periode, PERIODE_X1, PERIODE_Y1);
        g.setFont(textFont);
        //g.drawString(periode, PERIODE_X2, PERIODE_Y2);
        //g.drawString(date, DATE_X, DATE_Y);
        g.setFont(studentFont);
        g.drawString(student.getFirstnameName(), STUDENT_X, STUDENT_Y);

        g.setFont(tableFont);
        drawContent(g, wishes, WISHTABLE_Y, margin);

        g.dispose();
        
        document.close();

        String tmpFileName = "lettre-reinscription-" + student.getId() + "_";
        return pdfHandler.createPdf(tmpFileName, outputStream, PageTemplate.ENROLMENTWISH_PAGE_MODEL, false); 
    }

    private int drawContent(Graphics g, List<EnrolmentWish> wishes, int top, int margin) {
        int tableY = top + 160;
        int tabletop = tableY;

        new WishHeaderElement(margin, tableY).draw(g);
        g.drawLine(margin, tableY + 20, margin + WishItemElement.TABLE_WIDTH, tableY + 20);

        tableY += 5;
        for (EnrolmentWish wish : wishes) {
            WishItemElement item = new WishItemElement(margin, tableY + 20, wish);
            item.draw(g);
            tableY = tableY + 20 + item.getOffset();
        }
        tableY += 5;
        int tablebottom = tableY + 20;

        // encadrement du tableau d'items
        g.drawRect(margin, tabletop, WishItemElement.TABLE_WIDTH, tablebottom - tabletop);
        // lignes séparatrices verticales des colonnes
        g.drawLine(WishItemElement.xColCourse, tabletop, WishItemElement.xColCourse, tablebottom); // colonne discipline
        g.drawLine(WishItemElement.xColDay, tabletop, WishItemElement.xColDay, tablebottom); // colonne jour
        g.drawLine(WishItemElement.xColHour, tabletop, WishItemElement.xColHour, tablebottom); // colonne heure
        g.drawLine(WishItemElement.xColDuration, tabletop, WishItemElement.xColDuration, tablebottom); // colonne durée
        g.drawLine(WishItemElement.xColTeacher, tabletop, WishItemElement.xColTeacher, tablebottom); // colonne prof
        return tablebottom;

    }

}
