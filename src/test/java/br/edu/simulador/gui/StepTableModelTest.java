package br.edu.simulador.gui;

import br.edu.simulador.core.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StepTableModelTest {

    @Test
    @DisplayName("Deve configurar corretamente colunas e linhas para simulação com 3 frames")
    void shouldConfigureColumnsAndRowsFor3Frames() {
        List<Step> steps = List.of(
                new Step(7, true, new int[]{7, -1, -1}, null),
                new Step(0, true, new int[]{7, 0, -1}, null),
                new Step(0, false, new int[]{7, 0, -1}, null),
                new Step(2, true, new int[]{2, 0, -1}, 7)
        );

        StepTableModel model = new StepTableModel(steps, 3);

        // Colunas: Passo (0), Página (1), Falta? (2), Frame 0 (3), Frame 1 (4), Frame 2 (5), Página Substituída (6)
        assertEquals(7, model.getColumnCount());
        assertEquals("Passo", model.getColumnName(0));
        assertEquals("Página", model.getColumnName(1));
        assertEquals("Falta?", model.getColumnName(2));
        assertEquals("Frame 0", model.getColumnName(3));
        assertEquals("Frame 1", model.getColumnName(4));
        assertEquals("Frame 2", model.getColumnName(5));
        assertEquals("Página Substituída", model.getColumnName(6));

        assertEquals(4, model.getRowCount());

        // Linha 0: Passo 1, Página 7, Falta SIM, 7, -, -, -
        assertEquals(1, model.getValueAt(0, 0));
        assertEquals(7, model.getValueAt(0, 1));
        assertEquals("SIM", model.getValueAt(0, 2));
        assertEquals("7", model.getValueAt(0, 3));
        assertEquals("-", model.getValueAt(0, 4));
        assertEquals("-", model.getValueAt(0, 5));
        assertEquals("-", model.getValueAt(0, 6));
        assertTrue(model.isFault(0));

        // Linha 2: Passo 3, Página 0, Falta NÃO (Hit)
        assertEquals(3, model.getValueAt(2, 0));
        assertEquals(0, model.getValueAt(2, 1));
        assertEquals("NÃO", model.getValueAt(2, 2));
        assertFalse(model.isFault(2));

        // Linha 3: Passo 4, Página 2, Falta SIM, Substituída: 7
        assertEquals("7", model.getValueAt(3, 6));
        assertTrue(model.isFault(3));
    }
}
