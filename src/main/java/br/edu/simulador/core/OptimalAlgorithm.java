package br.edu.simulador.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptimalAlgorithm implements PageReplacementAlgorithm {

    @Override
    public String name() {
        return "Ótimo";
    }

    @Override
    public SimulationResult run(int[] referenceString, int frameCount) {
        PageReplacementAlgorithm.validateInput(referenceString, frameCount);

        int[] frames = new int[frameCount];
        Arrays.fill(frames, -1);

        List<Step> steps = new ArrayList<>(referenceString.length);
        int pageFaults = 0;

        for (int i = 0; i < referenceString.length; i++) {
            int page = referenceString[i];
            int existingIndex = findPageIndex(frames, page);

            if (existingIndex != -1) {
                steps.add(new Step(page, false, frames, null));
            } else {
                pageFaults++;
                int emptyIndex = findPageIndex(frames, -1);

                if (emptyIndex != -1) {
                    frames[emptyIndex] = page;
                    steps.add(new Step(page, true, frames, null));
                } else {
                    int vitima = acharVitima(frames, referenceString, i + 1);
                    int paginaSubstituida = frames[vitima];

                    frames[vitima] = page;
                    steps.add(new Step(page, true, frames, paginaSubstituida));
                }
            }
        }

        return new SimulationResult(name(), pageFaults, steps);
    }

    // Busca a pagina que vai demorar mais para ser usada no futuro
    private int acharVitima(int[] frames, int[] referenceString, int startIndex) {
        int vitima = 0;
        int maiorDistancia = -1;

        for (int f = 0; f < frames.length; f++) {
            int pagina = frames[f];
            int proximoUso = Integer.MAX_VALUE;

            for (int k = startIndex; k < referenceString.length; k++) {
                if (referenceString[k] == pagina) {
                    proximoUso = k;
                    break;
                }
            }

            // Mantem o menor indice do frame em caso de empate
            if (proximoUso > maiorDistancia) {
                maiorDistancia = proximoUso;
                vitima = f;
            }
        }

        return vitima;
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
