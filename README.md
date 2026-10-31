# Simulador de Algoritmos de Substituição de Páginas

Trabalho prático da disciplina de Sistemas Operacionais para comparar o desempenho dos principais algoritmos de substituição de páginas em memória virtual.

O projeto conta com interface em linha de comando (CLI) e interface gráfica desktop em Swing com gráficos via JFreeChart.

---

## Integrantes
* João Pedro de Almeida Floriano
* Nicolly Feitosa Barroso

---

## Algoritmos Implementados

### 1. FIFO (First-In, First-Out)
Substitui a página que está na memória há mais tempo. Foi implementado usando uma fila para rastrear a ordem em que as páginas entraram nos frames. Permite observar a Anomalia de Belady em determinadas sequências de acesso.

### 2. LRU (Least Recently Used)
Substitui a página que não é utilizada há mais tempo. Usa a estrutura `LinkedHashMap` com ordenação por acesso (`accessOrder = true`), garantindo que o elemento no início da iteração seja sempre o menos recentemente acessado.

### 3. Relógio (Segunda Chance)
Aproximação circular do LRU. Cada frame possui um bit de referência inicializado em 1 quando uma página entra.
* Em caso de acerto (hit), o bit da página vai para 1 e o ponteiro não se move.
* Em caso de falta com memória cheia, o algoritmo percorre os frames a partir do ponteiro: se o bit for 1, ele recebe 0 (segunda chance) e o ponteiro avança; se for 0, a página daquele frame é substituída, o bit vira 1 e o ponteiro passa para a posição seguinte.

### 4. Ótimo (Optimal / MIN)
Algoritmo teórico que olha para o futuro da cadeia de referências e substitui a página que vai demorar mais tempo para ser usada novamente (ou que nunca mais será chamada).
* Critério de desempate: se houver mais de uma página que nunca mais será referenciada, é escolhida a primeira em ordem física de quadros (menor índice de frame).

---

## Estrutura do Projeto

```
simulador-paginas/
├── pom.xml
├── README.md
├── .gitignore
└── src/
    ├── main/
    │   ├── java/br/edu/simulador/
    │   │   ├── Main.java                      # Roteador CLI / GUI
    │   │   ├── core/                          # Núcleo da simulação
    │   │   │   ├── PageReplacementAlgorithm.java
    │   │   │   ├── FifoAlgorithm.java
    │   │   │   ├── LruAlgorithm.java
    │   │   │   ├── ClockAlgorithm.java
    │   │   │   ├── OptimalAlgorithm.java
    │   │   │   ├── SimulationResult.java
    │   │   │   ├── Step.java
    │   │   │   ├── Simulator.java
    │   │   │   └── InputParser.java
    │   │   ├── cli/
    │   │   │   └── ConsoleRunner.java          # Modo terminal
    │   │   └── gui/
    │   │       ├── MainWindow.java             # Janela Swing
    │   │       ├── InputPanel.java
    │   │       ├── ResultsPanel.java
    │   │       ├── ResultsChart.java
    │   │       └── StepTableModel.java
    │   └── resources/
    └── test/
        └── java/br/edu/simulador/
            ├── cli/
            │   └── ConsoleRunnerTest.java
            ├── core/
            │   ├── FifoAlgorithmTest.java
            │   ├── LruAlgorithmTest.java
            │   ├── ClockAlgorithmTest.java
            │   ├── OptimalAlgorithmTest.java
            │   ├── SimulatorTest.java
            │   └── InputParserTest.java
            └── gui/
                └── StepTableModelTest.java
```

---

## Requisitos
* Java JDK 21 ou superior
* Maven 3.8+ (ou o wrapper `./mvnw` / `.\mvnw.cmd` incluído no projeto)

---

## Compilação e Empacotamento

Para compilar o código, rodar os testes e gerar o arquivo JAR executável:

```bash
mvn clean package
```

No Windows usando o wrapper:
```powershell
.\mvnw.cmd clean package
```

O arquivo executável é gerado em:
```
target/simulador-paginas-1.0.jar
```

---

## Testes Automatizados

Para rodar os testes unitários (JUnit 5):

```bash
mvn test
```
ou
```powershell
.\mvnw.cmd test
```

Os testes cobrem:
* Contagem exata de faltas na cadeia clássica (FIFO=15, LRU=12, Relógio=14, Ótimo=9);
* Anomalia de Belady no FIFO (3 quadros = 9 faltas, 4 quadros = 10 faltas);
* Garantia de que o algoritmo Ótimo nunca gera mais faltas que os outros;
* Validações de entradas inválidas, vazias e números negativos.

---

## Como Executar

### 1. Interface Gráfica (Desktop)
Executando sem parâmetros, o programa abre a janela principal com o exemplo clássico carregado:

```bash
java -jar target/simulador-paginas-1.0.jar
```

Recursos da tela:
* Campo para editar a cadeia de páginas e o seletor de quantidade de frames (de 1 a 10);
* Tabela comparativa com taxa percentual e destaque para o menor número de faltas;
* Gráfico de barras comparativo gerado via JFreeChart;
* Abas com o histórico passo a passo da memória (verde para acertos e vermelho para faltas de página);
* Botão para exportar o gráfico como imagem PNG.

---

### 2. Linha de Comando (CLI)

#### Passando os parâmetros diretamente:
```bash
java -jar target/simulador-paginas-1.0.jar --cli --pages "7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1" --frames 3
```

#### Modo interativo:
Ao passar apenas `--cli`, o programa solicita os dados no terminal:
```bash
java -jar target/simulador-paginas-1.0.jar --cli
```

Exemplo:
```
Digite a cadeia de páginas (ex.: 7 0 1 2 0 3): 7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1
Digite o número de frames [padrão 3]: 3
```

Formato da saída:
```text
Método 1 - FIFO - 15 faltas de página
Método 2 - LRU - 12 faltas de página
Método 3 - Relógio - 14 faltas de página
Método 4 - Ótimo - 9 faltas de página
```

---

## Resultados na Cadeia Clássica (3 Frames)

Cadeia: `7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1` (20 referências)

| Algoritmo | Faltas de Página | Taxa de Falta (%) | Observações |
| :--- | :---: | :---: | :--- |
| **Ótimo** | 9 | 45.0% | Menor número possível de faltas teóricas |
| **LRU** | 12 | 60.0% | Bom aproveitamento da localidade temporal recente |
| **Relógio** | 14 | 70.0% | Custo de implementação baixo com desempenho próximo ao LRU |
| **FIFO** | 15 | 75.0% | Mais faltas nessa sequência; suscetível à anomalia de Belady |
