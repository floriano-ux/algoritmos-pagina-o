package br.edu.simulador.core;

// Interface base para os algoritmos de substituicao de paginas
public interface PageReplacementAlgorithm {

    String name();

    SimulationResult run(int[] referenceString, int frameCount);

    int MAX_FRAMES = 10_000;

    static void validateInput(int[] referenceString, int frameCount) {
        if (referenceString == null || referenceString.length == 0) {
            throw new IllegalArgumentException("A cadeia de referências não pode ser nula ou vazia.");
        }
        if (frameCount < 1) {
            throw new IllegalArgumentException("A quantidade de frames deve ser maior ou igual a 1 (informado: " + frameCount + ").");
        }
        if (frameCount > MAX_FRAMES) {
            throw new IllegalArgumentException(
                    "A quantidade de frames excede o limite máximo permitido de " + MAX_FRAMES + " (informado: " + frameCount + ")."
            );
        }
        for (int page : referenceString) {
            if (page < 0) {
                throw new IllegalArgumentException("A cadeia de referências contém página negativa (" + page + "). As páginas devem ser inteiros >= 0.");
            }
        }
    }
}
