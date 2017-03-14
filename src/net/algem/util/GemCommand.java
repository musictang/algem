/*
 * @(#)GemCommand.java 2.9.4.13 03/11/15
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
package net.algem.util;

/**
 * Generic action commands.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.2.c
 */
public class GemCommand
{
  public static final String CANCEL_CMD = BundleUtil.getLabel("Action.cancel.label");
  public static final String SEARCH_CMD = BundleUtil.getLabel("Action.search.label");
  public static final String NEW_SEARCH_CMD = BundleUtil.getLabel("Action.new.search.label");
  public static final String ERASE_CMD = BundleUtil.getLabel("Action.erase.label");
  public static final String EXPORT_CMD = BundleUtil.getLabel("Action.export.label");
  public static final String SAVE_CMD = BundleUtil.getLabel("Action.save.label");
  public static final String BROWSE_CMD = BundleUtil.getLabel("Action.browse.label");
  public static final String ADD_CMD = BundleUtil.getLabel("Action.add.label");
  public static final String ADD_NEWVAL_CMD = BundleUtil.getLabel("Action.add.new.val.label");
  public static final String CLOSE_CMD = BundleUtil.getLabel("Action.close.label");
  public static final String CREATE_CMD = BundleUtil.getLabel("Action.create.label");
  public static final String MODIFY_CMD = BundleUtil.getLabel("Action.modify.label");
  public static final String REMOVE_CMD = BundleUtil.getLabel("Action.remove.label");
  public static final String DELETE_CMD = BundleUtil.getLabel("Action.suppress.label");
  public static final String NOTE_CMD = BundleUtil.getLabel("Note.label");
  public static final String ECHEANCIER_CMD = BundleUtil.getLabel("Action.schedule.payment.label");
  public static final String ABORT = BundleUtil.getLabel("Action.abort.label");
  //CtrlValidation
  public static final String VALIDATION_CMD = BundleUtil.getLabel("Action.validation.label");
  public static final String VALIDATE_CMD = BundleUtil.getLabel("Action.validate.label");
  public static final String LOAD_CMD = BundleUtil.getLabel("Action.load.label");
  public static final String OK_CMD = BundleUtil.getLabel("Action.ok.label");
  public static final String ALL_CMD = BundleUtil.getLabel("Action.all.label");
  public static final String NEXT_CMD = BundleUtil.getLabel("Action.next.label");
  public static final String PREVIOUS_CMD = BundleUtil.getLabel("Action.previous.label");
  public static final String BACK_CMD = BundleUtil.getLabel("Action.back.label");
  public static final String APPLY_CMD = BundleUtil.getLabel("Action.apply.label");
  public static final String EDIT_CMD = BundleUtil.getLabel("Action.edit.label");
  public static final String VIEW_EDIT_CMD = BundleUtil.getLabel("Action.view.edit.label");
  public static final String PRINT_CMD = BundleUtil.getLabel("Action.print.label");
  public static final String MAIL_CMD = BundleUtil.getLabel("Action.mail.label");
  public static final String DUPLICATE_CMD = BundleUtil.getLabel("Action.duplicate.label");
  public static final String RENAME_CMD = BundleUtil.getLabel("Rename.label");

  private GemCommand(String val) {
  }
}
