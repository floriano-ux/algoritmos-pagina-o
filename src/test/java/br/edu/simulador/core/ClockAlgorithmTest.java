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

class ClockAlgorithmTest {

    private ClockAlgorithm clock;

    @BeforeEach
    void setUp() {
        clock = new ClockAlgorithm();
    }

    @Test
    @DisplayName("Deve retornar o nome correto do algoritmo")
    void shouldReturnCorrectName() {
        assertEquals("Relógio", clock.name());
    }

    /**
     * Traço detalhado passo a passo na cadeia clássica (3 frames):
     * Cadeia: 7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1
     *
     * 1.  p=7: Frame 0 livre. frames=[7,-1,-1], bits=[1,0,0], ptr=1. Falta 1. Evicted: null.
     * 2.  p=0: Frame 1 livre. frames=[7, 0,-1], bits=[1,1,0], ptr=2. Falta 2. Evicted: null.
     * 3.  p=1: Frame 2 livre. frames=[7, 0, 1], bits=[1,1,1], ptr=0. Falta 3. Evicted: null.
     * 4.  p=2: Cheio. ptr=0: bit0(1)->0, ptr=1; bit1(1)->0, ptr=2; bit2(1)->0, ptr=0; bit0(0)->substitui 7 por 2, bit0=1, ptr=1.
     *          frames=[2, 0, 1], bits=[1,0,0], ptr=1. Falta 4. Evicted: 7.
     * 5.  p=0: Hit no frame 1! bit1=1, ptr=1 não se move. frames=[2, 0, 1], bits=[1,1,0], ptr=1. Evicted: null.
     * 6.  p=3: Cheio. ptr=1: bit1(1)->0, ptr=2; bit2(0)->substitui 1 por 3, bit2=1, ptr=0.
     *          frames=[2, 0, 3], bits=[1,0,1], ptr=0. Falta 5. Evicted: 1.
     * 7.  p=0: Hit no frame 1! bit1=1, ptr=0 não se move. frames=[2, 0, 3], bits=[1,1,1], ptr=0. Evicted: null.
     * 8.  p=4: Cheio. ptr=0: bit0(1)->0, ptr=1; bit1(1)->0, ptr=2; bit2(1)->0, ptr=0; bit0(0)->substitui 2 por 4, bit0=1, ptr=1.
     *          frames=[4, 0, 3], bits=[1,0,0], ptr=1. Falta 6. Evicted: 2.
     * 9.  p=2: Cheio. ptr=1: bit1(0)->substitui 0 por 2, bit1=1, ptr=2.
     *          frames=[4, 2, 3], bits=[1,1,0], ptr=2. Falta 7. Evicted: 0.
     * 10. p=3: Hit no frame 2! bit2=1, ptr=2 não se move. frames=[4, 2, 3], bits=[1,1,1], ptr=2. Evicted: null.
     * 11. p=0: Cheio. ptr=2: bit2(1)->0, ptr=0; bit0(1)->0, ptr=1; bit1(1)->0, ptr=2; bit2(0)->substitui 3 por 0, bit2=1, ptr=0.
     *          frames=[4, 2, 0], bits=[0,0,1], ptr=0. Falta 8. Evicted: 3.
     * 12. p=3: Cheio. ptr=0: bit0(0)->substitui 4 por 3, bit0=1, ptr=1.
     *          frames=[3, 2, 0], bits=[1,0,1], ptr=1. Falta 9. Evicted: 4.
     * 13. p=2: Hit no frame 1! bit1=1, ptr=1 não se move. frames=[3, 2, 0], bits=[1,1,1], ptr=1. Evicted: null.
     * 14. p=1: Cheio. ptr=1: bit1(1)->0, ptr=2; bit2(1)->0, ptr=0; bit0(1)->0, ptr=1; bit1(0)->substitui 2 por 1, bit1=1, ptr=2.
     *          frames=[3, 1, 0], bits=[0,1,0], ptr=2. Falta 10. Evicted: 2.
     * 15. p=2: Cheio. ptr=2: bit2(0)->substitui 0 por 2, bit2=1, ptr=0.
     *          frames=[3, 1, 2], bits=[0,1,1], ptr=0. Falta 11. Evicted: 0.
     * 16. p=0: Cheio. ptr=0: bit0(0)->substitui 3 por 0, bit0=1, ptr=1.
     *          frames=[0, 1, 2], bits=[1,1,1], ptr=1. Falta 12. Evicted: 3.
     * 17. p=1: Hit no frame 1! bit1=1, ptr=1 não se move. frames=[0, 1, 2], bits=[1,1,1], ptr=1. Evicted: null.
     * 18. p=7: Cheio. ptr=1: bit1(1)->0, ptr=2; bit2(1)->0, ptr=0; bit0(1)->0, ptr=1; bit1(0)->substitui 1 por 7, bit1=1, ptr=2.
     *          frames=[0, 7, 2], bits=[0,1,0], ptr=2. Falta 13. Evicted: 1.
     * 19. p=0: Hit no frame 0! bit0=1, ptr=2 não se move. frames=[0, 7, 2], bits=[1,1,0], ptr=2. Evicted: null.
     * 20. p=1: Cheio. ptr=2: bit2(0)->substitui 2 por 1, bit2=1, ptr=0.
     *          frames=[0, 7, 1], bits=[1,1,1], ptr=0. Falta 14. Evicted: 2.
     *
     * Total de faltas: 14.
     */
    @Test
    @DisplayName("Deve produzir exatamente 14 faltas na cadeia clássica com 3 frames")
    void shouldProduce14PageFaultsOnClassicReferenceString() {
        int[] referenceString = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1};
        int frameCount = 3;

        SimulationResult result = clock.run(referenceString, frameCount);

        assertNotNull(result);
        assertEquals("Relógio", result.algorithmName());
        assertEquals(14, result.pageFaults());
        assertEquals(20, result.steps().size());

        // Passo 3: página 2 (substitui a 7 no frame 0)
        Step step3 = result.steps().get(3);
        assertTrue(step3.fault());
        assertArrayEquals(new int[]{2, 0, 1}, step3.framesSnapshot());
        assertEquals(7, step3.evictedPage());

        // Passo 4: página 0 (hit)
        Step step4 = result.steps().get(4);
        assertFalse(step4.fault());
        assertNull(step4.evictedPage());

        // Último passo (passo 19): página 1 (substitui 2 no frame 2)
        Step step19 = result.steps().get(19);
        assertTrue(step19.fault());
        assertArrayEquals(new int[]{0, 7, 1}, step19.framesSnapshot());
        assertEquals(2, step19.evictedPage());
    }

    @Test
    @DisplayName("Deve simular corretamente cadeia alternativa com 8 faltas em 3 frames")
    void shouldSimulateAlternativeReferenceString() {
        int[] alternativeString = {2, 3, 2, 1, 5, 2, 4, 5, 3, 2, 5, 2};
        SimulationResult result = clock.run(alternativeString, 3);

        assertEquals(8, result.pageFaults());
        assertEquals(12, result.steps().size());
    }

    @Test
    @DisplayName("Quando frames >= número de páginas distintas, as faltas devem ser iguais às páginas distintas")
    void shouldHaveFaultsEqualToDistinctPagesWhenFramesAreSufficient() {
        int[] referenceString = {1, 2, 3, 1, 2, 3, 1, 2};
        SimulationResult result = clock.run(referenceString, 4);

        assertEquals(3, result.pageFaults());
    }

    @Test
    @DisplayName("Cadeia com uma única página repetida deve gerar apenas 1 falta")
    void shouldHaveSingleFaultForRepeatedPage() {
        int[] referenceString = {8, 8, 8, 8, 8};
        SimulationResult result = clock.run(referenceString, 3);

        assertEquals(1, result.pageFaults());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para cadeia nula ou vazia")
    void shouldThrowExceptionForNullOrEmptyReferenceString() {
        assertThrows(IllegalArgumentException.class, () -> clock.run(null, 3));
        assertThrows(IllegalArgumentException.class, () -> clock.run(new int[]{}, 3));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    @DisplayName("Deve lançar IllegalArgumentException para frameCount < 1")
    void shouldThrowExceptionForInvalidFrameCount(int invalidFrames) {
        int[] ref = {1, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> clock.run(ref, invalidFrames));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando houver página negativa")
    void shouldThrowExceptionForNegativePageNumber() {
        int[] invalidPages = {1, 2, -3, 4};
        assertThrows(IllegalArgumentException.class, () -> clock.run(invalidPages, 3));
    }
}
