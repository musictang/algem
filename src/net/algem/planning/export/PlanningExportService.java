/*
 * @(#)PlanningExportService.java 2.15.8 26/03/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning.export;

import net.algem.config.ColorPrefs;
import net.algem.course.Course;
import net.algem.planning.*;
import net.algem.planning.day.DayPlan;
import net.algem.util.GemLogger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.Person;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * Exporting schedule service.
 * @author <a href="mailto:alexandre.delattre.biz@gmail.com">Alexd</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.8
 * @since 2.9.2
 */
public class PlanningExportService {

  private ColorPrefs colorPrefs;
  private PlanningService planningService;
  private ScheduleColorizer colorizer;
  private short paperSize;
  private boolean printMembers;

  public PlanningExportService(PlanningService service, ScheduleColorizer colorizer, short paperSize, boolean printMembers) {
    this.planningService = service;
    this.colorizer = colorizer;
    this.colorPrefs = new ColorPrefs();
    this.paperSize = paperSize;
    this.printMembers = printMembers;
  }

  /**
   * Export to Excel destination file.
   *
   * @param dayPlan list of day schedules
   * @param destFile destination file
   * @throws IOException
   */
  public void exportPlanning(List<DayPlan> dayPlan, File destFile) throws IOException {
    GemLogger.info("Exporting planning to " + destFile);

    Hour defStartTime = new Hour(ConfigUtil.getConf(ConfigKey.START_TIME.getKey()));
    int offset = defStartTime.getHour();
    int totalh = 24 - offset; // total time length in hours

    HSSFWorkbook workbook = new HSSFWorkbook();
    Sheet sheet = workbook.createSheet("Planning");
    if (dayPlan.size() > 0) {
      DateFormat df = new SimpleDateFormat("EEEE dd MMM yyyy");
      Header header = sheet.getHeader();
      String hd = df.format(dayPlan.get(0).getSchedule().get(0).getDate().getDate());
      header.setCenter(HSSFHeader.fontSize((short) 12) + HSSFHeader.startBold() + hd + HSSFHeader.endBold());
    }

    PrintSetup printSetup = sheet.getPrintSetup();
    printSetup.setLandscape(true);
    printSetup.setPaperSize(paperSize);
    sheet.setFitToPage(true);
    sheet.setHorizontallyCenter(false);// was true before 2.15.8
    sheet.setMargin(Sheet.TopMargin, 0.75); // 1.905
    sheet.setMargin(Sheet.BottomMargin, 0.4); // 0.4 inch = 1.016 cm
    sheet.setMargin(Sheet.LeftMargin, 0.4);
    sheet.setMargin(Sheet.RightMargin, 0.4);

    Map<String, CellStyle> styles = createStyles(workbook);

    Row headerRow = sheet.createRow(0);
    for (int i = 0; i < dayPlan.size(); i++) {
      Cell roomCell = headerRow.createCell(i + 1);
      // Set the width (in units of 1/256th of a character width)
      //sheet.setColumnWidth(i + 1, totalh * 256);// max number of characters must not depend of time length
      sheet.setColumnWidth(i + 1, 24 * 256);  // cours.titre character varying(32)
      roomCell.setCellValue(dayPlan.get(i).getLabel());
      roomCell.setCellStyle(styles.get("header"));
    }
    int offsetMn = offset * 60;// offset in minutes
    List<Row> rows = new ArrayList<>();
    System.out.println(" offset = "+offset+" totalh = "+ totalh);
    for (int t = 0, rowNumber=1; t < totalh * 60; t += 5, rowNumber++) { // 1 row = 5mn
      Hour hour = new Hour(offsetMn + t);
      Row row = sheet.createRow(rowNumber);
      //row.setHeightInPoints(25);
      row.setHeightInPoints(PrintSetup.A3_PAPERSIZE == paperSize ? 12 : 6);
      // TIME SUBDIVISIONS
      if (t % 15 == 0) {
        Cell cell = row.createCell(0);
        if (t % 30 == 0) {
          cell.setCellValue(hour.toString());//show time
          if (t % 60 == 0) {
            cell.setCellStyle(styles.get("hour"));
          } else {
            cell.setCellStyle(styles.get("hour-half"));
          }
        } else {
          cell.setCellStyle(styles.get("hour-quarter"));
        }
      } else { // BETWEEN SUBDIVISION
        Cell cell = row.createCell(0);
        if ("23:55".equals(hour.toString())) { // last slice
          cell.setCellStyle(styles.get("hour-last"));
        } else {
          cell.setCellStyle(styles.get("hour"));
        }
        if (rowNumber % 3 == 0){ // merge every 3 rows
          sheet.addMergedRegion(new CellRangeAddress(rowNumber -2, rowNumber, 0, 0));
        }
      }
      rows.add(row);
    }

    Map<java.awt.Color, CellStyle> coursStyleCache = new HashMap<>();

    for (int i = 0; i < dayPlan.size(); i++) {
      DayPlan plan = dayPlan.get(i);
      int col = i + 1;
      for (ScheduleObject event : plan.getSchedule()) {
        // if event starts before default starting time
        if (event.getStart().toMinutes() < offsetMn) {
          event.setStart(new Hour(offset * 60));
        }
        int startRowPos = (event.getStart().toMinutes() - offsetMn) / 5 + 1;
        int endRowPos = (event.getEnd().toMinutes() - offsetMn) / 5;

        Cell courseCell = rows.get(startRowPos - 1).createCell(col);
        courseCell.setCellValue(getLabel(event, workbook));// title text

        CellStyle style = getCourseStyle(workbook, event, coursStyleCache);
        courseCell.setCellStyle(style);
        if (startRowPos != endRowPos) {
          sheet.addMergedRegion(new CellRangeAddress(startRowPos, endRowPos, col, col));
          for (int row = startRowPos; row < endRowPos; row++) {
            rows.get(row).createCell(col).setCellStyle(style);
          }
        }
      }

    }

    try (FileOutputStream out = new FileOutputStream(destFile)) {
      workbook.write(out);
    }

  }

  /**
   *
   * @param wb workbook
   * @return a map, each key-value composed of a style name and a cell style
   */
  private Map<String, CellStyle> createStyles(HSSFWorkbook wb) {
    Map<String, CellStyle> styles = new HashMap<>();

    HSSFFont nf = wb.createFont();
    nf.setFontName("monospace");

    HSSFFont bf = wb.createFont();
    bf.setFontName("monospace");
    bf.setBold(true);

    HSSFFont sf = wb.createFont();
    sf.setFontHeightInPoints((short) 8);
    sf.setFontName("monospace");

    CellStyle style = wb.createCellStyle();
    style.setAlignment(CellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    style.setWrapText(true);
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    styles.put("header", style);

    //LEFT HEADER
    style = wb.createCellStyle();
    style.setAlignment(CellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
    style.setWrapText(true);
    style.setFont(bf);
    style.setBorderLeft(CellStyle.BORDER_THIN);
    style.setBorderRight(CellStyle.BORDER_THIN);
    style.setBorderTop(CellStyle.BORDER_THIN);
    style.setBorderBottom(CellStyle.BORDER_DOTTED);
    styles.put("hour", style);

    style = wb.createCellStyle();
    style.setAlignment(CellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
    style.setWrapText(false);
    style.setFont(sf);
    style.setBorderLeft(CellStyle.BORDER_THIN);
    style.setBorderRight(CellStyle.BORDER_THIN);
    style.setBorderTop(CellStyle.BORDER_DOTTED);
    style.setBorderBottom(CellStyle.BORDER_THIN);
    styles.put("hour-quarter", style);

    style = wb.createCellStyle();
    style.setAlignment(CellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
    style.setWrapText(false);
    style.setFont(sf);
    style.setBorderLeft(CellStyle.BORDER_THIN);
    style.setBorderRight(CellStyle.BORDER_THIN);
    style.setBorderTop(CellStyle.BORDER_DASHED);
    style.setBorderBottom(CellStyle.BORDER_THIN);
    styles.put("hour-half", style);

    style = wb.createCellStyle();
    style.setAlignment(CellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
    style.setWrapText(true);
    style.setFont(nf);
    style.setBorderLeft(CellStyle.BORDER_THIN);
    style.setBorderRight(CellStyle.BORDER_THIN);
    style.setBorderTop(CellStyle.BORDER_THIN);
    style.setBorderBottom(CellStyle.BORDER_THIN);
    styles.put("hour-last", style);

    return styles;
  }

  /**
   *
   * @param wb workbook
   * @param schedule current schedule
   * @param cache styles cache
   * @return a cell style
   */
  private CellStyle getCourseStyle(HSSFWorkbook wb, ScheduleObject schedule, Map<java.awt.Color, CellStyle> cache) {
    java.awt.Color color = colorizer.getScheduleColor(schedule);
    CellStyle cachedStyle = cache.get(color);
    if (cachedStyle != null) {
      return cachedStyle;
    } else {
      CellStyle style = wb.createCellStyle();
      style.setAlignment(CellStyle.ALIGN_CENTER);
      style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
      style.setWrapText(true);
      style.setBorderLeft(CellStyle.BORDER_THIN);
      style.setBorderRight(CellStyle.BORDER_THIN);
      style.setBorderTop(CellStyle.BORDER_THIN);
      style.setBorderBottom(CellStyle.BORDER_THIN);
      style.setFillPattern(CellStyle.SOLID_FOREGROUND);
      //the cell is painted with a pattern that consists of foreground and background pixels.
      //If you use SOLID_FOREGROUND, just the foreground pixel are visible.
      //This color is different from the color used to render text, which is set with the font
      style.setFillForegroundColor(getColorIndex(wb, color));

      // Unused because font is applied as richTextString property
      /*java.awt.Color textColor = colorizer.getTextColor(schedule);
      HSSFFont font = wb.createFont();
      font.setColor(getColorIndex(wb, textColor));
      style.setFont(font);
      cache.put(color, style);*/
      return style;
    }
  }

  /**
   *
   * @param wb workbook
   * @param c referenced color
   * @return an index
   */
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

  /**
   *
   * @param p current schedule
   * @param wb workbook
   * @return a formatted-string
   */
  private RichTextString getLabel(ScheduleObject p, HSSFWorkbook wb) {
    String header = p.getStart() + "-" + p.getEnd() + "\n";
    StringBuilder sb = new StringBuilder();
    sb.append(header);
    switch (p.getType()) {
      case Schedule.COURSE:
      case Schedule.WORKSHOP:
      case Schedule.TRAINING:
        Course c = ((CourseSchedule) p).getCourse();
        sb.append(c.getLabel() != null && c.getLabel().length() > 0 ? c.getLabel() : c.getTitle());
        sb.append('\n');
        sb.append(p.getPerson().getAbbrevFirstNameName());
        if (p.getLength() > 30 && printMembers) {
          try {
            List<Person> members = planningService.getPersons(p.getId());
            if (members.size() == 1) {
              Person per = members.get(0);
              sb.append('\n').append(per.getNickName() != null && per.getNickName().length() > 0 ? per.getNickName() : per.getAbbrevFirstNameName());
            }
          } catch (SQLException ex) {
            GemLogger.log(ex.getMessage());
          }
        }
        break;
      default:
        sb.append(p.getScheduleLabel());
    }

    String content = sb.toString();
    HSSFFont bf = wb.createFont();
    bf.setBold(true);
    HSSFFont nf = wb.createFont();

    java.awt.Color textColor = colorizer.getTextColor(p);
    bf.setColor(getColorIndex(wb, textColor));
    nf.setColor(getColorIndex(wb, textColor));

    // RichTextString is used here to apply bold font to title
    RichTextString rts = new HSSFRichTextString(content);
    //int idx = content.indexOf("\n", content.indexOf("\n")+1);
    int idx = content.indexOf("\n") + 1;
    rts.applyFont(0, header.length(), bf);
    if (idx >= header.length()) {// some additional content was found
      rts.applyFont(header.length(), content.length(), nf);
    }
    return rts;
  }

  protected java.awt.Color getScheduleColor(ScheduleObject p) {
    return colorizer.getScheduleColor(p);
  }
}
