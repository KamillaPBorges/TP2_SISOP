
import java.util.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Escolha o tipo de alocacao: (1) Worst Fit, (2) Buddy System, (3) Circular Fit");
        int tipo = Integer.parseInt(scanner.nextLine());

        System.out.println("Digite o tamanho total da memoria:");
        int tamanho = Integer.parseInt(scanner.nextLine());

        System.out.println("Digite o caminho do arquivo de comandos (ex: comandos.txt):");
        String caminho = scanner.nextLine().trim();

        List<String> comandos = new ArrayList<>();
        try {
            comandos = Files.readAllLines(Paths.get(caminho));
        } catch (Exception e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return;
        }

        if (tipo == 1) {
            MemoriaWorstFit mv = new MemoriaWorstFit(tamanho);
            mv.executarComandos(comandos);
        } else if (tipo == 2) {
            MemoriaBuddy mb = new MemoriaBuddy(tamanho);
            mb.executarComandos(comandos);
        } else if (tipo == 3) {
            MemoriaCircularFit mc = new MemoriaCircularFit(tamanho);
            mc.executarComandos(comandos);
        } else {
            System.out.println("Tipo de alocacao invalido.");
        }
    }
}
