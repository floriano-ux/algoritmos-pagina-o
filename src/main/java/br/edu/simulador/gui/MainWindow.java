package br.edu.simulador.gui;

import br.edu.simulador.core.InputParser;
import br.edu.simulador.core.SimulationResult;
import br.edu.simulador.core.Simulator;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

public class MainWindow extends JFrame {

    private final InputPanel inputPanel;
    private final ResultsPanel resultsPanel;
    private final JTabbedPane stepTabs;
    private final Simulator simulator;

    public MainWindow() {
        super("Simulador de Substituição de Páginas - Sistemas Operacionais");
        this.simulator = new Simulator();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(950, 650));
        setPreferredSize(new Dimension(1120, 800));

        JPanel contentPane = new JPanel(new BorderLayout(8, 8));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel = new InputPanel();
        inputPanel.addSimulateListener(e -> executeSimulation());
        contentPane.add(inputPanel, BorderLayout.NORTH);

        resultsPanel = new ResultsPanel();

        stepTabs = new JTabbedPane();
        stepTabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        stepTabs.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Passo a Passo da Memória por Algoritmo",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 12)
        ));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resultsPanel, stepTabs);
        splitPane.setResizeWeight(0.50);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(6);

        contentPane.add(splitPane, BorderLayout.CENTER);
        setContentPane(contentPane);

        pack();
        setLocationRelativeTo(null);

        // Executa a primeira vez com os valores padrao
        executeSimulation();
    }

    public void executeSimulation() {
        String pagesText = inputPanel.getPagesText();
        int frameCount = inputPanel.getFrameCount();

        try {
            int[] referenceString = InputParser.parse(pagesText);
            List<SimulationResult> results = simulator.run(referenceString, frameCount);

            resultsPanel.updateResults(results, referenceString.length);
            updateStepTabs(results, frameCount);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro inesperado durante a execução da simulação:\n" + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateStepTabs(List<SimulationResult> results, int frameCount) {
        stepTabs.removeAll();

        for (SimulationResult result : results) {
            StepTableModel tableModel = new StepTableModel(result.steps(), frameCount);
            JTable table = new JTable(tableModel);
            table.setRowHeight(24);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            table.setFillsViewportHeight(true);

            StepCellRenderer renderer = new StepCellRenderer(tableModel);
            for (int col = 0; col < table.getColumnCount(); col++) {
                table.getColumnModel().getColumn(col).setCellRenderer(renderer);
            }

            JScrollPane scrollPane = new JScrollPane(table);
            String tabTitle = String.format("%s (%d faltas)", result.algorithmName(), result.pageFaults());
            stepTabs.addTab(tabTitle, scrollPane);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }

    // Pinta a linha de vermelho se houve falta de pagina ou verde se foi hit
    private static class StepCellRenderer extends DefaultTableCellRenderer {
        private final StepTableModel model;

        public StepCellRenderer(StepTableModel model) {
            this.model = model;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            boolean isFault = model.isFault(row);

            if (!isSelected) {
                if (isFault) {
                    c.setBackground(new Color(255, 235, 238));
                    c.setForeground(new Color(183, 28, 28));
                } else {
                    c.setBackground(new Color(241, 248, 233));
                    c.setForeground(new Color(46, 125, 50));
                }
            }

            setHorizontalAlignment(CENTER);
            return c;
        }
    }
}
