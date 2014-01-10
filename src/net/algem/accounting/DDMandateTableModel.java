
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.algem.accounting;

import java.text.DateFormat;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 * Direct debit mandate table model.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.p
 * @since 2.7.p 06/01/2014
 */
public class DDMandateTableModel 
extends JTableModel

{
  
public DDMandateTableModel() {
    header = new String[]{BundleUtil.getLabel("Payer.label"),
                          BundleUtil.getLabel("Direct.debit.creation.label"),
                          BundleUtil.getLabel("Direct.debit.signature.label"),
                          BundleUtil.getLabel("Direct.debit.seq.type.label"),
                          BundleUtil.getLabel("Recurrent.label")};
    
    
  }

  @Override
  public int getIdFromIndex(int i) {
    return ((DDMandate) getItem(i)).getId();
  }
  
   @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:  
        return Integer.class;
      case 1:
      case 2:
        return DateFr.class;
      case 3:
        return DDSeqType.class;
      case 4:
        return Boolean.class;
      default:
        return Object.class;
    }
  }

  @Override
  public Object getValueAt(int line, int col) {
    DDMandate dd = (DDMandate) tuples.elementAt(line);
    switch (col) {
      case 0:
        return dd.getIdper();
      case 1:
        return new DateFr(dd.getCreation());
        case 2:
        return new DateFr(dd.getDateSign());
      case 3:
        return dd.getSeqType();
      case 4:
        return dd.isRecurrent();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
//    throw new UnsupportedOperationException("Not supported yet.");
  }
}
