import java.util.*;


class MemoriaCircularFit {
    int tamanhoTotal;              // Tamanho total da memória
    List<Bloco> memoria;           // Lista de blocos da memória
    int ultimaPosicao = 0;         // Posição onde terminou a última alocação

    // Construtor: inicializa a memória com um único bloco livre
    public MemoriaCircularFit(int tamanho) {
        this.tamanhoTotal = tamanho;
        memoria = new ArrayList<>();
        memoria.add(new Bloco(0, tamanho, null)); // bloco único livre
        exibirEstado(); // Exibe o estado inicial
    }

    // Executa a lista de comandos (IN e OUT)
    public void executarComandos(List<String> comandos) {
        for (String linha : comandos) {
            linha = linha.replaceAll("\\s+", ""); // remove espaços

            if (linha.startsWith("IN")) {
                // Extrai id e tamanho do processo
                String id = linha.substring(3, linha.indexOf(","));
                int tam = Integer.parseInt(linha.substring(linha.indexOf(",") + 1, linha.indexOf(")")));
                alocar(id, tam); // aloca o processo
            } else if (linha.startsWith("OUT")) {
                // Extrai id do processo a ser removido
                String id = linha.substring(4, linha.indexOf(")"));
                liberar(id); // libera o processo
            }

            exibirEstado(); // mostra estado da memória após cada comando
        }
    }

    // Realiza a alocação 
    private void alocar(String id, int tam) {
        int n = memoria.size();
        int count = 0;
        int i = ultimaPosicao; // começa da última posição usada

        // Percorre os blocos até dar uma volta completa
        while (count < n) {
            if (i >= memoria.size()) i = 0;  // ajuste caso a lista aumente

            Bloco b = memoria.get(i);
            if (b.processo == null && b.tamanho >= tam) {
                if (b.tamanho > tam) {
                    // Se o bloco for maior que o necessário, divide
                    Bloco novoLivre = new Bloco(b.inicio + tam, b.tamanho - tam, null);
                    b.tamanho = tam;
                    b.processo = id;

                    // Insere o bloco restante como livre após o alocado
                    if (i + 1 >= memoria.size()) {
                        memoria.add(novoLivre);
                    } else {
                        memoria.add(i + 1, novoLivre);
                    }
                } else {
                    // Bloco tem tamanho exato
                    b.processo = id;
                }

                ultimaPosicao = i; // atualiza o ponto da última alocação
                return;
            }

            // Próximo bloco (circular)
            i = (i + 1) % memoria.size();
            count++;
        }

        // Se nenhum bloco livre suficiente foi encontrado
        System.out.println("ESPACO INSUFICIENTE");
    }

    // Libera a memória ocupada por um processo e tenta fundir blocos livres
    private void liberar(String id) {
        for (int i = 0; i < memoria.size(); i++) {
            Bloco b = memoria.get(i);
            if (id.equals(b.processo)) {
                b.processo = null; // marca como livre

                // Tenta fundir com o bloco anterior, se também estiver livre
                if (i > 0 && memoria.get(i - 1).processo == null) {
                    Bloco anterior = memoria.get(i - 1);
                    anterior.tamanho += b.tamanho;
                    memoria.remove(i);
                    i--;
                    b = anterior;
                }

                // Tenta fundir com o bloco seguinte, se também estiver livre
                if (i < memoria.size() - 1 && memoria.get(i + 1).processo == null) {
                    Bloco proximo = memoria.get(i + 1);
                    b.tamanho += proximo.tamanho;
                    memoria.remove(i + 1);
                }

                break; // processo encontrado e tratado
            }
        }
    }

    // Exibe os blocos livres da memória de forma visual
    private void exibirEstado() {
        StringBuilder sb = new StringBuilder();
        sb.append("| ");
        for (Bloco b : memoria) {
            if (b.processo == null) {
                sb.append(b.tamanho).append(" | "); // mostra apenas os livres
            }
        }
        System.out.println(sb.toString().trim());
    }
}
