/*
 * @(#)HourStatDlg.java	2.8.w 08/07/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JPanel;
import net.algem.course.Course;
import net.algem.course.CourseIO;
import net.algem.planning.DateFr;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleObject;
import net.algem.planning.ScheduleRangeObject;
import net.algem.room.Establishment;
import net.algem.room.Room;
import net.algem.util.*;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * Export hours of activity.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 1.0a 30/04/2003
 */
public class HourStatDlg
        extends JDialog
        implements ActionListener {

    private DataCache dataCache;
    private HourStatView view;
    private GemButton btValidation;
    private GemButton btCancel;
    private JPanel buttons;
    private NumberFormat nf = NumberFormat.getInstance(Locale.FRANCE);
    private ExportService service;

    public HourStatDlg(Frame frame, DataCache dataCache) {
        super(frame, "Edition Statistique activité");
        this.dataCache = dataCache;
        init(DataCache.getDataConnection());
    }

    public HourStatDlg(Dialog dialog, DataCache dataCache) {
        super(dialog, "Edition Statitique activité");
        this.dataCache = dataCache;
        init(DataCache.getDataConnection());
    }

    public void init(DataConnection dc) {

        service = new ExportService(dc);
        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        view = new HourStatView(dc, dataCache.getList(Model.School));

        btValidation = new GemButton(GemCommand.VALIDATION_CMD);
        btValidation.addActionListener(this);
        btCancel = new GemButton(GemCommand.CANCEL_CMD);
        btCancel.addActionListener(this);

        buttons = new GemPanel(new GridLayout(1, 2));
        buttons.add(btValidation);
        buttons.add(btCancel);

        getContentPane().add("Center", view);
        getContentPane().add("South", buttons);
//    setSize(380, 250);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == btCancel) {
        } else if (evt.getSource() == btValidation) {
            transfer();
        }
        setVisible(false);
        dispose();
    }

    void transfer() {
        DateFr start = view.getDateStart();
        DateFr end = view.getDateEnd();
        int school = view.getSchool();
        //boolean detail = vue.withDetail();

        GemList<Establishment> estabList = dataCache.getList(Model.Establishment);

        int size = estabList.getSize();

        int partPro[] = new int[size];
        int partLeisure[] = new int[size];
        int collective[] = new int[size];
        int collectivePro[] = new int[size];
        int rehearsal[] = new int[size];
        int workshop[] = new int[size];
        int other[] = new int[size];

        String lf = TextUtil.LINE_SEPARATOR;

        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        String file = service.getPath() + FileUtil.FILE_SEPARATOR + "heurestat.txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.println(MessageUtil.getMessage("export.hours.header", new Object[]{start, end}) + lf);

            Vector<ScheduleObject> plan = service.getSchedule(start, end);
            for (int i = 0; i < plan.size(); i++) {
                Schedule p = plan.elementAt(i);

                if (p.getType() == Schedule.COURSE) {
                    Course c = ((CourseIO) DataCache.getDao(Model.Course)).findIdByAction(p.getIdAction());
                    if (!c.isCollective()) {
                        continue;
                    }
                }

//        Room s = ((RoomIO) DataCache.getDao(Model.Room)).findId(p.getIdRoom());
                Room s = (Room) DataCache.findId(p.getIdRoom(), Model.Room);

//        Establishment estab = dataCache.getEstabFromId(s.getEstab());
                Establishment estab = (Establishment) DataCache.findId(s.getEstab(), Model.Establishment);

                int duration = p.getStart().getLength(p.getEnd());
                int idx = estabList.indexOf(estab);

                if (p.getType() == Schedule.COURSE) {
                    collective[idx] += duration;
                } else if (p.getType() == Schedule.GROUP || p.getType() == Schedule.MEMBER) {
                    rehearsal[idx] += duration;
                } else if (p.getType() == Schedule.WORKSHOP) {
                    workshop[idx] += duration;
                } else {
                    other[idx] += duration;
                }
            }

            Vector<ScheduleRangeObject> plages = service.getScheduleRange(start, end);
            for (int i = 0; i < plages.size(); i++) {
                ScheduleRangeObject p = plages.elementAt(i);
                int duree = p.getStart().getLength(p.getEnd());
                Room s = p.getRoom();
//        Establishment ee = dataCache.getEstabFromId(s.getEstab());
                Establishment ee = (Establishment) DataCache.findId(s.getEstab(), Model.Establishment);
                int idx = estabList.indexOf(ee);
                Course c = p.getCourse();
                if (c.isCollective()) {
                    if (service.isPro(p.getIdAction(), p.getMember().getId())) {
                        collectivePro[idx] += duree;
                    }
                    continue;
                }
                /*if (OrderLineIO.isPro(p.getMemberId(), dataCache)) {
          partPro[idx] += duree;
        } */
                if (service.isPro(p.getIdAction(), p.getMember().getId())) {
                    partPro[idx] += duree;
                } else {
                    partLeisure[idx] += duree;
                }

            }

            out.println();
            for (int k = 0; k < estabList.getSize(); k++) {
                out.println(MessageUtil.getMessage("export.hours.collective.leisure") + "\t\t" + ((Establishment) estabList.getElementAt(k)).getName() + "\t\t: " + nf.format(collective[k] / 60.0));
            }

            out.println();
            for (int k = 0; k < estabList.getSize(); k++) {
                out.println(MessageUtil.getMessage("export.hours.collective.pro") + "\t\t" + ((Establishment) estabList.getElementAt(k)).getName() + "\t\t: " + nf.format(collectivePro[k] / 60.0));
            }

            out.println();
            for (int k = 0; k < estabList.getSize(); k++) {
                out.println(MessageUtil.getMessage("export.hours.private.leisure") + "\t\t" + ((Establishment) estabList.getElementAt(k)).getName() + "\t\t: " + nf.format(partLeisure[k] / 60.0));
            }

            out.println();
            for (int k = 0; k < estabList.getSize(); k++) {
                out.println(MessageUtil.getMessage("export.hours.private.pro") + "\t\t" + ((Establishment) estabList.getElementAt(k)).getName() + "\t\t: " + nf.format(partPro[k] / 60.0));
            }

            out.println();
            for (int k = 0; k < estabList.getSize(); k++) {
                out.println(MessageUtil.getMessage("export.hours.workshop") + "\t\t" + ((Establishment) estabList.getElementAt(k)).getName() + "\t\t : " + nf.format(workshop[k] / 60.0));
            }

            out.println();
            for (int k = 0; k < estabList.getSize(); k++) {
                out.println(MessageUtil.getMessage("export.hours.rehearsal") + "\t\t" + ((Establishment) estabList.getElementAt(k)).getName() + "\t\t: " + nf.format(rehearsal[k] / 60.0));
            }

            out.println();
            for (int k = 0; k < estabList.getSize(); k++) {
                out.println(MessageUtil.getMessage("export.hours.other") + "\t\t" + ((Establishment) estabList.getElementAt(k)).getName() + "\t\t: " + nf.format(other[k] / 60.0));
            }

            out.close();
            MessagePopup.information(this, MessageUtil.getMessage("export.hours.success.info", file));
        } catch (Exception ex) {
            GemLogger.logException("transfer error", ex, this);
        }
        setCursor(Cursor.getDefaultCursor());
    }

    /*
   *
   * public static void main(String args[])
   *
   * {
   * HeureProf edit = new HeureProf(); edit.liste("Malakoff",
   * 0,false,"01-12-1999","31-12-1999"); edit.liste("Malakoff détail",
   * 0,true,"01-12-1999","31-12-1999"); edit.liste("Chaville",
   * 1,false,"01-12-1999","31-12-1999"); edit.liste("Chaville détail",
   * 1,true,"01-12-1999","31-12-1999"); }
     */
}
