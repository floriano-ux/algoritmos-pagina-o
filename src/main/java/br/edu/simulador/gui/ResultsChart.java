package br.edu.simulador.gui;

import br.edu.simulador.core.SimulationResult;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ResultsChart extends JPanel {

    private final DefaultCategoryDataset dataset;
    private final JFreeChart chart;
    private final ChartPanel chartPanel;

    public ResultsChart() {
        super(new BorderLayout());

        this.dataset = new DefaultCategoryDataset();
        this.chart = createChart(dataset);
        this.chartPanel = new ChartPanel(chart);
        this.chartPanel.setMouseWheelEnabled(true);

        add(chartPanel, BorderLayout.CENTER);
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        JFreeChart barChart = ChartFactory.createBarChart(
                "Comparativo de faltas de página",
                "Algoritmo",
                "Faltas de Página",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        barChart.setBackgroundPaint(Color.WHITE);
        barChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(248, 249, 250));
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 13));
        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 13));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 13));

        BarRenderer renderer = new BarRenderer() {
            private final Color[] colors = {
                    new Color(54, 162, 235), // FIFO
                    new Color(255, 159, 64), // LRU
                    new Color(153, 102, 255), // Relogio
                    new Color(75, 192, 192)  // Otimo
            };

            @Override
            public java.awt.Paint getItemPaint(int row, int column) {
                return colors[column % colors.length];
            }
        };

        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.20);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 12));

        plot.setRenderer(renderer);

        return barChart;
    }

    public void updateData(List<SimulationResult> results) {
        dataset.clear();
        if (results != null) {
            for (SimulationResult result : results) {
                dataset.addValue(result.pageFaults(), "Faltas", result.algorithmName());
            }
        }
    }

    public void exportAsPng(Component parent) {
        if (dataset.getColumnCount() == 0) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Não há dados no gráfico para exportar. Execute uma simulação primeiro.",
                    "Exportação de Gráfico",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Gráfico Comparativo como PNG");
        fileChooser.setSelectedFile(new File("grafico_comparativo.png"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imagem PNG (*.png)", "png"));

        int choice = fileChooser.showSaveDialog(parent);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".png")) {
                selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".png");
            }

            try {
                ChartUtils.saveChartAsPNG(selectedFile, chart, 800, 500);
                JOptionPane.showMessageDialog(
                        parent,
                        "Gráfico exportado com sucesso em:\n" + selectedFile.getAbsolutePath(),
                        "Exportação Concluída",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        parent,
                        "Falha ao salvar a imagem do gráfico: " + e.getMessage(),
                        "Erro de Exportação",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public JFreeChart getChart() {
        return chart;
    }
}
