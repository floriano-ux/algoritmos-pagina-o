package br.edu.simulador.gui;

import br.edu.simulador.core.SimulationResult;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

public class ResultsPanel extends JPanel {

    private final JTable summaryTable;
    private final DefaultTableModel tableModel;
    private final ResultsChart resultsChart;
    private final JButton exportButton;

    private int minFaults = -1;

    public ResultsPanel() {
        super(new BorderLayout(8, 8));

        String[] columns = {"Algoritmo", "Faltas de Página", "Taxa de Falta (%)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        summaryTable = new JTable(tableModel);
        summaryTable.setRowHeight(26);
        summaryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        summaryTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        summaryTable.setFillsViewportHeight(true);

        HighlightMinRenderer cellRenderer = new HighlightMinRenderer();
        for (int i = 0; i < summaryTable.getColumnCount(); i++) {
            summaryTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane tableScrollPane = new JScrollPane(summaryTable);
        tableScrollPane.setPreferredSize(new Dimension(320, 150));
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Tabela de Desempenho",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 12)
        ));

        resultsChart = new ResultsChart();
        resultsChart.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Visualização Gráfica",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 12)
        ));

        exportButton = new JButton("Exportar gráfico (PNG)");
        exportButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        exportButton.setToolTipText("Salva o gráfico em arquivo de imagem");
        exportButton.addActionListener(e -> resultsChart.exportAsPng(this));

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportPanel.add(exportButton);

        JPanel leftPanel = new JPanel(new BorderLayout(4, 4));
        leftPanel.add(tableScrollPane, BorderLayout.CENTER);
        leftPanel.add(exportPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        add(resultsChart, BorderLayout.CENTER);
    }

    public void updateResults(List<SimulationResult> results, int totalReferences) {
        tableModel.setRowCount(0);

        if (results == null || results.isEmpty()) {
            minFaults = -1;
            resultsChart.updateData(List.of());
            return;
        }

        minFaults = results.stream()
                .mapToInt(SimulationResult::pageFaults)
                .min()
                .orElse(-1);

        for (SimulationResult result : results) {
            double faultRate = totalReferences > 0
                    ? ((double) result.pageFaults() / totalReferences) * 100.0
                    : 0.0;

            String rateStr = String.format("%.1f%%", faultRate);
            tableModel.addRow(new Object[]{
                    result.algorithmName(),
                    result.pageFaults(),
                    rateStr
            });
        }

        resultsChart.updateData(results);
    }

    public ResultsChart getResultsChart() {
        return resultsChart;
    }

    // Destaca a linha do algoritmo com menor numero de faltas
    private class HighlightMinRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            int faults = -1;
            try {
                Object faultsObj = table.getValueAt(row, 1);
                if (faultsObj instanceof Number) {
                    faults = ((Number) faultsObj).intValue();
                } else if (faultsObj != null) {
                    faults = Integer.parseInt(faultsObj.toString());
                }
            } catch (Exception ignored) {
            }

            boolean isBest = (faults != -1 && faults == minFaults);

            if (!isSelected) {
                if (isBest) {
                    c.setBackground(new Color(232, 245, 233));
                    c.setForeground(new Color(27, 94, 32));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                    c.setForeground(Color.BLACK);
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                }
            }

            if (column >= 1) {
                setHorizontalAlignment(CENTER);
            } else {
                setHorizontalAlignment(LEFT);
            }

            return c;
        }
    }
}
