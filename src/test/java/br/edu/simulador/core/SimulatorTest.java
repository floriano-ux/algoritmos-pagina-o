package br.edu.simulador.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatorTest {

    private Simulator simulator;

    @BeforeEach
    void setUp() {
        simulator = new Simulator();
    }

    @Test
    @DisplayName("Deve inicializar por padrão com os 4 algoritmos clássicos na ordem especificada")
    void shouldInitializeWithDefaultAlgorithmsInOrder() {
        List<PageReplacementAlgorithm> algs = simulator.getAlgorithms();
        assertEquals(4, algs.size());
        assertEquals("FIFO", algs.get(0).name());
        assertEquals("LRU", algs.get(1).name());
        assertEquals("Relógio", algs.get(2).name());
        assertEquals("Ótimo", algs.get(3).name());
    }

    @Test
    @DisplayName("Deve retornar 4 resultados na ordem correta com as faltas esperadas na cadeia clássica")
    void shouldReturnFourResultsWithExpectedFaultsOnClassicReferenceString() {
        int[] referenceString = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1};
        int frameCount = 3;

        List<SimulationResult> results = simulator.run(referenceString, frameCount);

        assertNotNull(results);
        assertEquals(4, results.size());

        // 1. FIFO = 15
        assertEquals("FIFO", results.get(0).algorithmName());
        assertEquals(15, results.get(0).pageFaults());

        // 2. LRU = 12
        assertEquals("LRU", results.get(1).algorithmName());
        assertEquals(12, results.get(1).pageFaults());

        // 3. Relógio = 14
        assertEquals("Relógio", results.get(2).algorithmName());
        assertEquals(14, results.get(2).pageFaults());

        // 4. Ótimo = 9
        assertEquals("Ótimo", results.get(3).algorithmName());
        assertEquals(9, results.get(3).pageFaults());
    }

    @Test
    @DisplayName("Deve permitir construtor customizado e manter ordem dos algoritmos fornecidos")
    void shouldSupportCustomAlgorithmList() {
        Simulator customSimulator = new Simulator(List.of(new LruAlgorithm(), new FifoAlgorithm()));
        assertEquals(2, customSimulator.getAlgorithms().size());

        int[] ref = {1, 2, 3, 1, 4};
        List<SimulationResult> results = customSimulator.run(ref, 3);

        assertEquals(2, results.size());
        assertEquals("LRU", results.get(0).algorithmName());
        assertEquals("FIFO", results.get(1).algorithmName());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para lista de algoritmos nula ou vazia")
    void shouldThrowExceptionForInvalidAlgorithmList() {
        assertThrows(IllegalArgumentException.class, () -> new Simulator(null));
        assertThrows(IllegalArgumentException.class, () -> new Simulator(Collections.emptyList()));
    }

    @Test
    @DisplayName("Deve propagar exceção para cadeia nula ou vazia")
    void shouldPropagateExceptionForNullOrEmptyReferenceString() {
        assertThrows(IllegalArgumentException.class, () -> simulator.run(null, 3));
        assertThrows(IllegalArgumentException.class, () -> simulator.run(new int[]{}, 3));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -3, 10_001, Integer.MAX_VALUE})
    @DisplayName("Deve propagar exceção para frameCount inválido (< 1 ou acima do limite máximo)")
    void shouldPropagateExceptionForInvalidFrameCount(int invalidFrames) {
        int[] ref = {1, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> simulator.run(ref, invalidFrames));
    }

    @Test
    @DisplayName("Deve propagar exceção para referências com número de página negativo")
    void shouldPropagateExceptionForNegativePageNumber() {
        int[] ref = {1, 2, -1, 3};
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> simulator.run(ref, 3));
        assertTrue(ex.getMessage().contains("negativa"));
    }
}
