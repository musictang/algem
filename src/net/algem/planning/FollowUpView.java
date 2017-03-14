/*
 * @(#)FollowUpView.java	2.12.0 13/03/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import net.algem.config.FollowUpStatus;
import net.algem.enrolment.FollowUp;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GemTextArea;
import net.algem.util.ui.GridBagHelper;

/**
 * View for followup editing.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.12.0
 */
public class FollowUpView
        extends GemPanel
{

  private JLabel course;
  private GemTextArea textArea;
  private JTextField note;
  private JComboBox status;

  public FollowUpView(String courseName, DateFr date, Hour start, Hour end) {
    course = new JLabel(courseName);
    JLabel detail = new JLabel(date + " " + start + "-" + end);
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    textArea = new GemTextArea(3, 30);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    note = new JTextField(BundleUtil.getLabel("Note.label"));
    note.setMinimumSize(new Dimension(40, note.getPreferredSize().height));
    note.setColumns(6);
    note.setDocument(new NoteDocumentFilter());
    
    status = new JComboBox(FollowUpStatus.values());

    setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(course, 0, 0, 3, 1, GridBagHelper.WEST);
    gb.add(detail, 0, 1, 3, 1, GridBagHelper.WEST);
    gb.add(textArea, 0, 2, 3, 1, GridBagHelper.BOTH, 1.0, 1.0);
    
    gb.add(new JLabel(BundleUtil.getLabel("Note.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(note, 1, 3, 1, 1, GridBagHelper.WEST);
    note.setPreferredSize(new Dimension(20, note.getPreferredSize().height));
    gb.add(status, 2, 3, 1, 1, GridBagHelper.WEST);

  }

  public FollowUpView(DateFr date, Hour start, Hour end) {
    this("", date, start, end);
  }

  public void set(ScheduleRangeObject range, boolean collective) {
    if (range.getFollowUp() != null) {
      if (collective) {
        textArea.setText(range.getNote2());
      } else {
        FollowUp up = range.getFollowUp();
        textArea.setText(up.getContent());
        note.setText(up.getNote());
        status.setSelectedItem(up.getStatusFromResult());
      }
    }
    if (collective) {
      note.setEnabled(false);
      status.setEnabled(false);
    }

  }

  public void set(FollowUp up, boolean collective) {
    if (up != null) {
      textArea.setText(up.getContent());
      if (!collective) {
        note.setText(up.getNote());
        status.setSelectedItem(up.getStatusFromResult());
      }
    }
    if (collective) {
      note.setEnabled(false);
      status.setEnabled(false);
    }
  }

  public FollowUp get() {
    FollowUp up = new FollowUp();
    up.setContent(textArea.getText());
    up.setNote((String) note.getText());
    up.setStatus((short) ((FollowUpStatus) status.getSelectedItem()).getId());
    return up;
  }

  public void clear() {
    course.setText(null);
    textArea.clear();
    note.setText(null);
    status.setSelectedIndex(0);
    note.setEnabled(true);
    status.setEnabled(true);
  }

  class NoteDocumentFilter
          extends PlainDocument
  {

    private static final String REGEX = "^[a-zA-Z0-9,\\.\\+-]+$";

    @Override
    public void insertString(int offset,
            String text, AttributeSet attr)
            throws BadLocationException {
      if (text == null) {
        return;
      }
      if (text.matches(REGEX)) {
        super.insertString(offset, text, attr);
      }
    }

    @Override
    public void replace(int offset, int length, String text, AttributeSet attr) throws BadLocationException {
      if (text == null) {
        return;
      }
      if (text.matches(REGEX)) {
        super.replace(offset, length, text, attr);
      }
    }

  }

}
