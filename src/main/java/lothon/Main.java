package lothon;

import lothon.domain.Loteria;
import lothon.util.Infra;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class Main {

    private static void printOpcoes(String msgErro) {
        System.out.println(msgErro + '\n');
        System.out.println("Para processar os sorteios das loterias e calcular estatisticas:");
        System.out.println("jLothon  -c  [unidade:][diretorio] \n");

        System.out.println("\t [unidade:][diretorio]  Especifica o local com os arquivos de sorteios das Loterias.");
    }

    public static void main(String[] args) throws IOException {
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

        // Inicia o processamento efetuando a leitura dos arquivos CSV:
        System.out.println(">> Diretorio Corrente = " + System.getProperty("user.dir"));
        System.out.println(">> Diretorio de Dados = " + dataDir.getAbsolutePath());
        System.out.println(">> Primeiro Arquivo CSV = " + dataFiles[0]);

        Path csvPath = Loteria.DIA_DE_SORTE.getCsvPath(dataDir);
        System.out.println(">> Arquivo Dia de Sorte = " + csvPath.toAbsolutePath());

        int[][] sorteios = Infra.loadSorteios(csvPath);

        // Contabiliza e apresenta o tempo total gasto no processamento:
        millis = System.currentTimeMillis() - millis;
        long min = TimeUnit.MILLISECONDS.toMinutes(millis);
        long sec = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(min);
        System.out.println("\n\n>> TEMPO DE PROCESSAMENTO: " + String.format("%d min, %d seg", min, sec));

        // Encerra o processamento informando que foi realizado com sucesso:
        System.exit(0);

    }


}