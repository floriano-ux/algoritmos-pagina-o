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

class FifoAlgorithmTest {

    private FifoAlgorithm fifo;

    @BeforeEach
    void setUp() {
        fifo = new FifoAlgorithm();
    }

    @Test
    @DisplayName("Deve retornar o nome correto do algoritmo")
    void shouldReturnCorrectName() {
        assertEquals("FIFO", fifo.name());
    }

    @Test
    @DisplayName("Deve produzir exatamente 15 faltas na cadeia clássica com 3 frames")
    void shouldProduce15PageFaultsOnClassicReferenceString() {
        int[] referenceString = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1};
        int frameCount = 3;

        SimulationResult result = fifo.run(referenceString, frameCount);

        assertNotNull(result);
        assertEquals("FIFO", result.algorithmName());
        assertEquals(15, result.pageFaults());
        assertEquals(20, result.steps().size());

        // Validação detalhada dos primeiros passos
        Step step0 = result.steps().get(0); // Página 7
        assertEquals(7, step0.page());
        assertTrue(step0.fault());
        assertArrayEquals(new int[]{7, -1, -1}, step0.framesSnapshot());
        assertNull(step0.evictedPage());

        Step step1 = result.steps().get(1); // Página 0
        assertEquals(0, step1.page());
        assertTrue(step1.fault());
        assertArrayEquals(new int[]{7, 0, -1}, step1.framesSnapshot());
        assertNull(step1.evictedPage());

        Step step2 = result.steps().get(2); // Página 1
        assertEquals(1, step2.page());
        assertTrue(step2.fault());
        assertArrayEquals(new int[]{7, 0, 1}, step2.framesSnapshot());
        assertNull(step2.evictedPage());

        Step step3 = result.steps().get(3); // Página 2 (substitui a 7 no frame 0)
        assertEquals(2, step3.page());
        assertTrue(step3.fault());
        assertArrayEquals(new int[]{2, 0, 1}, step3.framesSnapshot());
        assertEquals(7, step3.evictedPage());

        Step step4 = result.steps().get(4); // Página 0 (hit)
        assertEquals(0, step4.page());
        assertFalse(step4.fault());
        assertArrayEquals(new int[]{2, 0, 1}, step4.framesSnapshot());
        assertNull(step4.evictedPage());
    }

    @Test
    @DisplayName("Deve demonstrar a Anomalia de Belady no FIFO (3 frames = 9 faltas, 4 frames = 10 faltas)")
    void shouldDemonstrateBeladysAnomaly() {
        int[] beladyString = {1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5};

        SimulationResult result3Frames = fifo.run(beladyString, 3);
        SimulationResult result4Frames = fifo.run(beladyString, 4);

        assertEquals(9, result3Frames.pageFaults(), "Com 3 frames devem ocorrer 9 faltas");
        assertEquals(10, result4Frames.pageFaults(), "Com 4 frames devem ocorrer 10 faltas (anomalia de Belady)");
        assertTrue(result4Frames.pageFaults() > result3Frames.pageFaults());
    }

    @Test
    @DisplayName("Quando frames >= número de páginas distintas, as faltas devem ser iguais às páginas distintas")
    void shouldHaveFaultsEqualToDistinctPagesWhenFramesAreSufficient() {
        int[] referenceString = {1, 2, 3, 1, 2, 3, 1, 2};
        // 3 páginas distintas e 5 frames disponíveis
        SimulationResult result = fifo.run(referenceString, 5);

        assertEquals(3, result.pageFaults());
    }

    @Test
    @DisplayName("Cadeia com uma única página repetida deve gerar apenas 1 falta")
    void shouldHaveSingleFaultForRepeatedPage() {
        int[] referenceString = {4, 4, 4, 4, 4, 4};
        SimulationResult result = fifo.run(referenceString, 3);

        assertEquals(1, result.pageFaults());
        for (int i = 1; i < result.steps().size(); i++) {
            assertFalse(result.steps().get(i).fault());
        }
    }

    @Test
    @DisplayName("Deve simular corretamente cadeia alternativa com 9 faltas em 3 frames")
    void shouldSimulateAlternativeReferenceString() {
        // Cadeia: 2 3 2 1 5 2 4 5 3 2 5 2
        // FIFO com 3 frames: 9 faltas de página
        int[] alternativeString = {2, 3, 2, 1, 5, 2, 4, 5, 3, 2, 5, 2};
        SimulationResult result = fifo.run(alternativeString, 3);

        assertEquals(9, result.pageFaults());
        assertEquals(12, result.steps().size());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para cadeia nula ou vazia")
    void shouldThrowExceptionForNullOrEmptyReferenceString() {
        assertThrows(IllegalArgumentException.class, () -> fifo.run(null, 3));
        assertThrows(IllegalArgumentException.class, () -> fifo.run(new int[]{}, 3));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    @DisplayName("Deve lançar IllegalArgumentException para frameCount < 1")
    void shouldThrowExceptionForInvalidFrameCount(int invalidFrames) {
        int[] ref = {1, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> fifo.run(ref, invalidFrames));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando houver página negativa")
    void shouldThrowExceptionForNegativePageNumber() {
        int[] invalidPages = {1, 2, -1, 3};
        assertThrows(IllegalArgumentException.class, () -> fifo.run(invalidPages, 3));
    }
}
