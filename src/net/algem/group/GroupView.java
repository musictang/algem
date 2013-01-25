/*
 * @(#)GroupView.java	2.7.a 03/12/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import net.algem.config.GemParamChoice;
import net.algem.config.MusicStyle;
import net.algem.contact.*;
import net.algem.util.*;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
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
 * @version 2.7.a
 * @since 1.1aa mardi 24 février 2009
 *
 */
public class GroupView
        extends GemPanel
        implements ActionListener, GemEventListener
{

  private static Dimension btDimension = new Dimension(100, 20);
  private static final Contact nullContact = new Contact();

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
  private GemButton bRef;
  private GemButton bMan;
  private GemButton bBook;
  private GemButton refReset, manReset, bookReset;
  private ImageIcon resetIcon;
  private Note note;
  private GroupService service;

  public GroupView(GemDesktop desktop, GroupService service) {
    this.desktop = desktop;
    this.service = service;
    resetIcon = ImageUtil.createImageIcon(ImageUtil.DELETE_ICON);
    noteLabel = new GemLabel();
    noteLabel.setForeground(java.awt.Color.red);

    cbStyle = new GemParamChoice(desktop.getDataCache().getList(Model.MusicStyle));
    cbStyle.setBackground(Color.WHITE);
    //cbStyle.setRenderer(new AlignedListCellRenderer(SwingConstants.RIGHT));
    initButtons();
    initFields();

    GemPanel infos = new GemPanel();
    infos.setLayout(new GridBagLayout());

    GridBagHelper gb = new GridBagHelper(infos);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new GemLabel("N°"), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Name.label")), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Style.label")), 0, 2, 1, 1, GridBagHelper.EAST);
    gb.add(bRef, 0, 3, 1, 1, GridBagHelper.EAST);
    gb.add(bMan, 0, 4, 1, 1, GridBagHelper.EAST);
    gb.add(bBook, 0, 5, 1, 1, GridBagHelper.EAST);

    gb.add(no, 1, 0, 3, 1, GridBagHelper.WEST);
    gb.add(name, 1, 1, 3, 1, GridBagHelper.WEST);
    gb.add(cbStyle, 1, 2, 3, 1, GridBagHelper.WEST);
    gb.add(ref, 1, 3, 3, 1, GridBagHelper.WEST);
    gb.add(man, 1, 4, 3, 1, GridBagHelper.WEST);
    gb.add(book, 1, 5, 3, 1, GridBagHelper.WEST);

    gb.add(refReset, 4, 3, 1, 1, GridBagHelper.WEST);
    gb.add(manReset, 4, 4, 1, 1, GridBagHelper.WEST);
    gb.add(bookReset, 4, 5, 1, 1, GridBagHelper.WEST);

    GemBorderPanel b = new GemBorderPanel();
    GemBorderPanel body = new GemBorderPanel(BorderFactory.createEmptyBorder(50, 50, 50, 50));
    body.setLayout(new BorderLayout());
    body.add(infos, BorderLayout.CENTER);
    sw = new WebSiteView(service.getCategoriesSite());
    sw.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
    body.add(sw, BorderLayout.SOUTH);

    b.add(body);
    this.setLayout(new BorderLayout());
    add(b, BorderLayout.CENTER);
    add(noteLabel, BorderLayout.SOUTH);
  }

  public void setId(int i) {
    id = i;
    no.setText(String.valueOf(i));
  }

  /**
   * Inits the fields for the group {@code g}.
   * @param g current group
   */
  public void setGroup(Group g) {
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

  }

  public String getGroupName() {
    return name.getText();
  }

  /* ajout 1.1a */
  public Note getNote() {
    return note;
  }

  /**
   * Sets a note for the group.
   *
   * @param n the note to display
   * @since 1.1.a
   */
  void setNote(Note n) {
    note = n;
    if (n != null && n.getText() != null) {
      noteLabel.setText(n.getText().replace('\n', ' '));
    }
  }

  /** Sets the referent. */
  public void setRef(Contact c) {
    idref = c.getId();
    ref.setText(c.getFirstnameName());
  }

  /** Sets the manager. */
  public void setMan(Contact c) {
    idman = c.getId();
    man.setText(c.getFirstnameName());
  }

  /** Sets the booker. */
  public void setBook(Contact c) {
    idbook = c.getId();
    book.setText(c.getFirstnameName());
  }

  public Group get() {
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
  public void setCurrentSearch(int search) {
    currentSearch = search;
  }

  /** Gets the current type of contact. */
  public int getCurrentSearch() {
    return currentSearch;
  }

  /** Reset. */
  public void clear() {
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
      setContact(c, getCurrentSearch());
    } else if (evt instanceof NoteEvent) {
      Note n = ((NoteEvent) evt).getNote();
      if (n.getIdPer() == id && n.getPtype() == Person.GROUP) {
        setNote(((NoteEvent) evt).getNote());
      }
    }
  }

  /** Initialisation des boutons du panneau central. */
  private void initButtons() {
    bRef = new GemButton(BundleUtil.getLabel("Group.referent"));
    set(bRef, "setREF");

    bMan = new GemButton(BundleUtil.getLabel("Group.manager"));
    set(bMan, "setMAN");

    bBook = new GemButton(BundleUtil.getLabel("Group.booker"));
    set(bBook, "setTOUR");
    
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
    dpBrowser = new PersonFileSearchCtrl(desktop, "Recherche contact", this);
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
