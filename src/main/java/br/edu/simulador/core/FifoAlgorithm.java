package br.edu.simulador.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class FifoAlgorithm implements PageReplacementAlgorithm {

    @Override
    public String name() {
        return "FIFO";
    }

    @Override
    public SimulationResult run(int[] referenceString, int frameCount) {
        PageReplacementAlgorithm.validateInput(referenceString, frameCount);

        int[] frames = new int[frameCount];
        Arrays.fill(frames, -1);

        Queue<Integer> fila = new ArrayDeque<>();
        List<Step> steps = new ArrayList<>(referenceString.length);
        int pageFaults = 0;

        for (int page : referenceString) {
            int existingIndex = findPageIndex(frames, page);

            if (existingIndex != -1) {
                // Pagina ja esta na memoria
                steps.add(new Step(page, false, frames, null));
            } else {
                pageFaults++;
                int emptyIndex = findPageIndex(frames, -1);

                if (emptyIndex != -1) {
                    frames[emptyIndex] = page;
                    fila.add(emptyIndex);
                    steps.add(new Step(page, true, frames, null));
                } else {
                    // Substitui o frame que chegou primeiro
                    int vitima = fila.poll();
                    int paginaSubstituida = frames[vitima];
                    frames[vitima] = page;
                    fila.add(vitima);
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
