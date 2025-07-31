import java.util.*;


class Bloco {
    int inicio, tamanho;
    String processo;

    
    Bloco(int inicio, int tamanho, String processo) {
        this.inicio = inicio;       // Posição de início na memória
        this.tamanho = tamanho;     // Tamanho do bloco
        this.processo = processo;   // Nome do processo ou null (se estiver livre)
    }
}

// Classe que implementa o algoritmo de alocação Worst Fit
class MemoriaWorstFit {
    int tamanhoTotal;              // Tamanho total da memória
    List<Bloco> memoria;           // Lista que representa os blocos da memória

    // Construtor: inicializa a memória com um único bloco livre
    public MemoriaWorstFit(int tamanho) {
        this.tamanhoTotal = tamanho;
        memoria = new ArrayList<>();
        memoria.add(new Bloco(0, tamanho, null)); // Memória começa toda livre
        exibirEstado(); // Mostra o estado inicial
    }

    // Executa comandos de alocação (IN) e liberação (OUT)
    public void executarComandos(List<String> comandos) {
        for (String linha : comandos) {
            linha = linha.replaceAll("\\s+", ""); // Remove espaços

            if (linha.startsWith("IN")) {
                // Comando IN(id, tamanho): alocar processo
                String id = linha.substring(3, linha.indexOf(","));
                int tam = Integer.parseInt(linha.substring(linha.indexOf(",") + 1, linha.indexOf(")")));
                alocar(id, tam);
            } else if (linha.startsWith("OUT")) {
                // Comando OUT(id): liberar processo
                String id = linha.substring(4, linha.indexOf(")"));
                liberar(id);
            }

            exibirEstado(); // Mostra estado da memória após o comando
        }
    }

    // Aloca um processo usando a estratégia Worst Fit (maior bloco livre)
    private void alocar(String id, int tam) {
        Bloco pior = null;

        // Busca o pior bloco (maior livre que comporte o processo)
        for (Bloco b : memoria) { //ve em todos os blocos, acha o pior
            if (b.processo == null && b.tamanho >= tam) {
                if (pior == null || b.tamanho > pior.tamanho) {
                    pior = b;
                }
            }
        }

        if (pior == null) {
            // Nenhum bloco suficiente encontrado
            System.out.println("ESPACO INSUFICIENTE");
            return;
        }

        int sobra = pior.tamanho - tam;
        int inicioNovo = pior.inicio + tam;

        memoria.remove(pior); // Remove o bloco original (livre)

        // Adiciona o novo bloco alocado
        memoria.add(new Bloco(pior.inicio, tam, id));

        // Se sobrou espaço, cria um novo bloco livre com o restante
        if (sobra > 0) {
            memoria.add(new Bloco(inicioNovo, sobra, null));
        }

        ordenar(); // Reorganiza os blocos por ordem de início
    }

    // Libera todos os blocos pertencentes a um processo
    private void liberar(String id) {
        for (Bloco b : memoria) {
            if (id.equals(b.processo)) {
                b.processo = null; // Marca o bloco como livre
            }
        }
        fundirLivres(); // Junta blocos livres consecutivos
    }

    // Junta blocos livres consecutivos para evitar fragmentação externa
    private void fundirLivres() {
        ordenar(); // Garante que os blocos estão ordenados

        List<Bloco> nova = new ArrayList<>();

        for (int i = 0; i < memoria.size(); i++) {
            Bloco atual = memoria.get(i);

            if (atual.processo != null) {
                // Bloco ocupado: adiciona diretamente
                nova.add(atual);
                continue;
            }

            // Junta todos os blocos livres consecutivos
            while (i + 1 < memoria.size() && memoria.get(i + 1).processo == null) {
                atual.tamanho += memoria.get(i + 1).tamanho;
                i++;
            }

            nova.add(atual); // Adiciona o bloco fundido
        }

        memoria = nova; // Atualiza a lista de blocos
    }

    // Ordena os blocos com base na posição de início (necessário para fusão correta)
    private void ordenar() {
        memoria.sort(Comparator.comparingInt(b -> b.inicio));
    }

    // Exibe o estado atual da memória (somente blocos livres)
    private void exibirEstado() {
        StringBuilder sb = new StringBuilder();
        sb.append("| ");
        for (Bloco b : memoria) {
            if (b.processo == null) {
                sb.append(b.tamanho).append(" | ");
            }
        }
        System.out.println(sb.toString().trim());
    }
}
