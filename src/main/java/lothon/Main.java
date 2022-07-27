package lothon;

import static lothon.util.Eve.*;
import lothon.process.AbstractProcess;
import lothon.process.ProcessDiaDeSorte;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Main {

    private static void printOpcoes(String msgErro) {
        print(msgErro + '\n');
        print("Para processar os sorteios das loterias e calcular estatisticas:");
        print("  jLothon  -c  [unidade:][diretorio] \n");
        print("\t [unidade:][diretorio]  Especifica o local com os arquivos de sorteios das Loterias.");
    }

    private static AbstractProcess[] getProcessChain(File dataDir) {
        return new AbstractProcess[]{new ProcessDiaDeSorte(dataDir),
                                     new ProcessDiaDeSorte(dataDir)};
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
        print(">> Diretorio Corrente = {0}", System.getProperty("user.dir"));
        print(">> Diretorio de Dados = {0}", dataDir.getAbsolutePath());
        print(">> Primeiro Arquivo CSV = {0}", dataFiles[0]);
        print("\n\n");

        // Inicia o processamento em paralelo para cada loteria:
        for (final AbstractProcess process : getProcessChain(dataDir))
            try {
                process.start();
                process.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        // Contabiliza e apresenta o tempo total gasto no processamento:
        millis = System.currentTimeMillis() - millis;
        long min = TimeUnit.MILLISECONDS.toMinutes(millis);
        long sec = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(min);
        print("\n\n>> TEMPO DE PROCESSAMENTO: {0} min, {1} seg", min, sec);

        // Encerra o processamento informando que foi realizado com sucesso:
        System.exit(0);
    }

}
