package lothon;

import static lothon.util.Eve.*;

import lothon.process.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Main {

    /*
     * Pode ser definido para cada loteria ou de maneira generica:
     */
    private static final int MIN_THRESHOLD = 10;

    private static void printOpcoes(String msgErro) {
        print(msgErro + '\n');
        print("Para processar os sorteios das loterias e calcular estatisticas:");
        print("  jLothon  -c  [unidade:][diretorio] -j [d|l|u|q|m] \n");
        print("\t -c [diretorio]  Especifica o local com os arquivos de sorteios das Loterias.");
        print("\t -j [d|l|u|q|m]  Processa a loteria indicada:");
        print("\t                 d = Dia de Sorte,");
        print("\t                 l = Lotofacil,");
        print("\t                 u = Dupla Sena,");
        print("\t                 q = Quina,");
        print("\t                 m = Mega-Sena.");
    }

    private static AbstractProcess parseLoteria(String loteria, File dataDir) {
        return switch (loteria) {
            case "d" -> new ProcessDiaDeSorte(dataDir, MIN_THRESHOLD);
            case "l" -> new ProcessLotofacil(dataDir, MIN_THRESHOLD);
            case "u" -> new ProcessDuplaSena(dataDir, MIN_THRESHOLD);
            case "q" -> new ProcessQuina(dataDir, MIN_THRESHOLD);
            case "m" -> new ProcessMegaSena(dataDir, MIN_THRESHOLD);
            default -> null;
        };
    }

    public static void main(String[] args) {
        // Contabiliza o tempo gasto.
        long millis = System.currentTimeMillis();

        // Se nao foi fornecido nenhum argumento /configuracao, ja finaliza com erro.
        if (args == null || args.length < 4) {
            printOpcoes("ERRO: A sintaxe do comando esta incorreta.");
            System.exit(1);
        }

        String opt1 = args[0],
               opt2 = args[2];
        if (! "-d".equalsIgnoreCase(opt1) || ! "-j".equalsIgnoreCase(opt2)) {
            printOpcoes("ERRO: Comando para processamento não reconhecido.");
            System.exit(1);
        }

        File dataDir = new File(args[1]);
        if (! dataDir.exists() || ! dataDir.isDirectory()) {
            printOpcoes("ERRO: Diretorio com arquivos de dados nao encontrado.");
            System.exit(1);
        }

        String[] dataFiles = dataDir.list();
        if (dataFiles == null || dataFiles.length == 0) {
            printOpcoes("ERRO: Diretorio vazio, sem os arquivos de dados.");
            System.exit(1);
        }

        String loteria = args[3].toLowerCase();
        AbstractProcess process = parseLoteria(loteria, dataDir);
        if (loteria.length() != 1 || !"dluqm".contains(loteria) || process == null) {
            printOpcoes("ERRO: Codigo da loteria invalido.");
            System.exit(1);
        }

        // informacoes para debug:
        print(">> Loteria a processar = {0}.", process.loteria.nome);
        print(">> Diretorio Corrente = {0}.", System.getProperty("user.dir"));
        print(">> Diretorio de Dados = {0}.", dataDir.getAbsolutePath());
        print(">> Primeiro Arquivo CSV = {0}.", dataFiles[0]);

        // Inicia o processamento em sequencia para a loteria indicada (para evitar estouro de memoria):
        process.run();

        // Contabiliza e apresenta o tempo total gasto no processamento:
        millis = System.currentTimeMillis() - millis;
        long min = TimeUnit.MILLISECONDS.toMinutes(millis);
        long sec = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(min);
        print("\n\n>> TEMPO DE PROCESSAMENTO: {0} min, {1} seg.", min, sec);

        // Encerra o processamento informando que foi realizado com sucesso:
        System.exit(0);
    }

}
