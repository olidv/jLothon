package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

public class ComputeEspacamento extends AbstractCompute {

    public ComputeEspacamento(Loteria loteria, int[][] sorteios, int threshold) {
        super(loteria, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;

        // numero maximo de espacos deve considerar que a soma dos espacos eh o total de dezenas:
        int qtdItens = this.qtdDezenas / (this.qtdBolas - 1);

        // contabiliza o percentual dos espacamentos:
        this.jogosPercentos = Stats.toPercentos(this.loteria.getEspacamentosJogos(), this.loteria.qtdJogos);

        // contabiliza os espacos repetidos em cada sorteio dos concursos:
        int[] ultimosEspacamentosRepetidas = Stats.newArrayInt(qtdItens);
        this.valorUltimoConcurso = -1;
        this.valorPenultimoConcurso = -1;
        for (final int[] sorteio : this.sorteios) {
            int qtdEspacos = Stats.countEspacos(sorteio);
            // verifica se repetiu a espacamento do ultimo concurso:
            if (qtdEspacos == this.valorUltimoConcurso) {
                ultimosEspacamentosRepetidas[qtdEspacos]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.valorPenultimoConcurso = this.valorUltimoConcurso;
            this.valorUltimoConcurso = qtdEspacos;
        }

        // contabiliza o percentual dos ultimos espacamentos:
        this.ultimosSorteiosPercentos = Stats.toPercentos(ultimosEspacamentosRepetidas, this.qtdSorteios);
    }

    public int rateJogo(int[] jogo) {
        return Stats.countEspacos(jogo);
    }

}
