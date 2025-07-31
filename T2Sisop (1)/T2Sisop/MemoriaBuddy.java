import java.util.*;

// Classe que representa um nó da árvore do Buddy System
class No {
    int inicio, tamanho;         // Início do bloco na memória e tamanho
    String processo;             // Nome do processo alocado (null se livre)
    boolean livre;               // Se o bloco está livre ou não
    No esquerdo, direito;        // Referências para os blocos filhos (caso o bloco tenha sido dividido)
    String nome;                 // Nome do nó (ex: "ROOT", "L", "R") para visualização

    // Construtor do nó
    No(int inicio, int tamanho, String nome) {
        this.inicio = inicio;
        this.tamanho = tamanho;
        this.livre = true;       // Inicialmente o nó é livre
        this.nome = nome;
    }
}

class MemoriaBuddy {
    No raiz;                            // Raiz da árvore, representa toda a memória
    private int fragmentacaoInterna = 0; // Total acumulado de fragmentação interna

    // Inicializa a memória com um único bloco (raiz) do tamanho total
    public MemoriaBuddy(int tamanho) {
        raiz = new No(0, tamanho, "ROOT");
    }

    // Executa os comandos recebidos (IN ou OUT)
    public void executarComandos(List<String> comandos) {
        for (String linha : comandos) {
            linha = linha.replaceAll("\\s+", ""); // Remove espaços em branco

            System.out.println("Comando: " + linha);

            if (linha.startsWith("IN")) {
                // Comando de alocação: IN(nome, tamanho)
                String id = linha.substring(3, linha.indexOf(","));
                int tam = Integer.parseInt(linha.substring(linha.indexOf(",") + 1, linha.indexOf(")")));

                // Tenta alocar memória para o processo
                if (!alocar(raiz, id, tam)) {
                    System.out.println("ESPACO INSUFICIENTE");
                }

            } else if (linha.startsWith("OUT")) {
                // Comando de liberação: OUT(nome)
                String id = linha.substring(4, linha.indexOf(")"));
                liberar(raiz, id);       // Libera o processo
                coalescar(raiz);         // Tenta fundir blocos vizinhos
            }

            // Mostra o estado da memória após cada comando
            System.out.println("Memoria Buddy Atual:");
            exibir(raiz, "");
        }

        // Ao final de todos os comandos, exibe a fragmentação interna total
        System.out.println("Fragmentacao Interna Total: " + fragmentacaoInterna + " unidades.");
    }

    // Função recursiva que tenta alocar um processo no nó atual
    private boolean alocar(No no, String id, int tam) {
        // Se o nó não está livre ou já possui processo, não pode alocar
        if (!no.livre || no.processo != null)
            return false;

        int pot2 = menorPotencia2(tam); // Tamanho ideal em potência de 2 para esse processo

        // Se o nó tem exatamente o tamanho necessário, aloca aqui
        if (no.tamanho == pot2) {
            no.processo = id;
            no.livre = false;
            fragmentacaoInterna += (pot2 - tam); // Armazena sobra como fragmentação
            return true;
        }

        // Se o nó é menor que o necessário, não pode alocar
        if (no.tamanho < pot2)
            return false;

        // Se o nó é maior e ainda não foi dividido, divide-o ao meio
        if (no.esquerdo == null && no.direito == null) {
            int metade = no.tamanho / 2;
            no.esquerdo = new No(no.inicio, metade, "L");
            no.direito = new No(no.inicio + metade, metade, "R");
        }

        // Tenta alocar primeiro na esquerda, depois na direita
        return alocar(no.esquerdo, id, tam) || alocar(no.direito, id, tam);
    }

    // Função recursiva que libera um processo da árvore
    private void liberar(No no, String id) {
        if (no == null) return;

        // Se encontrou o processo, libera-o
        if (id.equals(no.processo)) {
            no.processo = null;
            no.livre = true;
            return;
        }

        // Continua buscando nas subárvores
        liberar(no.esquerdo, id);
        liberar(no.direito, id);
    }

    // Função que tenta fundir blocos filhos livres em um único bloco pai
    private void coalescar(No no) {
        // Se não há filhos, não há o que coalescer
        if (no == null || no.esquerdo == null || no.direito == null)
            return;

        // Coalesce filhos recursivamente
        coalescar(no.esquerdo);
        coalescar(no.direito);

        // Se os dois filhos estão livres e sem processo, funde-os
        if (no.esquerdo.livre && no.direito.livre &&
            no.esquerdo.processo == null && no.direito.processo == null) {
            no.esquerdo = null;
            no.direito = null;
            no.livre = true;
        }
    }

    // Exibe o estado da memória em forma de árvore indentada
    private void exibir(No no, String prefixo) {
        if (no == null) return;

        // Exibe o nó atual
        System.out.println(prefixo + no.nome + " -> [" + (no.processo == null ? "LIVRE" : no.processo) + ", T=" + no.tamanho + "]");

        // Exibe recursivamente os filhos
        exibir(no.esquerdo, prefixo + "  ");
        exibir(no.direito, prefixo + "  ");
    }

    // Calcula a menor potência de 2 maior ou igual ao valor fornecido
    private int menorPotencia2(int n) {
        int pot = 1;
        while (pot < n) pot *= 2; //multiplicando por 2
        return pot;
    }
}

