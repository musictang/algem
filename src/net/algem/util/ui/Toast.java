package net.algem.util.ui;

import net.algem.util.module.GemDesktop;

import java.awt.Color;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Toast {

    private final JComponent component;
    private Point location;
    private final String message;
    private long duration; //in millisecond

    public Toast(JComponent comp, Point toastLocation, String msg, long forDuration) {
        this.component = comp;
        this.location = toastLocation;
        this.message = msg;
        this.duration = forDuration;

        if (this.component != null) {

            if (this.location == null) {
                this.location = component.getLocationOnScreen();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Popup view = null;
                    try {
                        JLabel tip = new JLabel("<html><span style='font-size:18px'>" + message + "</span></html>");
                        tip.setForeground(Color.black);
                        tip.setBackground(Color.lightGray);
                        view = PopupFactory.getSharedInstance().getPopup(component, tip, location.x + 20, location.y + component.getHeight() - 35);
                        view.show();
                        Thread.sleep(duration);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Toast.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        if (view != null) {
                            view.hide();
                        }
                    }
                }
            }).start();
        }
    }


    public static void showToast(JComponent component, String message) {
        new Toast(component, null, message, 2000/*Default 2 Sec*/);
    }

    public static void showToast(JComponent component, String message, Point location, long forDuration) {
        new Toast(component, location, message, forDuration);
    }

    public static void showToast(GemDesktop desktop, String message) {
        showToast((JComponent) desktop.getFrame().getComponent(0), message);
    }
}