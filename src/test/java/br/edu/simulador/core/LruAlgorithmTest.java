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

class LruAlgorithmTest {

    private LruAlgorithm lru;

    @BeforeEach
    void setUp() {
        lru = new LruAlgorithm();
    }

    @Test
    @DisplayName("Deve retornar o nome correto do algoritmo")
    void shouldReturnCorrectName() {
        assertEquals("LRU", lru.name());
    }

    @Test
    @DisplayName("Deve produzir exatamente 12 faltas na cadeia clássica com 3 frames")
    void shouldProduce12PageFaultsOnClassicReferenceString() {
        int[] referenceString = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1};
        int frameCount = 3;

        SimulationResult result = lru.run(referenceString, frameCount);

        assertNotNull(result);
        assertEquals("LRU", result.algorithmName());
        assertEquals(12, result.pageFaults());
        assertEquals(20, result.steps().size());

        // Verificação de passos específicos
        // Passo 0: página 7
        Step step0 = result.steps().get(0);
        assertTrue(step0.fault());
        assertArrayEquals(new int[]{7, -1, -1}, step0.framesSnapshot());
        assertNull(step0.evictedPage());

        // Passo 1: página 0
        Step step1 = result.steps().get(1);
        assertTrue(step1.fault());
        assertArrayEquals(new int[]{7, 0, -1}, step1.framesSnapshot());

        // Passo 2: página 1
        Step step2 = result.steps().get(2);
        assertTrue(step2.fault());
        assertArrayEquals(new int[]{7, 0, 1}, step2.framesSnapshot());

        // Passo 3: página 2 -> LRU é 7 (frame 0)
        Step step3 = result.steps().get(3);
        assertTrue(step3.fault());
        assertArrayEquals(new int[]{2, 0, 1}, step3.framesSnapshot());
        assertEquals(7, step3.evictedPage());

        // Passo 4: página 0 -> hit! (ordem de uso recente passa a ser: 1, 2, 0)
        Step step4 = result.steps().get(4);
        assertFalse(step4.fault());
        assertArrayEquals(new int[]{2, 0, 1}, step4.framesSnapshot());
        assertNull(step4.evictedPage());

        // Passo 5: página 3 -> substitui 1 (frame 2), pois 1 é o LRU
        Step step5 = result.steps().get(5);
        assertTrue(step5.fault());
        assertArrayEquals(new int[]{2, 0, 3}, step5.framesSnapshot());
        assertEquals(1, step5.evictedPage());
    }

    @Test
    @DisplayName("Deve substituir corretamente a página menos recentemente usada em sequência curta")
    void shouldEvictLeastRecentlyUsedPage() {
        // Sequência: 1, 2, 3 (enche a memória), depois 1 (hit!), depois 4 (deve substituir 2, pois 1 foi usado mais recentemente)
        int[] ref = {1, 2, 3, 1, 4};
        SimulationResult result = lru.run(ref, 3);

        assertEquals(4, result.pageFaults());
        Step step4 = result.steps().get(4); // Página 4
        assertTrue(step4.fault());
        assertEquals(2, step4.evictedPage());
        assertArrayEquals(new int[]{1, 4, 3}, step4.framesSnapshot());
    }

    @Test
    @DisplayName("Quando frames >= número de páginas distintas, as faltas devem ser iguais às páginas distintas")
    void shouldHaveFaultsEqualToDistinctPagesWhenFramesAreSufficient() {
        int[] referenceString = {1, 2, 3, 1, 2, 3, 1, 2};
        SimulationResult result = lru.run(referenceString, 5);

        assertEquals(3, result.pageFaults());
    }

    @Test
    @DisplayName("Cadeia com uma única página repetida deve gerar apenas 1 falta")
    void shouldHaveSingleFaultForRepeatedPage() {
        int[] referenceString = {9, 9, 9, 9, 9};
        SimulationResult result = lru.run(referenceString, 3);

        assertEquals(1, result.pageFaults());
    }

    @Test
    @DisplayName("Deve simular corretamente cadeia alternativa com 7 faltas em 3 frames")
    void shouldSimulateAlternativeReferenceString() {
        // Cadeia: 2 3 2 1 5 2 4 5 3 2 5 2
        // LRU com 3 frames: 7 faltas de página
        int[] alternativeString = {2, 3, 2, 1, 5, 2, 4, 5, 3, 2, 5, 2};
        SimulationResult result = lru.run(alternativeString, 3);

        assertEquals(7, result.pageFaults());
        assertEquals(12, result.steps().size());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para cadeia nula ou vazia")
    void shouldThrowExceptionForNullOrEmptyReferenceString() {
        assertThrows(IllegalArgumentException.class, () -> lru.run(null, 3));
        assertThrows(IllegalArgumentException.class, () -> lru.run(new int[]{}, 3));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    @DisplayName("Deve lançar IllegalArgumentException para frameCount < 1")
    void shouldThrowExceptionForInvalidFrameCount(int invalidFrames) {
        int[] ref = {1, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> lru.run(ref, invalidFrames));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando houver página negativa")
    void shouldThrowExceptionForNegativePageNumber() {
        int[] invalidPages = {1, 2, -10, 3};
        assertThrows(IllegalArgumentException.class, () -> lru.run(invalidPages, 3));
    }
}
