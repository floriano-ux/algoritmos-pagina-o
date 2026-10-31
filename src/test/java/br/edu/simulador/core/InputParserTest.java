package br.edu.simulador.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputParserTest {

    @Test
    @DisplayName("Deve converter entrada com números separados apenas por espaços")
    void shouldParseSpaceSeparatedInput() {
        int[] result = InputParser.parse("7 0 1 2 0 3");
        assertArrayEquals(new int[]{7, 0, 1, 2, 0, 3}, result);
    }

    @Test
    @DisplayName("Deve converter entrada com números separados apenas por vírgulas")
    void shouldParseCommaSeparatedInput() {
        int[] result = InputParser.parse("7,0,1,2,0,3");
        assertArrayEquals(new int[]{7, 0, 1, 2, 0, 3}, result);
    }

    @Test
    @DisplayName("Deve converter entrada com vírgulas e espaços combinados")
    void shouldParseCommaAndSpaceSeparatedInput() {
        int[] result = InputParser.parse("7, 0, 1, 2, 0, 3");
        assertArrayEquals(new int[]{7, 0, 1, 2, 0, 3}, result);
    }

    @Test
    @DisplayName("Deve lidar com múltiplos espaços e vírgulas redundantes")
    void shouldHandleIrregularSpacesAndCommas() {
        int[] result = InputParser.parse("   7  ,  0,,, 1   2 ,,,  0   3   ");
        assertArrayEquals(new int[]{7, 0, 1, 2, 0, 3}, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t\n"})
    @DisplayName("Deve lançar IllegalArgumentException para entradas vazias ou em branco")
    void shouldThrowExceptionForEmptyOrBlankInput(String input) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> InputParser.parse(input));
        assertTrue(ex.getMessage().toLowerCase().contains("vazia") || ex.getMessage().toLowerCase().contains("nula"));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para entrada nula")
    void shouldThrowExceptionForNullInput() {
        assertThrows(IllegalArgumentException.class, () -> InputParser.parse(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"7 a 1 2", "1.5 2 3", "xyz", "7, 0, 1, abc, 3", "٣", "1 ٢ 3", "99999999999"})
    @DisplayName("Deve lançar IllegalArgumentException para valores não inteiros ou fora do padrão ASCII")
    void shouldThrowExceptionForNonIntegerTokens(String input) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> InputParser.parse(input));
        assertTrue(ex.getMessage().toLowerCase().contains("não é um número inteiro válido"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "7 -2 1 3", "0, 1, -5, 2"})
    @DisplayName("Deve lançar IllegalArgumentException para páginas com valor negativo")
    void shouldThrowExceptionForNegativePages(String input) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> InputParser.parse(input));
        assertTrue(ex.getMessage().toLowerCase().contains("negativa") || ex.getMessage().toLowerCase().contains(">= 0"));
    }
}
