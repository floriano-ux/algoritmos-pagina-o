package br.edu.simulador.core;

// Guarda o snapshot de cada passo da simulacao
public record Step(int page, boolean fault, int[] framesSnapshot, Integer evictedPage) {

    public Step {
        if (framesSnapshot != null) {
            framesSnapshot = framesSnapshot.clone();
        }
    }

    @Override
    public int[] framesSnapshot() {
        return framesSnapshot != null ? framesSnapshot.clone() : null;
    }
}
