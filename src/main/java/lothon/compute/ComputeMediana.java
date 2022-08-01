package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

public class ComputeMediana extends AbstractCompute {

    public ComputeMediana(Loteria loteria, int[][] jogos, int[][] sorteios, int threshold) {
        super(loteria, jogos, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;

        // numero de itens/medianas vai depender do valor da ultima dezena:
        int qtdItens = (int) Math.round(Math.sqrt(this.qtdDezenas));

        // calcula a mediana de cada combinacao de jogo:
        int[] medianasJogos = Stats.newArrayInt(qtdItens);
        for (final int[] jogo : this.jogos) {
            int valMediana = Stats.calcMediana(jogo);
            medianasJogos[valMediana]++;
        }

        // calcula o percentual das medianas:
        this.jogosPercentos = Stats.toPercentos(medianasJogos, this.qtdJogos);

        // calcula a mediana repetida de cada sorteio dos concursos:
        int[] ultimasMedianasRepetidas = Stats.newArrayInt(qtdItens);
        this.valorUltimoConcurso = -1;
        this.valorPenultimoConcurso = -1;
        for (final int[] sorteio : this.sorteios) {
            int valMediana = Stats.calcMediana(sorteio);
            // verifica se repetiu a mediana do ultimo concurso:
            if (valMediana == this.valorUltimoConcurso) {
                ultimasMedianasRepetidas[valMediana]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.valorPenultimoConcurso = this.valorUltimoConcurso;
            this.valorUltimoConcurso = valMediana;
        }

        // contabiliza o percentual das ultimas medianas:
        this.ultimosSorteiosPercentos = Stats.toPercentos(ultimasMedianasRepetidas, this.qtdSorteios);
    }

    public int rateJogo(int ordinal, int[] jogo) {
        return Stats.calcMediana(jogo);
    }

}
