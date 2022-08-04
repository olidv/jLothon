package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

public class ComputeConsecutiva extends AbstractCompute {

    public ComputeConsecutiva(Loteria loteria, int[][] sorteios, int threshold) {
        super(loteria, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;

        // numero de consecutivas eh sempre menor que o numero de bolas sorteadas:
        int qtdItens = this.qtdBolas - 1;

        // contabiliza o percentual das consecutivas:
        this.jogosPercentos = Stats.toPercentos(this.loteria.getConsecutivasJogos(), this.loteria.qtdJogos);

        // contabiliza as dezenas consecutivas repetidas de cada sorteio dos concursos:
        int[] ultimasConsecutivasRepetidas = Stats.newArrayInt(qtdItens);
        this.valorUltimoConcurso = -1;
        this.valorPenultimoConcurso = -1;
        for (final int[] sorteio : this.sorteios) {
            int qtdConsecutivas = Stats.countConsecutivas(sorteio);
            // verifica se repetiu o numero de consecutivas do ultimo concurso:
            if (qtdConsecutivas == this.valorUltimoConcurso) {
                ultimasConsecutivasRepetidas[qtdConsecutivas]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.valorPenultimoConcurso = this.valorUltimoConcurso;
            this.valorUltimoConcurso = qtdConsecutivas;
        }

        // contabiliza o percentual das ultimas consecutivas:
        this.ultimosSorteiosPercentos = Stats.toPercentos(ultimasConsecutivasRepetidas, this.qtdSorteios);
    }

    public int rateJogo(int[] jogo) {
        return Stats.countConsecutivas(jogo);
    }

}
