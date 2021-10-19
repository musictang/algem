package net.algem.planning.fact.ui;

import net.algem.planning.fact.services.PlanningFact;
import net.algem.util.DataCache;
import net.algem.util.model.Model;

import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import net.algem.util.GemLogger;

public class FactTableModel extends AbstractTableModel {
    private final List<PlanningFact> facts;

    public FactTableModel(List<PlanningFact> facts) {
        this.facts = facts;
    }

    @Override
    public int getRowCount() {
        return facts.size();
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Date";
            case 1:
                return "Type";
            case 2:
                return "Id planning";
            case 3:
                return "Prof";
            case 4:
                return "Commentaire";
            case 5:
                return "Durée (mins)";
            case 6:
                return "Statut";
            case 7:
                return "Niveau";
            default:
                return null;
        }
    }

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PlanningFact fact = facts.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fact.getDate());
            case 1:
                return fact.getType();
            case 2:
                return fact.getPlanning();
            case 3:
                try {
                    return DataCache.findId(fact.getProf(), Model.Teacher) + " (" + fact.getProf() + ")";
                } catch (SQLException e) {
                    GemLogger.logException(e);
                    return null;
                }
            case 4:
                return fact.getCommentaire();
            case 5:
                return fact.getDureeMinutes();
            case 6:
                return fact.getStatut();
            case 7:
                return fact.getNiveau();
            default:
                return null;
        }
    }
}
