package br.edu.simulador;

import br.edu.simulador.cli.ConsoleRunner;
import br.edu.simulador.gui.MainWindow;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        // Se passar --cli abre pelo terminal, senao abre a janela Swing
        boolean isCli = Arrays.stream(args).anyMatch(arg -> "--cli".equalsIgnoreCase(arg.trim()));

        if (isCli) {
            ConsoleRunner.run(args);
        } else {
            MainWindow.main(args);
        }
    }
}
