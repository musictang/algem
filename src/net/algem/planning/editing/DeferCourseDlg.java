/*
 * @(#)DeferCourseDlg.java	2.7.a 26/11/12

 */
package net.algem.planning.editing;

import java.awt.Frame;
import java.util.Date;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.ScheduleObject;
import net.algem.room.Room;
import net.algem.room.RoomIO;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;

/**
 * Dialog for course time modification.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 *
 */
public class DeferCourseDlg
        extends ModifPlanDlg
{

  private DataCache dataCache;
  private int roomId;
  private PutOffCourseView pv;
  private ScheduleObject schedule;

  public DeferCourseDlg(Frame f, DataCache dc, ScheduleObject _plan, String titleKey) {
    super(f);
    dataCache = dc;
    schedule = _plan;
    pv = new PutOffCourseView(dataCache);
    setTitle(schedule.getScheduleLabel());
    setDate(schedule.getDay().getDate());
    setRoom(schedule.getPlace());
    setHour(schedule.getStart(), schedule.getEnd());
    validation = false;
    dlg = new JDialog(f, true);
    addContent(pv, titleKey);
  }

  @Override
  public void entry() {
    dlg.setVisible(true);
  }

  @Override
  public boolean isEntryValid() {
    if (!pv.getHourEnd().after(pv.getHourStart())) {
      JOptionPane.showMessageDialog(dlg,
                                    MessageUtil.getMessage("hour.range.error"),
                                    MessageUtil.getMessage("invalid.time.slot"),
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }
    /* Condition annulée car on peut différer un cours par anticipation à une date antérieure */
    /*if (pv.getNewStart().before(pv.getStart()))
    {
    JOptionPane.showMessageDialog(dlg,
    "Date de end invalide",
    "Plage de date invalide",
    JOptionPane.ERROR_MESSAGE);
    return false;
    }*/

    if (pv.getHourStart().getDuration(pv.getHourEnd()) != pv.getOldHourStart().getDuration(pv.getOldHourEnd())) {
      JOptionPane.showMessageDialog(dlg,
                                    MessageUtil.getMessage("invalid.duration"),
                                    MessageUtil.getMessage("invalid.time.slot"),
                                    JOptionPane.ERROR_MESSAGE);
      return false;

    }

    int room = getNewRoom();
    /* 1.1c Ajout d'une condition pour les salles de type exterieur */
    RoomIO roomIO = (RoomIO) DataCache.getDao(Model.Room);
    Room r = roomIO.findId(roomId); //salle habituelle du planning
    Room n = roomIO.findId(room); //nouvelle salle
    // SEULEMENT POUR MUSIQUES TANGENTES
    if (r.getEstab() > 13000 && n.getName().toLowerCase().startsWith("rattrap")) {
      JOptionPane.showMessageDialog(dlg,
                                    "La salle de rattrapage n'est pas valide pour les cours à l'extérieur.",
                                    MessageUtil.getMessage("room.invalid.choice"),
                                    JOptionPane.ERROR_MESSAGE);
      return false;

    }
    return true;
  }

  @Override
  public boolean isValidate() {
    return validation;
  }

  private void setTitle(String t) {
    pv.setTitle(t);
  }

  private void setDate(Date d) {
    pv.setStart(d);
    pv.setNewStart(d);
  }
  
  private void setHour(Hour start, Hour end) {
    pv.setHour(start, end);
  }

  private void setRoom(int id) {
    roomId = id;
    pv.setRoom(id);
  }

  public DateFr getStart() {
    return pv.getStart();
  }

  public DateFr getNewStart() {
    return pv.getNewStart();
  }

  public Hour getNewHourStart() {
    return pv.getHourStart();
  }

  public Hour getNewHourEnd() {
    return pv.getHourEnd();
  }

  public int getNewRoom() {
    return pv.getRoomId();
  }

}

