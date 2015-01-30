package net.algem.planning.export;

import net.algem.config.ColorPlan;
import net.algem.config.ColorPrefs;
import net.algem.course.Course;
import net.algem.planning.*;
import net.algem.planning.day.DayPlan;
import net.algem.room.Room;
import net.algem.util.GemLogger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanningExportService {

    private ColorPrefs colorPrefs;

    public PlanningExportService() {
        colorPrefs = new ColorPrefs();
    }

    public void exportPlanning(List<DayPlan> dayPlan, File destFile) throws Exception {
        GemLogger.info("Exporting planning to " + destFile);

        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Planning");

        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        printSetup.setPaperSize(PrintSetup.A3_PAPERSIZE);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);

        Map<String, CellStyle> styles = createStyles(workbook);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < dayPlan.size(); i++) {
            Cell roomCell = headerRow.createCell(i + 1);
            sheet.setColumnWidth(i + 1, 14 * 256);
            roomCell.setCellValue(dayPlan.get(i).getLabel());
            roomCell.setCellStyle(styles.get("header"));
        }

        List<Row> rows = new ArrayList<>();
        for (int halfHour = 0; halfHour < 28; halfHour++) {
            Hour hour = new Hour(9 * 60 + halfHour * 30);
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
                int startRowPos = (event.getStart().toMinutes() - (9 * 60)) / 30 + 1;
                int endRowPos = (event.getEnd().toMinutes() - (9 * 60)) / 30;
                Cell coursCell = rows.get(startRowPos - 1).createCell(col);
                coursCell.setCellValue(getCoursLibelle(event));
                CellStyle style = getCoursStyle(workbook, event, coursStyleCache);
                coursCell.setCellStyle(style);

                if (startRowPos != endRowPos) {
                    sheet.addMergedRegion(new CellRangeAddress(startRowPos, endRowPos, col, col));
                    for (int row=startRowPos; row<endRowPos; row++) {
                        rows.get(row).createCell(col).setCellStyle(style);
                    }
                }
            }

        }

        FileOutputStream out = new FileOutputStream(destFile);
        workbook.write(out);
        out.close();
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
        java.awt.Color color = getScheduleColor(o);
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
            style.setFillForegroundColor(wb.getCustomPalette().findSimilarColor(color.getRed(), color.getGreen(), color.getBlue()).getIndex());
            cache.put(color, style);
            return style;
        }
    }

    private String getCoursLibelle(ScheduleObject p) {
        switch (p.getType()) {
            case Schedule.COURSE:
                Room s = ((CourseSchedule) p).getRoom();
                Course cc = ((CourseSchedule) p).getCourse();
                return cc.getLabel() + "\n" +  p.getPerson().getAbbrevFirstNameName();
            default:
                return p.getScheduleLabel();
        }
    }

    protected java.awt.Color getScheduleColor(ScheduleObject p) {
        java.awt.Color c = java.awt.Color.white;
        switch (p.getType()) {
            case Schedule.COURSE:
                Room s = ((CourseSchedule) p).getRoom();
                Course cc = ((CourseSchedule) p).getCourse();
                if (s.isCatchingUp()) {
                    //c = Color.black; // salles de rattrapages
                    c = colorPrefs.getColor(ColorPlan.CATCHING_UP);
                } else {
                    if (cc != null && !cc.isCollective()) {
                        //c = DARK_GREEN; // couleur plannings cours individuels
                        c = colorPrefs.getColor(ColorPlan.COURSE_INDIVIDUAL);
                    } else {
                        //c = Color.red; // couleur cours collectif
                        if (cc != null && cc.isInstCode()) {
                            c = colorPrefs.getColor(ColorPlan.INSTRUMENT_CO);
                        } else {
                            c = colorPrefs.getColor(ColorPlan.COURSE_CO);
                        }
                    }
                }
                break;
            case Schedule.ACTION:
                //c = Color.green;
                c = colorPrefs.getColor(ColorPlan.ACTION);
                break;
            case Schedule.MEMBER:
                c = colorPrefs.getColor(ColorPlan.MEMBER_REHEARSAL);
                break;
            case Schedule.GROUP:
                //c= DARK_BLUE; // couleur groupe et repetiteurs
                c = colorPrefs.getColor(ColorPlan.GROUP_REHEARSAL);
                break;
            case Schedule.WORKSHOP:
                //c = Color.white; // couleur atelier ponctuel
                c = colorPrefs.getColor(ColorPlan.WORKSHOP);
                break;
            case Schedule.TRAINING:
                //c = Color.white; // couleur atelier ponctuel
                c = colorPrefs.getColor(ColorPlan.TRAINING);
                break;
            case Schedule.STUDIO:
            case Schedule.TECH:
                //c = Color.white; // couleur atelier ponctuel
                c = colorPrefs.getColor(ColorPlan.STUDIO);
                break;
        } // end switch couleurs
        return c;
    }
}
