package br.edu.simulador.core;

import java.util.ArrayList;
import java.util.List;

public final class InputParser {

    private InputParser() {
    }

    // Faz o parse de entradas separadas por espaco ou virgula
    public static int[] parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("A cadeia de páginas não pode ser nula ou vazia.");
        }

        String[] tokens = input.trim().split("[,\\s]+");
        if (tokens.length == 0) {
            throw new IllegalArgumentException("Nenhuma página válida foi identificada na entrada.");
        }

        List<Integer> pages = new ArrayList<>(tokens.length);

        for (String token : tokens) {
            String trimmed = token.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            if (!trimmed.matches("[-+]?[0-9]+")) {
                throw new IllegalArgumentException("O valor '" + trimmed + "' não é um número inteiro válido.");
            }

            int value;
            try {
                value = Integer.parseInt(trimmed);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("O valor '" + trimmed + "' não é um número inteiro válido.");
            }

            if (value < 0) {
                throw new IllegalArgumentException(
                        "A cadeia contém página inválida (negativa: " + value + "). As páginas devem ser inteiros >= 0."
                );
            }

            pages.add(value);
        }

        if (pages.isEmpty()) {
            throw new IllegalArgumentException("A cadeia de páginas não contém nenhum número.");
        }

        int[] result = new int[pages.size()];
        for (int i = 0; i < pages.size(); i++) {
            result[i] = pages.get(i);
        }

        return result;
    }
}
