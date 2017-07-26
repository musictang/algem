/*
 * @(#)GroupView.java	2.15.0 26/07/2017
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
package net.algem.group;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import net.algem.config.GemParamChoice;
import net.algem.config.MusicStyle;
import net.algem.contact.*;
import net.algem.util.*;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.jdesktop.DesktopHandler;
import net.algem.util.jdesktop.DesktopOpenHandler;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemDesktopCtrl;
import net.algem.util.module.GemModule;
import net.algem.util.ui.*;

/**
 * Group view info.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 1.1aa mardi 24 février 2009
 *
 */
public class GroupView
        extends GemPanel
        implements ActionListener, GemEventListener
{

  private static Dimension btDimension = new Dimension(100, 20);
  private static final Contact nullContact = new Contact();

//  private static final String PARENT_DIR = "groupes/";

   /** Default bio dir name. */
  private static final String BIO_DIR = "bio";

  /** Default datasheet dir name. */
  private static final String DATASHEET_DIR = "fiche-technique";

  /** Default stage plan name. */
  static final String STAGE_DIR = "plan-scene";


  static {
    nullContact.setId(0);
    nullContact.setName("a définir");
    nullContact.setFirstName("");
  }
  private GemDesktop desktop;
  private PersonFileSearchCtrl dpBrowser;
  private int id;
  private int idref;
  private int idman;
  private int idbook;
  private int currentSearch;
  private GemNumericField no;
  private GemField name;
  private GemParamChoice cbStyle;
  private GemField ref;
  private GemField man;
  private GemField book;
  private WebSiteView sw;
  private GemLabel noteLabel;
  private GemButton refBt;
  private GemButton manBt;
  private GemButton bookBt;
  private GemButton refReset, manReset, bookReset;
  private ImageIcon resetIcon;
  private Note note;
  private GemGroupService service;

  private GemButton bioBt;
  private GemButton dataSheetBt;
  private GemButton stagePlanBt;

  private File bio;
  private File dataSheet;
  private File stage;
  private DesktopHandler handler;

  public GroupView(GemDesktop desktop, GemGroupService service) {
    this.desktop = desktop;
    this.service = service;
    handler = new DesktopOpenHandler();

    resetIcon = ImageUtil.createImageIcon(ImageUtil.DELETE_ICON);
    noteLabel = new GemLabel();

    noteLabel.setForeground(java.awt.Color.red);
    noteLabel.setFont(noteLabel.getFont().deriveFont(Font.BOLD));

    cbStyle = new GemParamChoice(desktop.getDataCache().getList(Model.MusicStyle));
    cbStyle.setBackground(Color.WHITE);
    //cbStyle.setRenderer(new AlignedListCellRenderer(SwingConstants.RIGHT));
    initButtons();
    initFields();

    GemPanel contactPanel = new GemPanel();
    contactPanel.setLayout(new GridBagLayout());

    GridBagHelper gb = new GridBagHelper(contactPanel);

    gb.add(new GemLabel("N°"), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Name.label")), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Style.label")), 0, 2, 1, 1, GridBagHelper.EAST);
    gb.add(refBt, 0, 3, 1, 1, GridBagHelper.EAST);
    gb.add(manBt, 0, 4, 1, 1, GridBagHelper.EAST);
    gb.add(bookBt, 0, 5, 1, 1, GridBagHelper.EAST);

    gb.add(no, 1, 0, 3, 1, GridBagHelper.WEST);
    gb.add(name, 1, 1, 3, 1, GridBagHelper.WEST);
    gb.add(cbStyle, 1, 2, 3, 1, GridBagHelper.WEST);
    gb.add(ref, 1, 3, 3, 1, GridBagHelper.WEST);
    gb.add(man, 1, 4, 3, 1, GridBagHelper.WEST);
    gb.add(book, 1, 5, 3, 1, GridBagHelper.WEST);

    gb.add(refReset, 4, 3, 1, 1, GridBagHelper.WEST);
    gb.add(manReset, 4, 4, 1, 1, GridBagHelper.WEST);
    gb.add(bookReset, 4, 5, 1, 1, GridBagHelper.WEST);

    sw = new WebSiteView(service.getCategoriesSite(), false);
    gb.add(sw, 0, 6, 5, 1, GridBagHelper.BOTH, GridBagHelper.WEST);

    ActionListener groupFileListener = new GroupDocListener();

    GemPanel buttons = new GemPanel(new GridLayout(1,3));
    bioBt = new GemButton(BundleUtil.getLabel("Bio.label"));
    bioBt.addActionListener(groupFileListener);
    dataSheetBt = new GemButton(BundleUtil.getLabel("Datasheet.label"));
    dataSheetBt.addActionListener(groupFileListener);
    stagePlanBt = new GemButton(BundleUtil.getLabel("Stage.plan.label"));
    stagePlanBt.addActionListener(groupFileListener);

    buttons.add(bioBt);
    buttons.add(dataSheetBt);
    buttons.add(stagePlanBt);
    gb.add(buttons, 0, 7, 5, 1, GridBagHelper.BOTH, GridBagHelper.WEST);

    this.setLayout(new BorderLayout());
    add(contactPanel, BorderLayout.CENTER);
    GemBorderPanel labelBox = new GemBorderPanel(new FlowLayout(FlowLayout.LEFT));
    labelBox.add(noteLabel);
    add(labelBox, BorderLayout.SOUTH);
  }

  void setId(int i) {
    id = i;
    no.setText(String.valueOf(i));
  }

  /**
   * Inits the fields for the group {@code g}.
   * @param g current group
   */
  void setGroup(Group g) {
    if (g == null) {
      g = new Group();
    }
    id = g.getId();
    no.setText(String.valueOf(g.getId()));
    name.setText(g.getName());
    cbStyle.setKey(g.getStyle().getId());
    setContact(g);
    setNote(g.getNote());
    sw.setSites(g.getSites());

    setButtonAccess(id);
  }

  /**
   * Sets a note for the group.
   *
   * @param n the note to display
   * @since 1.1.a
   */
  private void setNote(Note n) {
    note = n;
    if (n != null && n.getText() != null) {
      noteLabel.setText(n.getText().replace('\n', ' '));
    }
  }

  /** Sets the referent. */
  private void setRef(Contact c) {
    idref = c.getId();
    ref.setText(getContactName(c));
  }

  /** Sets the manager. */
  private void setMan(Contact c) {
    idman = c.getId();
    man.setText(getContactName(c));
  }

  /** Sets the booker. */
  private void setBook(Contact c) {
    idbook = c.getId();
    book.setText(getContactName(c));
  }

  private String getContactName(Contact c) {
    String n = c.getFirstnameName();
    if (n != null && n.length() > 0 ) {
      return n;
    }
    n = c.getOrgName();
    if (n != null && n.length() > 0) {
      return n;
    }
    return String.valueOf(c.getId());
  }

  /**
   * Sets the state of the file's access buttons.
   * If a particular file exists for this group, the button is enabled.
   * @param groupId
   */
  private void setButtonAccess(int groupId) {

    String parent = service.getDocumentPath();

    bio = FileUtil.findLastFile(parent, BIO_DIR, groupId);
    bioBt.setEnabled(bio != null && bio.canRead());

    dataSheet = FileUtil.findLastFile(parent, DATASHEET_DIR, groupId);
    dataSheetBt.setEnabled(dataSheet != null && dataSheet.canRead());

    stage = FileUtil.findLastFile(parent, STAGE_DIR, groupId);
    stagePlanBt.setEnabled(stage != null && stage.canRead());
  }

  Group get() {
    Group g = new Group();
    g.setId(id);
    g.setName(name.getText());
    g.setStyle((MusicStyle) cbStyle.getSelectedItem());
    g.setIdContact(idref, idman, idbook);

    g.setSites(sw.getSites());
    int i = 0;
    if (g.getSites() != null) {
      for(WebSite s : g.getSites()) {
        s.setPtype(Person.GROUP);
        s.setIdx(i++);
      }
    }
    g.setNote(note);

    return g;
  }

  /** Specifies the type of contact to search. */
  private void setCurrentSearch(int type) {
    currentSearch = type;
  }

  /** Reset. */
  void clear() {
    no.setText("");
    name.setText("");
    ref.setText("");
    book.setText("");
    man.setText("");

    setId(0);
    idref = 0;
    idman = 0;
    idbook = 0;
    noteLabel.setText("");
    currentSearch = 0;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String arg = evt.getActionCommand();
    if ("setREF".equals(arg)) {
      setCurrentSearch(Group.REFERENT);
      findContact();
    } else if ("setMAN".equals(arg)) {
      setCurrentSearch(Group.MANAGER);
      findContact();
    } else if ("setTOUR".equals(arg)) {
      setCurrentSearch(Group.TOURNEUR);
      findContact();
    } else if ("resetREF".equals(arg)) {
      setRef(nullContact);
    } else if ("resetMAN".equals(arg)) {
      setMan(nullContact);
    } else if ("resetTOUR".equals(arg)) {
      setBook(nullContact);
    }

  }

  @Override
  public void postEvent(GemEvent evt) {
    if (evt instanceof ContactSelectEvent) {
      Contact c = ((ContactSelectEvent) evt).getContact();
      setContact(c, currentSearch);
    } else if (evt instanceof NoteEvent) {
      Note n = ((NoteEvent) evt).getNote();
      if (n.getIdPer() == id && n.getPtype() == Person.GROUP) {
        setNote(((NoteEvent) evt).getNote());
      }
    }
  }

  /** Initialisation des boutons du panneau central. */
  private void initButtons() {
    refBt = new GemButton(BundleUtil.getLabel("Group.referent"));
    set(refBt, "setREF");

    manBt = new GemButton(BundleUtil.getLabel("Group.manager"));
    set(manBt, "setMAN");

    bookBt = new GemButton(BundleUtil.getLabel("Group.booker"));
    set(bookBt, "setTOUR");

    refReset = new GemButton(resetIcon);
    setReset(refReset, "resetREF");

    manReset = new GemButton(resetIcon);
    setReset(manReset, "resetMAN");

    bookReset = new GemButton(resetIcon);
    setReset(bookReset, "resetTOUR");

  }

  private void setReset(GemButton b, String cmd) {
    b.setActionCommand(cmd);
    b.setToolTipText(GemCommand.DELETE_CMD);
    b.addActionListener(this);
  }

  private void set(GemButton b, String cmd) {
    b.setActionCommand(cmd);
    b.addActionListener(this);
    b.setToolTipText(GemCommand.MODIFY_CMD);
    b.setPreferredSize(btDimension);
  }

  private void initFields() {
    GrpClickContactListener dbcl = new GrpClickContactListener();
    String tip = MessageUtil.getMessage("contact.clic.info");
    no = new GemNumericField(6);
    no.setEditable(false);
    name = new GemField(30);
    /* Attention à ne pas mettre une valeur trop élevée au paramètre */
    ref = new GemField(30);
    ref.setEditable(false);
    ref.setBackground(Color.WHITE);
    ref.addMouseListener(dbcl);
    ref.setToolTipText(tip);

    man = new GemField(30);
    man.setEditable(false);
    man.setBackground(Color.WHITE);
    man.addMouseListener(dbcl);
    man.setToolTipText(tip);

    book = new GemField(30);
    book.setEditable(false);
    book.setBackground(Color.WHITE);
    book.addMouseListener(dbcl);
    book.setToolTipText(tip);

  }

  private void findContact() {
    dpBrowser = new PersonFileSearchCtrl(desktop, BundleUtil.getLabel("Contact.browser.label"), this);
    dpBrowser.init();
    desktop.addPanel("Contact", dpBrowser, GemModule.S_SIZE);
  }

  private void setContact(Group g) {

    setRef(g.getReferent());
    setMan(g.getManager());
    setBook(g.getBooker());

  }

  private void setContact(Contact c, int current) {
    if (Group.REFERENT == current) {
      setRef(c);
    } else if (Group.MANAGER == current) {
      setMan(c);
    } else if (Group.TOURNEUR == current) {
      setBook(c);
    }
  }

  /**
   * Opens a contact file.
   *
   * @param id contact id
   */
  private void viewCard(int id) {
    PersonFileEditor m = ((GemDesktopCtrl) desktop).getPersonFileEditor(id);
    if (m == null) {
      PersonFile dossier = new PersonFile(service.getContact(id));
      try {
        ((PersonFileIO) DataCache.getDao(Model.PersonFile)).complete(dossier);
      } catch (SQLException ex) {
        GemLogger.logException("complete dossier browser", ex);
      }
      PersonFileEditor editor = new PersonFileEditor(dossier);
      desktop.addModule(editor);
    } else {
      desktop.setSelectedModule(m);
    }
  }

  /** Double-click listener for referent, manager and booker fields. */
  class GrpClickContactListener
          extends MouseAdapter
  {

    @Override
    public void mouseClicked(MouseEvent e) {
      int id = 0;
      if (e.getSource() == ref) {
        id = idref;
      } else if (e.getSource() == man) {
        id = idman;
      } else if (e.getSource() == book) {
        id = idbook;
      }
      if (e.getClickCount() == 2 && id != 0) {
        viewCard(id);
      }
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  private class GroupDocListener implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == bioBt) {
        if (bio != null) {
          FileUtil.open(handler, bio.getAbsolutePath());
        }
      } else if (src == dataSheetBt) {
         if (dataSheet != null) {
          FileUtil.open(handler, dataSheet.getAbsolutePath());
        }
      } else if (src == stagePlanBt) {
        if (stage != null) {
          FileUtil.open(handler, stage.getAbsolutePath());
        }
      }
    }

  }

  /* pour aligner les items dans la combobox */
  /* class AlignedListCellRenderer extends DefaultListCellRenderer { private int
   * align;
   *
   * public AlignedListCellRenderer(int align) { this.align = align; }
   *
   * public Component getListCellRendererComponent(JList list, Object value, int
   * index, boolean isSelected, boolean cellHasFocus) { //
   * DefaultListCellRenderer uses a JLabel as the rendering component: JLabel
   * lbl = (JLabel)super.getListCellRendererComponent( list, value, index,
   * isSelected, cellHasFocus); lbl.setHorizontalAlignment(align); return lbl; }
   * } */
}
