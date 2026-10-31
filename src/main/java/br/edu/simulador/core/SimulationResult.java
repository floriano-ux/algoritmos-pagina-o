package br.edu.simulador.core;

import java.util.List;

// Resultado final de uma simulacao para um algoritmo especifico
public record SimulationResult(String algorithmName, int pageFaults, List<Step> steps) {

    public SimulationResult {
        steps = steps != null ? List.copyOf(steps) : List.of();
    }
}
