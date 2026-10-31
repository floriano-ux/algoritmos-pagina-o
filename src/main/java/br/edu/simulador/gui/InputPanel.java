package br.edu.simulador.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

public class InputPanel extends JPanel {

    public static final String DEFAULT_EXAMPLE = "7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1";
    public static final int DEFAULT_FRAMES = 3;

    private final JTextField pagesTextField;
    private final JSpinner framesSpinner;
    private final JButton simulateButton;
    private final JButton exampleButton;

    public InputPanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(),
                        "Parâmetros da Simulação",
                        0,
                        0,
                        new Font("Segoe UI", Font.BOLD, 13)
                ),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JPanel pagesPanel = new JPanel(new BorderLayout(6, 6));
        JLabel pagesLabel = new JLabel("Cadeia de Páginas (separadas por espaço ou vírgula):");
        pagesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        pagesTextField = new JTextField();
        pagesTextField.setFont(new Font("Consolas", Font.PLAIN, 13));
        pagesTextField.setToolTipText("Exemplo: 7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1");
        pagesPanel.add(pagesLabel, BorderLayout.NORTH);
        pagesPanel.add(pagesTextField, BorderLayout.CENTER);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));

        JLabel framesLabel = new JLabel("Número de Frames (1 a 10):");
        framesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        framesSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_FRAMES, 1, 10, 1));
        framesSpinner.setPreferredSize(new Dimension(65, 28));

        exampleButton = new JButton("Exemplo Clássico");
        exampleButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        exampleButton.setToolTipText("Preenche com a cadeia clássica do livro");
        exampleButton.addActionListener(e -> fillExample());

        simulateButton = new JButton("Simular");
        simulateButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        simulateButton.setPreferredSize(new Dimension(100, 28));

        controlsPanel.add(framesLabel);
        controlsPanel.add(framesSpinner);
        controlsPanel.add(exampleButton);
        controlsPanel.add(simulateButton);

        add(pagesPanel, BorderLayout.CENTER);
        add(controlsPanel, BorderLayout.SOUTH);

        fillExample();
    }

    public void fillExample() {
        pagesTextField.setText(DEFAULT_EXAMPLE);
        framesSpinner.setValue(DEFAULT_FRAMES);
        pagesTextField.requestFocusInWindow();
    }

    public String getPagesText() {
        return pagesTextField.getText();
    }

    public int getFrameCount() {
        return (Integer) framesSpinner.getValue();
    }

    public void setPagesText(String text) {
        pagesTextField.setText(text);
    }

    public void setFrameCount(int frameCount) {
        framesSpinner.setValue(frameCount);
    }

    public void addSimulateListener(ActionListener listener) {
        simulateButton.addActionListener(listener);
        pagesTextField.addActionListener(listener);
    }
}
