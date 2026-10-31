package br.edu.simulador.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClockAlgorithm implements PageReplacementAlgorithm {

    @Override
    public String name() {
        return "Relógio";
    }

    @Override
    public SimulationResult run(int[] referenceString, int frameCount) {
        PageReplacementAlgorithm.validateInput(referenceString, frameCount);

        int[] frames = new int[frameCount];
        Arrays.fill(frames, -1);

        int[] bits = new int[frameCount];
        int ponteiro = 0;

        List<Step> steps = new ArrayList<>(referenceString.length);
        int pageFaults = 0;

        for (int page : referenceString) {
            int existingIndex = findPageIndex(frames, page);

            if (existingIndex != -1) {
                // Hit: marca o bit com 1 e nao move o ponteiro
                bits[existingIndex] = 1;
                steps.add(new Step(page, false, frames, null));
            } else {
                pageFaults++;
                int emptyIndex = findPageIndex(frames, -1);

                if (emptyIndex != -1) {
                    frames[emptyIndex] = page;
                    bits[emptyIndex] = 1;
                    ponteiro = (emptyIndex + 1) % frameCount;
                    steps.add(new Step(page, true, frames, null));
                } else {
                    // Percorre os frames dando segunda chance aos bits 1
                    while (bits[ponteiro] == 1) {
                        bits[ponteiro] = 0;
                        ponteiro = (ponteiro + 1) % frameCount;
                    }

                    int vitima = ponteiro;
                    int paginaSubstituida = frames[vitima];

                    frames[vitima] = page;
                    bits[vitima] = 1;
                    ponteiro = (vitima + 1) % frameCount;

                    steps.add(new Step(page, true, frames, paginaSubstituida));
                }
            }
        }

        return new SimulationResult(name(), pageFaults, steps);
    }

    private int findPageIndex(int[] frames, int target) {
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] == target) {
                return i;
            }
        }
        return -1;
    }
}
