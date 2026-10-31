package br.edu.simulador.core;

import java.util.ArrayList;
import java.util.List;

public class Simulator {

    private final List<PageReplacementAlgorithm> algorithms;

    // Inicializa com os quatro algoritmos padrao
    public Simulator() {
        this(List.of(
                new FifoAlgorithm(),
                new LruAlgorithm(),
                new ClockAlgorithm(),
                new OptimalAlgorithm()
        ));
    }

    public Simulator(List<PageReplacementAlgorithm> algorithms) {
        if (algorithms == null || algorithms.isEmpty()) {
            throw new IllegalArgumentException("A lista de algoritmos do simulador não pode ser nula ou vazia.");
        }
        this.algorithms = List.copyOf(algorithms);
    }

    // Roda todos os algoritmos configurados para a mesma cadeia e quantidade de frames
    public List<SimulationResult> run(int[] referenceString, int frameCount) {
        PageReplacementAlgorithm.validateInput(referenceString, frameCount);

        List<SimulationResult> results = new ArrayList<>(algorithms.size());
        for (PageReplacementAlgorithm algorithm : algorithms) {
            results.add(algorithm.run(referenceString, frameCount));
        }

        return results;
    }

    public List<PageReplacementAlgorithm> getAlgorithms() {
        return algorithms;
    }
}
