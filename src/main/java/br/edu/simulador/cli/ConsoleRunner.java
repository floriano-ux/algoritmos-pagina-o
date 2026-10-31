package br.edu.simulador.cli;

import br.edu.simulador.core.InputParser;
import br.edu.simulador.core.SimulationResult;
import br.edu.simulador.core.Simulator;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class ConsoleRunner {

    private static final int DEFAULT_FRAMES = 3;

    public static void run(String[] args) {
        PrintStream utf8Out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        PrintStream utf8Err = new PrintStream(System.err, true, StandardCharsets.UTF_8);
        run(args, System.in, utf8Out, utf8Err);
    }

    public static void run(String[] args, InputStream in, PrintStream out, PrintStream err) {
        String pagesInput = null;
        Integer framesInput = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if ("--pages".equalsIgnoreCase(arg) && i + 1 < args.length) {
                pagesInput = args[++i];
            } else if (arg.toLowerCase().startsWith("--pages=")) {
                pagesInput = arg.substring("--pages=".length());
            } else if ("--frames".equalsIgnoreCase(arg) && i + 1 < args.length) {
                try {
                    framesInput = parseAsciiInt(args[++i]);
                } catch (NumberFormatException e) {
                    err.println("Erro: o valor de frames informado não é um número inteiro válido.");
                    return;
                }
            } else if (arg.toLowerCase().startsWith("--frames=")) {
                try {
                    framesInput = parseAsciiInt(arg.substring("--frames=".length()));
                } catch (NumberFormatException e) {
                    err.println("Erro: o valor de frames informado não é um número inteiro válido.");
                    return;
                }
            }
        }

        // Se nao passou via parametro, pede interativamente
        Scanner scanner = new Scanner(in, StandardCharsets.UTF_8);
        if (pagesInput == null || pagesInput.trim().isEmpty()) {
            out.print("Digite a cadeia de páginas (ex.: 7 0 1 2 0 3): ");
            if (scanner.hasNextLine()) {
                pagesInput = scanner.nextLine();
            } else {
                err.println("Erro: nenhuma entrada fornecida.");
                return;
            }
        }

        if (framesInput == null) {
            out.print("Digite o número de frames [padrão " + DEFAULT_FRAMES + "]: ");
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    framesInput = DEFAULT_FRAMES;
                } else {
                    try {
                        framesInput = parseAsciiInt(line);
                    } catch (NumberFormatException e) {
                        err.println("Erro: o valor informado para frames não é um número inteiro válido.");
                        return;
                    }
                }
            } else {
                framesInput = DEFAULT_FRAMES;
            }
        }

        try {
            int[] referenceString = InputParser.parse(pagesInput);
            Simulator simulator = new Simulator();
            List<SimulationResult> results = simulator.run(referenceString, framesInput);

            // Exibe no formato exigido pela especificacao
            for (int i = 0; i < results.size(); i++) {
                SimulationResult res = results.get(i);
                out.printf("Método %d - %s - %d faltas de página%n", (i + 1), res.algorithmName(), res.pageFaults());
            }

        } catch (IllegalArgumentException ex) {
            err.println("Erro de validação: " + ex.getMessage());
        } catch (Exception ex) {
            err.println("Erro inesperado durante a simulação: " + ex.getMessage());
        }
    }

    private static int parseAsciiInt(String value) {
        String trimmed = value.trim();
        if (!trimmed.matches("[-+]?[0-9]+")) {
            throw new NumberFormatException("Valor inválido: " + trimmed);
        }
        return Integer.parseInt(trimmed);
    }
}
