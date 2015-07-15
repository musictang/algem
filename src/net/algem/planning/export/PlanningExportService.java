/*
 * @(#)PlanningExportService.java 2.9.3.2 11/03/15
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.Person;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * @author <a href="mailto:alexandre.delattre.biz@gmail.com">Alexd</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.3.2
 * @since 2.9.2
 */
public class PlanningExportService
{

  private final ScheduleColorizer colorizer;
  private final PlanningService planningService;

  private final static short COLOR_START_INDEX = 20;
  private short colorIndex = COLOR_START_INDEX;

  public PlanningExportService(PlanningService service, ScheduleColorizer colorizer) {
    this.planningService = service;
    this.colorizer = colorizer;
  }

  public void exportPlanning(List<DayPlan> dayPlan, File destFile) throws IOException {
    GemLogger.info("Exporting planning to " + destFile);

    Hour defStartTime = new Hour(ConfigUtil.getConf(ConfigKey.START_TIME.getKey()));
    int offset = defStartTime.getHour();
    int totalh = 24 - offset;

    HSSFWorkbook workbook = new HSSFWorkbook();
    Sheet sheet = workbook.createSheet("Planning");

    PrintSetup printSetup = sheet.getPrintSetup();
    printSetup.setLandscape(true);
    printSetup.setPaperSize(PrintSetup.A3_PAPERSIZE);
    sheet.setFitToPage(true);
    sheet.setHorizontallyCenter(true);

    eraseAllColors(workbook);
    colorIndex = COLOR_START_INDEX;

    Map<String, CellStyle> styles = createStyles(workbook);

    Row headerRow = sheet.createRow(0);
    for (int i = 0; i < dayPlan.size(); i++) {
      Cell roomCell = headerRow.createCell(i + 1);
      sheet.setColumnWidth(i + 1, totalh * 256);
      roomCell.setCellValue(dayPlan.get(i).getLabel());
      roomCell.setCellStyle(styles.get("header"));
    }

    List<Row> rows = new ArrayList<>();
    for (int halfHour = 0; halfHour < totalh * 2; halfHour++) { // totalh * 2 ( number of 1/2h in grid
      Hour hour = new Hour(offset * 60 + halfHour * 30);// offset = hour of start
      int rowNumber = halfHour + 1;
      Row row = sheet.createRow(rowNumber);
      row.setHeightInPoints(25);
      if (halfHour % 2 == 0) {
        Cell cell = row.createCell(0);
        cell.setCellValue(hour.toString());
        cell.setCellStyle(styles.get("hour"));
      } else {
        Cell cell = row.createCell(0);
        cell.setCellStyle(styles.get("hour"));
        sheet.addMergedRegion(new CellRangeAddress(rowNumber - 1, rowNumber, 0, 0));
      }
      rows.add(row);
    }

    Map<java.awt.Color, CellStyle> coursStyleCache = new HashMap<>();

    for (int i = 0; i < dayPlan.size(); i++) {
      DayPlan plan = dayPlan.get(i);
      int col = i + 1;
      for (ScheduleObject event : plan.getSchedule()) {
        int startRowPos = (event.getStart().toMinutes() - (offset * 60)) / 30 + 1;
        int endRowPos = (event.getEnd().toMinutes() - (offset * 60)) / 30;
        Cell coursCell = rows.get(startRowPos - 1).createCell(col);
        coursCell.setCellValue(getLabel(event));
        CellStyle style = getCoursStyle(workbook, event, coursStyleCache);
        coursCell.setCellStyle(style);

        if (startRowPos != endRowPos) {
          sheet.addMergedRegion(new CellRangeAddress(startRowPos, endRowPos, col, col));
          for (int row = startRowPos; row < endRowPos; row++) {
            rows.get(row).createCell(col).setCellStyle(style);
          }
        }
      }

    }

    FileOutputStream out = new FileOutputStream(destFile);
    workbook.write(out);
    out.close();
  }

  private void eraseAllColors(HSSFWorkbook workbook) {
    for (short i = 0; i < 56; i++) {
      workbook.getCustomPalette().setColorAtIndex(i, (byte) 0,(byte)  0, (byte) 0);
    }
  }

  private Map<String, CellStyle> createStyles(HSSFWorkbook wb) {
    Map<String, CellStyle> styles = new HashMap<>();

    CellStyle style = wb.createCellStyle();
    style.setAlignment(CellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    style.setWrapText(true);
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    styles.put("header", style);

    style = wb.createCellStyle();
    style.setAlignment(CellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
    style.setWrapText(true);
    style.setBorderLeft(CellStyle.BORDER_THIN);
    style.setBorderRight(CellStyle.BORDER_THIN);
    style.setBorderTop(CellStyle.BORDER_THIN);
    style.setBorderBottom(CellStyle.BORDER_THIN);
    styles.put("hour", style);

    return styles;
  }

  private CellStyle getCoursStyle(HSSFWorkbook wb, ScheduleObject o, Map<java.awt.Color, CellStyle> cache) {
    java.awt.Color color = colorizer.getScheduleColor(o);
    CellStyle cachedStyle = cache.get(color);
    if (cachedStyle != null) {
      return cachedStyle;
    } else {
      CellStyle style = wb.createCellStyle();
      style.setAlignment(CellStyle.ALIGN_CENTER);
      style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
      style.setWrapText(true);
      style.setBorderLeft(CellStyle.BORDER_THIN);
      style.setBorderRight(CellStyle.BORDER_THIN);
      style.setBorderTop(CellStyle.BORDER_THIN);
      style.setBorderBottom(CellStyle.BORDER_THIN);
      style.setFillPattern(CellStyle.SOLID_FOREGROUND);
      HSSFColor hffsColor = wb.getCustomPalette().findColor((byte) color.getRed(), (byte)color.getGreen(), (byte)color.getBlue());
      short index;
      if (hffsColor == null) {
        index = colorIndex++;
        wb.getCustomPalette().setColorAtIndex(index, (byte) color.getRed(), (byte)color.getGreen(), (byte)color.getBlue());
      } else {
        index = hffsColor.getIndex();
      }
      style.setFillForegroundColor(index);
      cache.put(color, style);
      return style;
    }
  }

  private String getLabel(ScheduleObject p) {
    StringBuilder sb = new StringBuilder();
    switch (p.getType()) {
      case Schedule.COURSE:
      case Schedule.WORKSHOP:
      case Schedule.TRAINING:
        Course c = ((CourseSchedule) p).getCourse();
        sb.append(c.getLabel() != null && c.getLabel().length() > 0 ? c.getLabel() : c.getTitle());
        sb.append('\n');
        sb.append(p.getPerson().getAbbrevFirstNameName());
        if (p.getLength() > 30) {
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
    return sb.toString();
  }
}
