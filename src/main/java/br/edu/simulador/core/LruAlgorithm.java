package br.edu.simulador.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class LruAlgorithm implements PageReplacementAlgorithm {

    @Override
    public String name() {
        return "LRU";
    }

    @Override
    public SimulationResult run(int[] referenceString, int frameCount) {
        PageReplacementAlgorithm.validateInput(referenceString, frameCount);

        int[] frames = new int[frameCount];
        Arrays.fill(frames, -1);

        // LinkedHashMap com access-order para manter o menos recentemente usado no inicio
        LinkedHashMap<Integer, Integer> mapa = new LinkedHashMap<>(frameCount, 0.75f, true);
        List<Step> steps = new ArrayList<>(referenceString.length);
        int pageFaults = 0;

        for (int page : referenceString) {
            if (mapa.containsKey(page)) {
                // Atualiza a ordem de uso da pagina
                mapa.get(page);
                steps.add(new Step(page, false, frames, null));
            } else {
                pageFaults++;
                int emptyIndex = findPageIndex(frames, -1);

                if (emptyIndex != -1) {
                    frames[emptyIndex] = page;
                    mapa.put(page, emptyIndex);
                    steps.add(new Step(page, true, frames, null));
                } else {
                    // A primeira chave do iterador e a menos recente
                    int lruPage = mapa.keySet().iterator().next();
                    int vitima = mapa.remove(lruPage);

                    frames[vitima] = page;
                    mapa.put(page, vitima);
                    steps.add(new Step(page, true, frames, lruPage));
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
