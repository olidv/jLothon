package lothon.process;

import lothon.compute.AbstractCompute;
import lothon.domain.MaisMilionaria;
import lothon.domain.Jogo;
import lothon.util.Infra;
import lothon.util.Stats;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

import static lothon.util.Eve.print;

public class ProcessMaisMilionaria extends AbstractProcess {

    public ProcessMaisMilionaria(File dataDir, int threshold) {
        super(new MaisMilionaria(), dataDir, threshold);
    }

    public void run() {
        // Inicia o processamento efetuando a leitura dos arquivos CSV:
        Path csvInput = this.loteria.getCsvInput(this.dataDir);
        print("\n>> {0}: Arquivo CSV com sorteios = {1}.", this.loteria.nome, csvInput.toAbsolutePath());
        int[][] sorteios = Infra.loadSorteios(csvInput);
        if (sorteios == null || sorteios.length == 0) {
            print(">> {0}: Arquivo CSV esta vazio. ERRO: Processo abortado.", this.loteria.nome);
            return;
        }

        // efetua a geracao dos jogos de acordo com as combinacoes de jogos da loteria:
        int[][] jogos = Stats.geraCombinacoes(this.loteria.qtdDezenas, this.loteria.qtdBolas);
        int qtdJogos = jogos.length;

        // Efetua a execucao de cada processo de analise em sequencia (chain) para coleta de dados:
        AbstractCompute[] computeChain = this.initComputeChain(sorteios, this.min_threshold);

        // efetua processamento dos sorteios da loteria:
        this.jogosComputados = new ArrayList<>(qtdJogos);
        int ordinal = 0;
        int qtdZerados = 0;
        for (final int[] jogo : jogos) {
            ordinal++;  // primeiro jogo ira comecar do #1
            // executa a avaliacao do jogo, para verificar se sera considerado ou descartado:
            double fator = 0;
            for (final AbstractCompute compute : computeChain) {
                double valor = compute.eval(jogo);

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
        print(">> {0}: Finalizado o processamento de  {1}  combinacoes de jogos.", this.loteria.nome, qtdJogos);
        print(">> {0}: Eliminados (zerados) = {1}  .:.  Considerados (inclusos) = {2}", this.loteria.nome, qtdZerados, qtdInclusos);

        // ao final, salva os jogos computados em arquivo CSV:
        Path csvOuput = this.loteria.getCsvOuput(this.dataDir);
        Infra.saveJogos(csvOuput, jogosComputados);
        print(">> {0}: Arquivo CSV com jogos computados = {1}.", this.loteria.nome, csvOuput.toAbsolutePath());
    }

}
