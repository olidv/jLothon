package lothon.process;

import lothon.compute.AbstractCompute;
import lothon.domain.Jogo;
import lothon.domain.Loteria;
import lothon.util.Infra;
import lothon.util.Stats;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static lothon.util.Eve.print;

public class ProcessQuina extends AbstractProcess {

    private static final Loteria QUINA = Loteria.QUINA;
    private static final int THRESHOLD = 10;

    public ProcessQuina(File dataDir) {
        super(dataDir);
    }

    public void run() {
        // Contabiliza o tempo gasto.
        long millis = System.currentTimeMillis();

        // Inicia o processamento efetuando a leitura dos arquivos CSV:
        Path csvInput = QUINA.getCsvInput(this.dataDir);
        print("\n\n{0}: Arquivo CSV com sorteios = {1}.", QUINA.nome, csvInput.toAbsolutePath());
        int[][] sorteios = Infra.loadSorteios(csvInput);
        if (sorteios == null || sorteios.length == 0) {
            print("{0}: Arquivo CSV esta vazio. ERRO: Processo abortado.", QUINA.nome);
            return;
        }

        // efetua a geracao dos jogos de acordo com as combinacoes de jogos da loteria:
        int[][] jogos = Stats.geraCombinacoes(QUINA.qtdDezenas, QUINA.qtdBolas);
        int qtdJogos = jogos.length;

        // Efetua a execucao de cada processo de analise em sequencia (chain) para coleta de dados:
        AbstractCompute[] computeChain = this.initComputeChain(QUINA, jogos, sorteios, THRESHOLD);

        // processamento preliminar, apenas para saber quantos jogos sao zerados por cada compute:
        int ordinal = 0;
        int qtdZerados = 0;
        for (final int[] jogo : jogos) {
            ordinal++;  // primeiro jogo ira comecar do #1

            // executa a avaliacao do jogo e desconsidera:
            double fator = 1.0;
            for (final AbstractCompute compute : computeChain) {
                final double valor = compute.eval(ordinal, jogo);
                fator *= valor;  // se valor for zero, ira zerar o fator tambem
            }
            if (fator == 0.0)
                qtdZerados++;
        }
        print("{0}: Foram zerados {1} jogos; do total {2} sobrou {3}:",
                QUINA.nome, qtdZerados, qtdJogos, qtdJogos - qtdZerados);
        for (final AbstractCompute compute : computeChain)
            print("\t{0}: qtd-zerados = {1}", compute.getClass().getName(), compute.qtdZerados);

        // agora sim, efetua processamento dos sorteios da loteria:
        this.jogosComputados = new ArrayList<>(qtdJogos);
        ordinal = qtdZerados = 0;
        for (final int[] jogo : jogos) {
            ordinal++;  // primeiro jogo ira comecar do #1
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
            if (fator == 0.0) {
                qtdZerados++;
            } else {
                this.jogosComputados.add(new Jogo(ordinal, jogo, fator));
            }
        }
        int qtdInclusos = this.jogosComputados.size();
        print("\n{0}: Finalizado o processamento de  {1}  combinacoes de jogos.", QUINA.nome, qtdJogos);
        print("{0}: Eliminados (zerados) = {1}  .:.  Considerados (inclusos) = {2}", QUINA.nome, qtdZerados, qtdInclusos);

        // teste para verificar o numero de apostas sem muitas repeticoes de dezenas entre si:
//        for (int i = 0; i < QUINA.qtdBolas-2; i++) {  // quantidade de recorrencias
//            List<Jogo> jogosBolao = this.relacionarJogos(i);
//            print("{0}: *** MAX-RECORRENCIAS = {1} ... #JOGOS = {2}", QUINA.nome, i, jogosBolao.size());
//        }

        // ao final, salva os jogos computados em arquivo CSV:
        Path csvOuput = QUINA.getCsvOuput(this.dataDir);
        Infra.saveJogos(csvOuput, jogosComputados);
        print("{0}: Arquivo CSV com jogos computados = {1}.", QUINA.nome, csvOuput.toAbsolutePath());

        // Contabiliza e apresenta o tempo total gasto no processamento:
        millis = System.currentTimeMillis() - millis;
        long min = TimeUnit.MILLISECONDS.toMinutes(millis);
        long sec = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(min);
        print("{0}: TEMPO DE PROCESSAMENTO: {1} min, {2} seg.", QUINA.nome, min, sec);
    }

}
