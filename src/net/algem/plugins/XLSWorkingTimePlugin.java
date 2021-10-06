/*
 * @(#) XLSWorkingTimePlugin.java Algem 2.17.3 24/02/2020
 *                                  2.11.3 29/11/16
 *
 * Copyright (c) 1999-2020 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem.
 * Algem is free software: you can redistribute it AND/or modify it
 * under the terms of the GNU Affero General Public License AS published by
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
package net.algem.plugins;

import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.algem.accounting.OrderLineIO;
import net.algem.contact.Person;
import net.algem.contact.PersonIO;
import net.algem.course.ModuleIO;
import net.algem.edition.HoursTaskExecutor;
import net.algem.enrolment.CourseOrderIO;
import net.algem.enrolment.ModuleOrderIO;
import net.algem.enrolment.OrderIO;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.planning.ScheduleRangeIO;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.3
 * @since 2.10.0 03/06/2016
 */
public class XLSWorkingTimePlugin
        extends HoursTaskExecutor {

    private HSSFWorkbook wb;
    private Sheet feuille;
    private HSSFCellStyle hourStyle;
    private HSSFCellStyle hourTotalStyle;
    private HSSFCellStyle teacherStyle;
    private HSSFCellStyle dayStyle;
    private HSSFCellStyle totalPeriodStyle;
    private HSSFCellStyle hourPeriodStyle;
    private HSSFCellStyle headerStyle;
    private HSSFCellStyle meetingStyle;
    private HSSFCellStyle coordStyle;
    private HSSFDataFormat dataFormat;
    private short nrow;
    private String totaldXL = "";
    private String totaldXP = "";
    private String totaldXR = "";
    private String totaldXC = "";

    private final int CODE_LOISIR = 1;
    private CustomDAO dao;

    private String[] categories = {
        "DEM + L1 + L2 + L3 + Loisirs",
        "Parcours BREVET + MIMA + FP",
        "Réunions",
        "Coordination"
    };

    public XLSWorkingTimePlugin() {
        dao = new CustomDAO(DataCache.getDataConnection());
    }

    @Override
    public String getLabel() {
        return "Heures Jazz à Tours";
    }

    @Override
    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tri par professeur :\n");
        for (String c : categories) {
            sb.append(c).append('\n');
        }
        return sb.toString();
    }

    private Double getHour(int min) {
        double res = ((min % 60) * 100) / 60;
        res = res / 100;
        res = res + (min / 60);
        return Double.valueOf(res);

    }

    private void printXLSResult(Person t, int total1, int total2, int total3, int total4) {
        Row row = feuille.createRow(nrow++);
        row.setHeightInPoints(25);
        Cell cell = row.createCell(0);
        cell.setCellValue(t.getFirstnameName());
        cell.setCellStyle(teacherStyle);

        row = feuille.createRow(nrow++);
        int firstRow = nrow;
        cell = row.createCell(0);
        cell.setCellValue(categories[0]);
        cell = row.createCell(1);
        cell.setCellValue((Double) getHour(total1));
        cell.setCellStyle(hourStyle);

        row = feuille.createRow(nrow++);
        cell = row.createCell(0);
        cell.setCellValue(categories[1]);
        cell = row.createCell(1);
        cell.setCellValue((Double) getHour(total2));
        cell.setCellStyle(hourStyle);

        row = feuille.createRow(nrow++);
        cell = row.createCell(0);
        cell.setCellValue(categories[2]);
        cell = row.createCell(1);
        cell.setCellValue((Double) getHour(total3));
        cell.setCellStyle(hourStyle);

        row = feuille.createRow(nrow++);
        cell = row.createCell(0);
        cell.setCellValue(categories[3]);
        cell = row.createCell(1);
        cell.setCellValue((Double) getHour(total4));
        cell.setCellStyle(hourStyle);

        row = feuille.createRow(nrow++);
        cell = row.createCell(0);
        cell.setCellValue("TOTAL");
        cell = row.createCell(1);
        cell.setCellStyle(hourStyle);
        cell.setCellFormula("SUM(B" + firstRow + ":B" + (firstRow + 3) + ")");
    }

    private void printXLSResult(DateFr d, int total1, int total2, int total3, int total4) {
        Row row = feuille.createRow(nrow++);
        Cell cell = row.createCell(0);
        cell.setCellValue(d.toString());
        cell.setCellStyle(dayStyle);

        row = feuille.createRow(nrow++);
        int firstRow = nrow;
        cell = row.createCell(0);
        cell.setCellValue(categories[0]);
        cell = row.createCell(1);
        cell.setCellValue((Double) getHour(total1));
        cell.setCellStyle(hourStyle);
        totaldXL += "B" + nrow + ",";

        row = feuille.createRow(nrow++);
        cell = row.createCell(0);
        cell.setCellValue(categories[1]);
        cell = row.createCell(1);
        cell.setCellValue((Double) getHour(total2));
        cell.setCellStyle(hourStyle);
        totaldXP += "B" + nrow + ",";

        row = feuille.createRow(nrow++);
        cell = row.createCell(0);
        cell.setCellValue(categories[2]);
        cell = row.createCell(1);
        cell.setCellValue((Double) getHour(total3));
        cell.setCellStyle(total3 > 0 ? meetingStyle : hourStyle);
        totaldXR += "B" + nrow + ",";

        row = feuille.createRow(nrow++);
        cell = row.createCell(0);
        cell.setCellValue(categories[3]);
        cell = row.createCell(1);
        cell.setCellValue((Double) getHour(total4));
        cell.setCellStyle(total4 > 0 ? coordStyle : hourStyle);
        totaldXC += "B" + nrow + ",";

        row = feuille.createRow(nrow++);
        cell = row.createCell(0);
        cell.setCellValue("TOTAL " + d.toString());
        cell = row.createCell(1);
        cell.setCellStyle(hourTotalStyle);
        cell.setCellFormula("SUM(B" + firstRow + ":B" + (firstRow + 3) + ")");
    }

    private void printXLSResult() {
        Row row = feuille.createRow(nrow++);
        int firstRow = nrow;
        Cell cell = row.createCell(0);
        cell.setCellValue("TOTAL " + categories[0]);
        cell = row.createCell(1);
        cell.setCellFormula("SUM(" + totaldXL + ")");
        cell.setCellStyle(hourStyle);

        row = feuille.createRow(nrow++);
        cell = row.createCell(0);
        cell.setCellValue("TOTAL " + categories[1]);
        cell = row.createCell(1);
        cell.setCellFormula("SUM(" + totaldXP + ")");
        cell.setCellStyle(hourStyle);

        row = feuille.createRow(nrow++);
        cell = row.createCell(0);
        cell.setCellValue("TOTAL " + categories[2]);
        cell = row.createCell(1);
        cell.setCellFormula("SUM(" + totaldXR + ")");
        cell.setCellStyle(meetingStyle);

        row = feuille.createRow(nrow++);
        cell = row.createCell(0);
        cell.setCellValue("TOTAL " + categories[3]);
        cell = row.createCell(1);
        cell.setCellFormula("SUM(" + totaldXC + ")");
        cell.setCellStyle(coordStyle);

        row = feuille.createRow(nrow++);
        cell = row.createCell(0);
        cell.setCellValue("TOTAL PERIODE");
        cell.setCellStyle(totalPeriodStyle);

        cell = row.createCell(1);
        cell.setCellStyle(hourPeriodStyle);
        cell.setCellFormula("SUM(B" + firstRow + ":B" + (firstRow + 3) + ")");

    }

    @Override
    public void execute() {
        System.out.println("XLSWorkingTimePlugins.execute");
        out.close(); //from HourEmployeeDlg
        try {
            wb = new HSSFWorkbook();
            feuille = wb.createSheet("Heures Profs");
            feuille.setColumnWidth(0, 42 * 256);
            hourStyle = wb.createCellStyle();
            dataFormat = wb.createDataFormat();
            HSSFFont nf = wb.createFont();
            nf.setFontName("monospace");

            HSSFFont bf = wb.createFont();
            bf.setFontName("monospace");
            //    bf.setFontHeightInPoints((short) 12);
            bf.setBold(true);
            hourStyle.setDataFormat(dataFormat.getFormat("0.00"));
            hourStyle.setAlignment(CellStyle.ALIGN_RIGHT);

            meetingStyle = wb.createCellStyle();
            meetingStyle.setFillPattern(hourTotalStyle.SOLID_FOREGROUND);
            meetingStyle.setFillForegroundColor(getColorIndex(wb, new java.awt.Color(255, 255, 127)));
            meetingStyle.setDataFormat(dataFormat.getFormat("0.00"));
            meetingStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            meetingStyle.setFont(bf);

            coordStyle = wb.createCellStyle();
            coordStyle.setFillPattern(hourTotalStyle.SOLID_FOREGROUND);
            coordStyle.setFillForegroundColor(getColorIndex(wb, new java.awt.Color(255, 170, 255)));
            coordStyle.setDataFormat(dataFormat.getFormat("0.00"));
            coordStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            coordStyle.setFont(bf);

            hourTotalStyle = wb.createCellStyle();
            hourTotalStyle.setFillPattern(hourTotalStyle.SOLID_FOREGROUND);
            hourTotalStyle.setFillForegroundColor(getColorIndex(wb, new java.awt.Color(0, 170, 255)));
            hourTotalStyle.setDataFormat(dataFormat.getFormat("0.00"));
            hourTotalStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            hourTotalStyle.setFont(bf);

            teacherStyle = wb.createCellStyle();
            teacherStyle.setFillPattern(teacherStyle.SOLID_FOREGROUND);
            teacherStyle.setFillForegroundColor(getColorIndex(wb, new java.awt.Color(156, 222, 49)));
            teacherStyle.setFont(bf);
            teacherStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            teacherStyle.setAlignment(CellStyle.ALIGN_CENTER);

            totalPeriodStyle = wb.createCellStyle();
            totalPeriodStyle.setFillPattern(teacherStyle.SOLID_FOREGROUND);
            totalPeriodStyle.setFillForegroundColor(getColorIndex(wb, new java.awt.Color(156, 222, 49)));
            totalPeriodStyle.setFont(bf);

            hourPeriodStyle = wb.createCellStyle();
            hourPeriodStyle.setFillPattern(hourTotalStyle.SOLID_FOREGROUND);
            hourPeriodStyle.setFillForegroundColor(getColorIndex(wb, new java.awt.Color(156, 222, 49)));
            hourPeriodStyle.setDataFormat(dataFormat.getFormat("0.00"));
            hourPeriodStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            hourPeriodStyle.setFont(bf);

            dayStyle = wb.createCellStyle();
            dayStyle.setFillPattern(dayStyle.SOLID_FOREGROUND);
            dayStyle.setFillForegroundColor(getColorIndex(wb, new java.awt.Color(0, 170, 255)));
            dayStyle.setFont(bf);
            dayStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            dayStyle.setAlignment(CellStyle.ALIGN_RIGHT);

            headerStyle = wb.createCellStyle();
            headerStyle.setFillPattern(teacherStyle.SOLID_FOREGROUND);
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            headerStyle.setFont(bf);
            headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            headerStyle.setAlignment(CellStyle.ALIGN_CENTER);

            Map<String, Object> props = getProperties();
            DateFr start = (DateFr) props.get("start");
            DateFr end = (DateFr) props.get("end");
            int idper = (int) props.get("idper");
            boolean detail = (boolean) props.get("detail");

            Row row = feuille.createRow(nrow++);
            row.setHeightInPoints(25);
            Cell cell = row.createCell(0);
            cell.setCellValue(MessageUtil.getMessage("export.hour.teacher.header", new Object[]{"JAT", start, end}));
            feuille.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
            cell.setCellStyle(headerStyle);

            List<CustomSchedule> schedules = dao.getSchedules(idper, start, end);

            int t = 0;
            int totalL = 0; // total Loisir
            int totalP = 0; // total Pro
            int totalR = 0; // total Réunion
            int totalC = 0; // total Coordination

            int totaldL = 0;
            int totaldP = 0;
            int totaldR = 0;
            int totaldC = 0;

            DateFr d = null;
            Person person = null;
            int len = schedules.size();
            int progress = 0;
            for (CustomSchedule cs : schedules) {
                if (detail && d == null) {
                    row = feuille.createRow(nrow++);
                    row.setHeightInPoints(25);
                    cell = row.createCell(0);
                    cell.setCellValue(cs.getPerson().getFirstnameName());
                    cell.setCellStyle(teacherStyle);
                }
                if (detail && (!cs.getDate().equals(d) || cs.getPerson().getId() != t)) {
                    if (d != null) { // pas au premier tour de boucle
                        printXLSResult(d, totaldL, totaldP, totaldR, totaldC);
                        totaldL = 0;
                        totaldP = 0;
                        totaldR = 0;
                        totaldC = 0;

                    }
                    d = cs.getDate();
                }
                if (cs.getPerson().getId() != t) {
                    if (t > 0) { // pas au premier tour de boucle
                        if (detail) {
                            row = feuille.createRow(nrow++);
                            printXLSResult();
                            row = feuille.createRow(nrow++);
                            row = feuille.createRow(nrow++);
                            row.setHeightInPoints(25);
                            cell = row.createCell(0);
                            cell.setCellValue(cs.getPerson().getFirstnameName());
                            cell.setCellStyle(teacherStyle);
                        } else {
                            printXLSResult(person, totalL, totalP, totalR, totalC);
                            row = feuille.createRow(nrow++);
                        }
                        totalL = 0;
                        totalP = 0;
                        totalR = 0;
                        totalC = 0;
                        totaldXL = "";
                        totaldXP = "";
                        totaldXR = "";
                        totaldXC = "";
                    }
                    t = cs.getPerson().getId();
                    person = cs.getPerson();
                }

                if (Schedule.ADMINISTRATIVE == cs.getType()) {
                    if (cs.getNoteValue() != null && "coordination".equals(cs.getNoteValue().trim().toLowerCase())) {
                        totalC += cs.getLength();
                        totaldC += cs.getLength();
                    } else {
                        totalR += cs.getLength();
                        totaldR += cs.getLength();
                    }
                    continue;
                } else {
                    if (cs.isCollective()) {//XXX si aucune plage élève
                        if (cs.status == CODE_LOISIR) {
                            totalL += cs.getLength();
                            totaldL += cs.getLength();
                        } else {
                            totalP += cs.getLength();
                            totaldP += cs.getLength();
                        }
                    } else {
                        List<CustomRange> ranges = dao.getRanges(cs.getId());
                        for (CustomRange r : ranges) {
                            if (r.isLeisure()) {
                                totalL += r.length;
                                totaldL += r.length;
                            } else {
                                totalP += r.length;
                                totaldP += r.length;
                            }
                        }
                    }
                }
                worker.setStep(progress++ * 100 / len);
            }

            if (detail) {
                if (d != null) {
                    printXLSResult(d, totaldL, totaldP, totaldR, totaldC);
                    printXLSResult();
                    row = feuille.createRow(nrow++);
                }
            } else {
                if (person != null) {
                    printXLSResult(person, totalL, totalP, totalR, totalC);
                }
            }

            FileOutputStream fileOut;
            try {
                String path = (String) props.get("path");
                //System.out.println("XLSWorkingTimePlugins path=" + path);
                fileOut = new FileOutputStream(path);
                wb.write(fileOut);
                fileOut.close();
            } catch (Exception ex) {
                GemLogger.logException(ex);
            }

        } catch (SQLException ex) {
            GemLogger.logException(ex);
        } catch (Exception ex) { //ERIC
            GemLogger.logException(ex);
        } finally {
            //out.close();
        }
    }

    private short getColorIndex(HSSFWorkbook wb, java.awt.Color c) {
        byte red = (byte) c.getRed();
        byte green = (byte) c.getGreen();
        byte blue = (byte) c.getBlue();
        short index = -1;
        HSSFColor color = wb.getCustomPalette().findColor(red, green, blue);
        if (color == null) {
            index = wb.getCustomPalette().findSimilarColor(red, green, blue).getIndex();
            wb.getCustomPalette().setColorAtIndex(index, red, green, blue);
        } else {
            index = color.getIndex();
        }
        return index;
    }

    private class CustomRange {
//    Parcours DEM Jazz, L1 Musicologie/Jazz à Tours, L2 Musicologie / Jazz à Tours, L3 Musicologie / Jazz à Tours

        private final int[] MOD_LOISIR = {149, 151, 157, 158};

        private final int[] MOD_PRO = {141, 142, 143, 144, 145, 146, 147, 148};
        private final String FP = "15000";

        private int length;
        private int moduleId;
        private String code;
        private String analytique;

        public CustomRange(int length, int moduleId, String code, String analytique) {
            this.length = length;
            this.moduleId = moduleId;
            this.code = code;
            this.analytique = analytique;
        }

        public boolean isLeisure() {
            if (code.startsWith("L")) {
                return true;
            }
            boolean l = false;
            for (int i : MOD_LOISIR) {
                if (moduleId == i) {
                    l = true;
                    break;
                }
            }
            return l;
            //return l && !FP.equals(analytique);
        }

        public boolean isPro() {
            if (FP.equals(analytique)) {
                return true;
            }
            for (int i : MOD_PRO) {
                if (moduleId == i) {
                    return true;
                }
            }
            return false;
        }

    }

    private class CustomSchedule
            extends Schedule {

        private Person person;
        private int length;
        private int status;
        private int action;
        private boolean collective;
        private String noteValue;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }

        public boolean isCollective() {
            return collective;
        }

        public void setCollective(boolean collective) {
            this.collective = collective;
        }

        public Person getPerson() {
            return person;
        }

        public void setPerson(Person person) {
            this.person = person;
        }

        public void setLength(int length) {
            this.length = length;
        }

        @Override
        public int getLength() {
            return length;
        }

        public String getNoteValue() {
            return noteValue;
        }

        public void setNoteValue(String noteValue) {
            this.noteValue = noteValue;
        }

    }

    private class CustomDAO {

        private DataConnection dc;
        private PreparedStatement ps5;

        private final String rangeQuery = "SELECT DISTINCT ON (pl.id) pl.fin-pl.debut,m.id,m.code,e.analytique"
                + " FROM " + ScheduleRangeIO.TABLE + " pl JOIN " + ScheduleIO.TABLE + " p ON (pl.idplanning = p.id)"
                + " JOIN " + CourseOrderIO.TABLE + " cc ON (cc.idaction = p.action)"
                + " JOIN " + ModuleOrderIO.TABLE + " cm ON (cc.module = cm.id)"
                + " JOIN " + ModuleIO.TABLE + " m ON (cm.module = m.id)"
                + " JOIN " + OrderIO.TABLE + " c ON (cm.idcmd = c.id AND pl.adherent = c.adh)"
                //"JOIN commande d ON (cm.idcmd = d.id)\n" +
                + " LEFT JOIN " + OrderLineIO.TABLE + " e ON (cm.idcmd = e.commande AND e.adherent = pl.adherent)" // !LEFT JOIN : IMPORTANT!
                + " WHERE pl.idplanning = ?";

        public CustomDAO(DataConnection dc) {
            this.dc = dc;
            ps5 = dc.prepareStatement(rangeQuery);
        }

        private List<CustomSchedule> getSchedules(int idper, DateFr start, DateFr end) throws SQLException {
            List<CustomSchedule> schedules = new ArrayList<>();
            String query = "SELECT p.id,p.jour,p.fin-p.debut,p.ptype,p.idper,per.nom,per.prenom,p.action,c.collectif,a.statut,s.texte"
                    + " FROM " + ScheduleIO.TABLE + " p  JOIN " + PersonIO.TABLE + " per ON (p.idper = per.id)"
                    + " JOIN action a ON (p.action = a.id) LEFT JOIN cours c ON (a.cours = c.id)"
                    + " LEFT JOIN plage pl ON (p.id = pl.idplanning AND p.idper = pl.adherent) LEFT JOIN suivi s ON (pl.note = s.id)"
                    + " WHERE p.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + "," + Schedule.ADMINISTRATIVE + ")"
                    + " AND p.jour BETWEEN '" + start + "' AND '" + end + "'"
                    + " AND p.lieux NOT IN (SELECT id FROM salle WHERE nom ~* 'RATTRAP')"
                    + " AND (p.ptype = " + Schedule.ADMINISTRATIVE + " OR (p.id IN (SELECT idplanning FROM " + ScheduleRangeIO.TABLE + ")))";
            if (idper > 0) {
                query += " AND p.idper = " + idper;
            }
            query += " ORDER BY per.nom,per.prenom,p.jour,p.debut";
            //System.out.println("XLSWorkingTimePlugins query=" + query);
            ResultSet rs = dc.executeQuery(query);
            while (rs.next()) {
                CustomSchedule cs = new CustomSchedule();

                cs.setId(rs.getInt(1));
                cs.setDate(new DateFr(rs.getString(2)));
                String len = rs.getString(3);
                cs.setLength(Hour.getMinutesFromString(len));
                cs.setType(rs.getInt(4));
                Person p = new Person(rs.getInt(5), rs.getString(6), rs.getString(7), "");
                cs.setPerson(p);
                cs.setAction(rs.getInt(8));
                cs.setCollective(rs.getBoolean(9));
                cs.setStatus(rs.getInt(10));
                cs.setNoteValue(rs.getString(11));

                schedules.add(cs);
            }
            return schedules;
        }

        /**
         * Gets individual ranges enclosed in schedule {@code p}.
         *
         * @param p schedule
         * @return a list of ranges
         * @throws SQLException
         */
        private List<CustomRange> getRanges(int p) throws SQLException {
            List<CustomRange> ranges = new ArrayList<>();
            ps5.setInt(1, p);
            try (ResultSet rs = ps5.executeQuery()) {
                while (rs.next()) {
                    int min = Hour.getMinutesFromString(rs.getString(1));
                    CustomRange cr = new CustomRange(min, rs.getInt(2), rs.getString(3), rs.getString(4));
                    ranges.add(cr);
                }
            }
            return ranges;
        }
    }

}
