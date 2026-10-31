package br.edu.simulador.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleRunnerTest {

    @Test
    @DisplayName("Deve produzir saída exata via argumentos de linha de comando diretos")
    void shouldProduceExactOutputWithCommandLineArguments() {
        String[] args = {"--cli", "--pages", "7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1", "--frames", "3"};

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);
        PrintStream err = new PrintStream(errContent, true, StandardCharsets.UTF_8);

        ConsoleRunner.run(args, new ByteArrayInputStream(new byte[0]), out, err);

        String output = outContent.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");

        String expected =
                "Método 1 - FIFO - 15 faltas de página\n" +
                "Método 2 - LRU - 12 faltas de página\n" +
                "Método 3 - Relógio - 14 faltas de página\n" +
                "Método 4 - Ótimo - 9 faltas de página\n";

        assertEquals(expected, output);
        assertTrue(errContent.toString().isEmpty());
    }

    @Test
    @DisplayName("Deve funcionar no modo interativo perguntando páginas e frames")
    void shouldWorkInInteractiveMode() {
        String[] args = {"--cli"};
        String simulatedUserInput = "7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1\n3\n";

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);
        PrintStream err = new PrintStream(errContent, true, StandardCharsets.UTF_8);

        ConsoleRunner.run(args, new ByteArrayInputStream(simulatedUserInput.getBytes(StandardCharsets.UTF_8)), out, err);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Método 1 - FIFO - 15 faltas de página"));
        assertTrue(output.contains("Método 2 - LRU - 12 faltas de página"));
        assertTrue(output.contains("Método 3 - Relógio - 14 faltas de página"));
        assertTrue(output.contains("Método 4 - Ótimo - 9 faltas de página"));
    }

    @Test
    @DisplayName("Deve exibir erro claro ao passar argumentos inválidos")
    void shouldDisplayErrorOnInvalidArguments() {
        String[] args = {"--cli", "--pages", "7 0 -1 2", "--frames", "3"};

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);
        PrintStream err = new PrintStream(errContent, true, StandardCharsets.UTF_8);

        ConsoleRunner.run(args, new ByteArrayInputStream(new byte[0]), out, err);

        String errOutput = errContent.toString(StandardCharsets.UTF_8);
        assertTrue(errOutput.contains("Erro de validação"));
    }

    @Test
    @DisplayName("Deve exibir erro de validação para quantidade excessiva de frames sem OutOfMemoryError")
    void shouldRejectExcessiveFramesGracefully() {
        String[] args = {"--cli", "--pages", "1 2 3", "--frames", "2000000000"};

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);
        PrintStream err = new PrintStream(errContent, true, StandardCharsets.UTF_8);

        ConsoleRunner.run(args, new ByteArrayInputStream(new byte[0]), out, err);

        String errOutput = errContent.toString(StandardCharsets.UTF_8);
        assertTrue(errOutput.contains("Erro de validação"));
        assertTrue(errOutput.contains("excede o limite máximo"));
    }
}
