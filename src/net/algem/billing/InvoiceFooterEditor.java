/*
 * @(#)InvoiceFooterEditor.java 2.9.3.1 03/03/15
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
package net.algem.billing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.util.*;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Reading and updating of invoice footer.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.3.1
 * @since 2.3.a 27/02/12
 */
public class InvoiceFooterEditor
        extends GemPanel
        implements ActionListener {

    private GemLabel label;
    private JTextArea area;
    private GemButton btOk;
    private GemButton btCancel;
    private GemDesktop desktop;

    /**
     * Module key.
     */
    private String key;

    /**
     * Border spacing.
     */
    private static final int bp = 10;

    public InvoiceFooterEditor(String key, GemDesktop desktop) {
        this.desktop = desktop;
        this.key = key;
        init();
    }

    private void init() {

        GemPanel content = new GemBorderPanel(BorderFactory.createEmptyBorder(bp, bp, bp, bp));
        content.setLayout(new BorderLayout(0, 10));

        label = new GemLabel(MessageUtil.getMessage("invoice.footer.editor"));
        area = new JTextArea();
        area.setMargin(new Insets(bp, bp, bp, bp));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setText(read());
        content.add(label, BorderLayout.NORTH);
        content.add(area, BorderLayout.CENTER);

        btOk = new GemButton(GemCommand.VALIDATION_CMD);
        btOk.addActionListener(this);
        btCancel = new GemButton(GemCommand.CANCEL_CMD);
        btCancel.addActionListener(this);

        GemPanel boutons = new GemPanel(new GridLayout(1, 2));
        boutons.add(btOk);
        boutons.add(btCancel);

        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);
        add(boutons, BorderLayout.SOUTH);
    }

    /**
     * Retrieves the actual footer.
     *
     * @return a list of strings
     */
    public static List<String> getFooter() {

        List<String> lines = new ArrayList<String>();
        String s = null;

        try {
            File f = getFile();
            if (f == null) {
                throw new IOException(MessageUtil.getMessage("file.not.found.exception", ""));
            }
            try (FileReader reader = new FileReader(f);
                    BufferedReader br = new BufferedReader(reader);) {
                while ((s = br.readLine()) != null) {
                    lines.add(s);
                }
            }
        } catch (IOException ex) {
            GemLogger.logException(ex);
            lines.add(ex.getMessage());
        }
        return lines;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (btOk == e.getSource()) {
            write();
        }
        desktop.removeModule(key);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    /**
     * Fills the text area.
     *
     * @return a string representing the invoice footer
     */
    private String read() {
        StringBuilder sb = new StringBuilder();
        List<String> ls = getFooter();
        for (String s : ls) {
            sb.append(s).append(TextUtil.LINE_SEPARATOR);
        }
        return sb.toString();
    }

    /**
     * Updates footer. The infos into the text area are saved in the file.
     * {@code FileUtil.INVOICE_FOOTER_FILE}.
     */
    private void write() {

        BufferedWriter bw = null;
        try {
            File f = getFile();
            if (f != null && f.canWrite()) {
                bw = new BufferedWriter(new FileWriter(f));
                bw.append(area.getText());
            } else {
                throw new IOException(MessageUtil.getMessage("file.writing.exception", (f == null ? "" : f.getAbsolutePath())));
            }
        } catch (FileNotFoundException f) {
            GemLogger.logException(f);
            MessagePopup.warning(this, f.getMessage());
        } catch (IOException ex) {
            GemLogger.logException(ex);
            MessagePopup.warning(this, ex.getMessage());
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ex) {
                GemLogger.logException(ex);
            }
        }

    }

    private static File getFile() {
        String filePath = ConfigUtil.getConf(ConfigKey.INVOICE_FOOTER.getKey());
        if (filePath != null) {
            return new File(filePath);
        }
        return null;
    }

}
