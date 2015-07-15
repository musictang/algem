package net.algem.planning;

import net.algem.config.ColorPlan;
import net.algem.config.ColorPrefs;
import net.algem.contact.Person;
import net.algem.course.Course;
import net.algem.room.Room;

import java.awt.*;

public class ScheduleColorizer {
    private final ColorPrefs colorPrefs;
    private final ActionIO actionIO;

    public ScheduleColorizer(ColorPrefs colorPrefs, ActionIO actionIO) {
        this.colorPrefs = colorPrefs;
        this.actionIO = actionIO;
    }

    /**
     * Gets the background color.
     *
     * @param p schedule
     * @return a color
     */
    public Color getScheduleColor(ScheduleObject p) {
        Color ac = null;
        if (p instanceof ScheduleRangeObject) {
            ac = getActionColor(((ScheduleRangeObject)p).getAction().getId());
        } else {
            ac = getActionColor(p.getIdAction());
        }
        if (ac != null) {
            return (p instanceof ScheduleRangeObject ? ColorPrefs.brighten(ac) : ac);
        }
        return getDefaultScheduleColor(p);
    }

    public Color getDefaultScheduleColor(ScheduleObject p) {
        if (p instanceof ScheduleRangeObject) {
            return colorPrefs.getColor(ColorPlan.RANGE);
        }
        switch (p.getType()) {
            case Schedule.COURSE:
                Room s = ((CourseSchedule) p).getRoom();
                Course cc = ((CourseSchedule) p).getCourse();
                if (s.isCatchingUp()) {
                    return colorPrefs.getColor(ColorPlan.CATCHING_UP);
                } else {
                    if (cc != null && !cc.isCollective()) {
                        return colorPrefs.getColor(ColorPlan.COURSE_INDIVIDUAL);
                    } else {
                        if (cc != null && cc.isInstCode()) {
                            return colorPrefs.getColor(ColorPlan.INSTRUMENT_CO);
                        } else {
                            return colorPrefs.getColor(ColorPlan.COURSE_CO);
                        }
                    }
                }
            case Schedule.ACTION:
                return colorPrefs.getColor(ColorPlan.ACTION);
            case Schedule.MEMBER:
                return  colorPrefs.getColor(ColorPlan.MEMBER_REHEARSAL);
            case Schedule.GROUP:
                return  colorPrefs.getColor(ColorPlan.GROUP_REHEARSAL);
            case Schedule.WORKSHOP:
                return  colorPrefs.getColor(ColorPlan.WORKSHOP);
            case Schedule.TRAINING:
                return  colorPrefs.getColor(ColorPlan.TRAINING);
            case Schedule.STUDIO:
            case Schedule.TECH:
                return  colorPrefs.getColor(ColorPlan.STUDIO);
            case Schedule.ADMINISTRATIVE:
                return colorPrefs.getColor(ColorPlan.ADMINISTRATIVE);
            default:
                return Color.WHITE;
        } // end switch couleurs
    }

    private Color getActionColor(int action) {
        return ((ActionIO) actionIO).getColor(action);
    }

    /**
     * Gets the text color for headers.
     *
     * @param p schedule
     * @return a color
     */
    public Color getTextColor(ScheduleObject p) {
        Color ac = getActionColor(p.getIdAction());
        if (ac != null) {
            return ColorPrefs.getForeground(ac);
        }
        switch (p.getType()) {
            case Schedule.COURSE:
                CourseSchedule cs = (CourseSchedule) p;
                Room r = p.getRoom();
                if (r.isCatchingUp()) {
                    return colorPrefs.getColor(ColorPlan.CATCHING_UP_LABEL);
                } else if (cs.getCourse() != null && cs.getCourse().isCollective()) {
                    return colorPrefs.getColor(ColorPlan.COURSE_CO_LABEL);
                } else {
                    return colorPrefs.getColor(ColorPlan.COURSE_INDIVIDUAL_LABEL);
                }
            case Schedule.WORKSHOP:
                return colorPrefs.getColor(ColorPlan.WORKSHOP_LABEL);
            case Schedule.TRAINING:
                return colorPrefs.getColor(ColorPlan.TRAINING_LABEL);
            case Schedule.GROUP:
                return colorPrefs.getColor(ColorPlan.GROUP_LABEL);
            case Schedule.MEMBER:
                return colorPrefs.getColor(ColorPlan.MEMBER_LABEL);
            case Schedule.STUDIO:
            case Schedule.TECH:
                return colorPrefs.getColor(ColorPlan.STUDIO_LABEL);
            case Schedule.ADMINISTRATIVE:
                return colorPrefs.getColor(ColorPlan.ADMINISTRATIVE_LABEL);
            default:
                return colorPrefs.getColor(ColorPlan.LABEL);
        }
    }

    /**
     * Gets schedule range color.
     *
     * @param p schedule
     * @param c basic color
     * @return a color
     */
    public Color getScheduleRangeColor(ScheduleObject p, Color c) {
        if (p instanceof ScheduleRangeObject) {
            Person a = ((ScheduleRangeObject) p).getMember();
            if (a == null) {
                c = Color.GRAY;
            } else if (a.getId() == 0) {
                c = c.darker();// break color
            }
        }
        return c;
    }
}
