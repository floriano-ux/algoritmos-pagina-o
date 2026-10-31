package br.edu.simulador.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptimalAlgorithmTest {

    private OptimalAlgorithm optimal;

    @BeforeEach
    void setUp() {
        optimal = new OptimalAlgorithm();
    }

    @Test
    @DisplayName("Deve retornar o nome correto do algoritmo")
    void shouldReturnCorrectName() {
        assertEquals("Ótimo", optimal.name());
    }

    @Test
    @DisplayName("Deve produzir exatamente 9 faltas na cadeia clássica com 3 frames")
    void shouldProduce9PageFaultsOnClassicReferenceString() {
        int[] referenceString = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1};
        int frameCount = 3;

        SimulationResult result = optimal.run(referenceString, frameCount);

        assertNotNull(result);
        assertEquals("Ótimo", result.algorithmName());
        assertEquals(9, result.pageFaults());
        assertEquals(20, result.steps().size());

        // Passo 3: página 2 substitui a 7 (que só reaparece no passo 17)
        Step step3 = result.steps().get(3);
        assertTrue(step3.fault());
        assertArrayEquals(new int[]{2, 0, 1}, step3.framesSnapshot());
        assertEquals(7, step3.evictedPage());

        // Passo 5: página 3 substitui a 1 (que só reaparece no passo 13)
        Step step5 = result.steps().get(5);
        assertTrue(step5.fault());
        assertArrayEquals(new int[]{2, 0, 3}, step5.framesSnapshot());
        assertEquals(1, step5.evictedPage());

        // Passo 7: página 4 substitui a 0 (que só reaparece no passo 10)
        Step step7 = result.steps().get(7);
        assertTrue(step7.fault());
        assertArrayEquals(new int[]{2, 4, 3}, step7.framesSnapshot());
        assertEquals(0, step7.evictedPage());

        // Passo 10: página 0 substitui a 4 (que nunca mais é usada)
        Step step10 = result.steps().get(10);
        assertTrue(step10.fault());
        assertArrayEquals(new int[]{2, 0, 3}, step10.framesSnapshot());
        assertEquals(4, step10.evictedPage());
    }

    @Test
    @DisplayName("Deve simular corretamente cadeia alternativa com 6 faltas em 3 frames")
    void shouldSimulateAlternativeReferenceString() {
        // Cadeia: 2 3 2 1 5 2 4 5 3 2 5 2
        int[] alternativeString = {2, 3, 2, 1, 5, 2, 4, 5, 3, 2, 5, 2};
        SimulationResult result = optimal.run(alternativeString, 3);

        assertEquals(6, result.pageFaults());
        assertEquals(12, result.steps().size());
    }

    @Test
    @DisplayName("Em caso de empate com páginas nunca mais usadas, deve escolher a primeira em ordem de frames")
    void shouldDeterministicallyBreakTiesByLowestFrameIndex() {
        // Carrega 10, 20, 30 nos frames 0, 1, 2. Depois chega 40.
        // Nenhuma página (10, 20, 30) será usada no futuro.
        // O algoritmo deve substituir a página do frame 0 (página 10).
        int[] ref = {10, 20, 30, 40};
        SimulationResult result = optimal.run(ref, 3);

        assertEquals(4, result.pageFaults());
        Step step3 = result.steps().get(3);
        assertEquals(40, step3.page());
        assertEquals(10, step3.evictedPage(), "Deve substituir a página 10 do frame 0 (menor índice)");
        assertArrayEquals(new int[]{40, 20, 30}, step3.framesSnapshot());
    }

    @Test
    @DisplayName("Ótimo nunca deve produzir mais faltas de página que os demais algoritmos")
    void optimalShouldNeverHaveMoreFaultsThanOthers() {
        FifoAlgorithm fifo = new FifoAlgorithm();
        LruAlgorithm lru = new LruAlgorithm();
        ClockAlgorithm clock = new ClockAlgorithm();

        int[][] sampleStrings = {
                {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1},
                {2, 3, 2, 1, 5, 2, 4, 5, 3, 2, 5, 2},
                {1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5},
                {0, 1, 2, 3, 0, 1, 4, 0, 1, 2, 3, 4}
        };

        for (int[] ref : sampleStrings) {
            for (int frames = 3; frames <= 5; frames++) {
                int optFaults = optimal.run(ref, frames).pageFaults();
                int fifoFaults = fifo.run(ref, frames).pageFaults();
                int lruFaults = lru.run(ref, frames).pageFaults();
                int clockFaults = clock.run(ref, frames).pageFaults();

                assertTrue(optFaults <= fifoFaults, "Ótimo (" + optFaults + ") deve ser <= FIFO (" + fifoFaults + ")");
                assertTrue(optFaults <= lruFaults, "Ótimo (" + optFaults + ") deve ser <= LRU (" + lruFaults + ")");
                assertTrue(optFaults <= clockFaults, "Ótimo (" + optFaults + ") deve ser <= Relógio (" + clockFaults + ")");
            }
        }
    }

    @Test
    @DisplayName("Quando frames >= número de páginas distintas, as faltas devem ser iguais às páginas distintas")
    void shouldHaveFaultsEqualToDistinctPagesWhenFramesAreSufficient() {
        int[] referenceString = {1, 2, 3, 1, 2, 3, 1, 2};
        SimulationResult result = optimal.run(referenceString, 3);

        assertEquals(3, result.pageFaults());
    }

    @Test
    @DisplayName("Cadeia com uma única página repetida deve gerar apenas 1 falta")
    void shouldHaveSingleFaultForRepeatedPage() {
        int[] referenceString = {7, 7, 7, 7, 7};
        SimulationResult result = optimal.run(referenceString, 3);

        assertEquals(1, result.pageFaults());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para cadeia nula ou vazia")
    void shouldThrowExceptionForNullOrEmptyReferenceString() {
        assertThrows(IllegalArgumentException.class, () -> optimal.run(null, 3));
        assertThrows(IllegalArgumentException.class, () -> optimal.run(new int[]{}, 3));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    @DisplayName("Deve lançar IllegalArgumentException para frameCount < 1")
    void shouldThrowExceptionForInvalidFrameCount(int invalidFrames) {
        int[] ref = {1, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> optimal.run(ref, invalidFrames));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando houver página negativa")
    void shouldThrowExceptionForNegativePageNumber() {
        int[] invalidPages = {1, 2, -7, 4};
        assertThrows(IllegalArgumentException.class, () -> optimal.run(invalidPages, 3));
    }
}
