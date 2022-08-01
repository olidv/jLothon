package lothon;

import static lothon.util.Eve.*;

import lothon.process.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Main {

    private static void printOpcoes(String msgErro) {
        print(msgErro + '\n');
        print("Para processar os sorteios das loterias e calcular estatisticas:");
        print("  jLothon  -c  [unidade:][diretorio] \n");
        print("\t [unidade:][diretorio]  Especifica o local com os arquivos de sorteios das Loterias.");
    }

    private static void runProcessDiaDeSorte(File dataDir) {
        new ProcessDiaDeSorte(dataDir).run();
    }

    private static void runProcessLotofacil(File dataDir) {
        new ProcessLotofacil(dataDir).run();
    }

    private static void runProcessDuplaSena(File dataDir) {
        new ProcessDuplaSena(dataDir).run();
    }

    private static void runProcessQuina(File dataDir) {
        new ProcessQuina(dataDir).run();
    }

    private static void runProcessMegaSena(File dataDir) {
        new ProcessMegaSena(dataDir).run();
    }

    public static void main(String[] args) {
        // Contabiliza o tempo gasto.
        long millis = System.currentTimeMillis();

        // Se nao foi fornecido nenhum argumento /configuracao, ja finaliza com erro.
        if (args == null || args.length < 2) {
            printOpcoes("ERRO: A sintaxe do comando esta incorreta.");
            System.exit(1);
        }

        String comando = args[0];
        if (! "-d".equalsIgnoreCase(comando)) {
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

        // informacoes para debug:
        print(">> Diretorio Corrente = {0}.", System.getProperty("user.dir"));
        print(">> Diretorio de Dados = {0}.", dataDir.getAbsolutePath());
        print(">> Primeiro Arquivo CSV = {0}.", dataFiles[0]);

        // Inicia o processamento em sequencia para cada loteria (para evitar estouro de memoria):
        runProcessDiaDeSorte(dataDir);
        runProcessLotofacil(dataDir);
        runProcessDuplaSena(dataDir);
        runProcessQuina(dataDir);
        runProcessMegaSena(dataDir);

        // Contabiliza e apresenta o tempo total gasto no processamento:
        millis = System.currentTimeMillis() - millis;
        long min = TimeUnit.MILLISECONDS.toMinutes(millis);
        long sec = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(min);
        print("\n\n>> TEMPO DE PROCESSAMENTO: {0} min, {1} seg.", min, sec);

        // Encerra o processamento informando que foi realizado com sucesso:
        System.exit(0);
    }

}
