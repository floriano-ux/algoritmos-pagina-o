package br.edu.simulador.gui;

import br.edu.simulador.core.Step;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class StepTableModel extends AbstractTableModel {

    private final List<Step> steps;
    private final int frameCount;
    private final String[] columnNames;

    public StepTableModel(List<Step> steps, int frameCount) {
        this.steps = steps != null ? steps : List.of();
        this.frameCount = frameCount;

        int totalColumns = 3 + frameCount + 1;
        this.columnNames = new String[totalColumns];
        this.columnNames[0] = "Passo";
        this.columnNames[1] = "Página";
        this.columnNames[2] = "Falta?";

        for (int i = 0; i < frameCount; i++) {
            this.columnNames[3 + i] = "Frame " + i;
        }

        this.columnNames[totalColumns - 1] = "Página Substituída";
    }

    @Override
    public int getRowCount() {
        return steps.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Step step = steps.get(rowIndex);

        if (columnIndex == 0) {
            return rowIndex + 1;
        } else if (columnIndex == 1) {
            return step.page();
        } else if (columnIndex == 2) {
            return step.fault() ? "SIM" : "NÃO";
        } else if (columnIndex >= 3 && columnIndex < 3 + frameCount) {
            int frameIndex = columnIndex - 3;
            int[] snapshot = step.framesSnapshot();
            if (snapshot != null && frameIndex < snapshot.length) {
                int page = snapshot[frameIndex];
                return page == -1 ? "-" : String.valueOf(page);
            }
            return "-";
        } else if (columnIndex == 3 + frameCount) {
            return step.evictedPage() != null ? String.valueOf(step.evictedPage()) : "-";
        }

        return "";
    }

    public boolean isFault(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < steps.size()) {
            return steps.get(rowIndex).fault();
        }
        return false;
    }
}
