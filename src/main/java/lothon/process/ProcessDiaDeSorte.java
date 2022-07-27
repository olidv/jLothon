package lothon.process;

import static lothon.util.Eve.*;
import lothon.compute.AbstractCompute;
import lothon.compute.ComputeParidade;
import lothon.compute.ComputeSequencia;
import lothon.domain.Jogo;
import lothon.domain.Loteria;
import lothon.util.Infra;
import lothon.util.Stats;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProcessDiaDeSorte extends AbstractProcess {

    private static final Loteria DIA_DE_SORTE = Loteria.DIA_DE_SORTE;
    private static final int THRESHOLD = 10;

    public ProcessDiaDeSorte(File dataDir) {
        super(dataDir);
    }

    private AbstractCompute[] getComputeChain(int[][] sorteios) {
        return new AbstractCompute[]{new ComputeParidade(sorteios, DIA_DE_SORTE.qtdDezenas, DIA_DE_SORTE.qtdBolas, THRESHOLD),
                                     new ComputeSequencia(sorteios, DIA_DE_SORTE.qtdDezenas, DIA_DE_SORTE.qtdBolas, THRESHOLD)};
    }

    public void run() {
        // Inicia o processamento efetuando a leitura dos arquivos CSV:
        Path csvInput = DIA_DE_SORTE.getCsvInput(this.dataDir);
        print("{0}: Arquivo CSV com sorteios = {1}", DIA_DE_SORTE.nome, csvInput.toAbsolutePath());
        int[][] sorteios = Infra.loadSorteios(csvInput);
        if (sorteios == null || sorteios.length == 0) {
            print("{0}: Arquivo CSV esta vazio. ERRO: Processo abortado.", DIA_DE_SORTE.nome);
            return;
        }

        // Efetua a execucao de cada processo de analise em sequencia (chain) para coleta de dados:
        AbstractCompute[] computeChain = getComputeChain(sorteios);
        for (final AbstractCompute compute : computeChain)
            try {
                compute.start();
                compute.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        // gera as combinacoes de jogos da loteria:
        int[][] jogos = Stats.geraCombinacoes(DIA_DE_SORTE.qtdDezenas, DIA_DE_SORTE.qtdBolas);
        int qtdJogos = jogos.length;

        // efetua processamento dos sorteios da loteria:
        List<Jogo> jogosComputados = new ArrayList<>(qtdJogos);
        int ordinal = 0;
        int qtdZerados = 0;
        for (final int[] jogo : jogos) {
            ordinal++;  // primeiro jogo ira comecar do #1
//            if (ordinal % 100000 == 0) {
//                print("{0}: ordinal = {1}", DIA_DE_SORTE.nome, ordinal);
//            }

            // executa a avaliacao do jogo, para verificar se sera considerado ou descartado:
            double fator = 0;
            for (final AbstractCompute compute : computeChain) {
                double valor = compute.eval(ordinal, jogo);

                // ignora o resto das analises se a metrica zerou:
                if (valor > 0) {
                    fator += valor;  // probabilidade da uniao de dois eventos
                } else {
                    fator = 0;  // zera o fator para que o jogo nao seja considerado
                    break;  // ignora e pula para o proximo jogo, acelerando o processamento
                }
            }

            // se a metrica atingir o ponto de corte, entao mantem o jogo para apostar:
            if (fator > 0) {
                jogosComputados.add(new Jogo(ordinal, jogo, fator));
            } else {
                qtdZerados++;
            }
        }

        int qtdInclusos = jogosComputados.size();
        print("{0}: Finalizado o processamento de  {1}  combinacoes de jogos.", DIA_DE_SORTE.nome, qtdJogos);
        print("{0}: Eliminados (zerados) = {1}  ...  Considerados (inclusos) = {2}", DIA_DE_SORTE.nome, qtdZerados, qtdInclusos);

        // ao final, salva os jogos computados em arquivo CSV:
        Path csvOuput = DIA_DE_SORTE.getCsvOuput(this.dataDir);
        Infra.saveJogos(csvOuput, jogosComputados);
        print("{0}: Arquivo CSV com jogos computados = {1}", DIA_DE_SORTE.nome, csvOuput.toAbsolutePath());
    }

}
