package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

public class ComputeSequencia extends AbstractCompute {

    public ComputeSequencia(Loteria loteria, int[][] sorteios, int threshold) {
        super(loteria, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;

        // numero de sequencias eh sempre menor que o numero de bolas sorteadas:
        int qtdItens = this.qtdBolas - 1;

        // contabiliza o percentual das sequencias:
        this.jogosPercentos = Stats.toPercentos(this.loteria.getSequenciasJogos(), this.loteria.qtdJogos);

        // contabiliza as sequencias repetidas de cada sorteio dos concursos:
        int[] ultimasSequenciasRepetidas = Stats.newArrayInt(qtdItens);
        this.valorUltimoConcurso = -1;
        this.valorPenultimoConcurso = -1;
        for (final int[] sorteio : this.sorteios) {
            int qtdSequencias = Stats.countSequencias(sorteio);
            // verifica se repetiu o numero de sequencias do ultimo concurso:
            if (qtdSequencias == this.valorUltimoConcurso) {
                ultimasSequenciasRepetidas[qtdSequencias]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.valorPenultimoConcurso = this.valorUltimoConcurso;
            this.valorUltimoConcurso = qtdSequencias;
        }

        // contabiliza o percentual das ultimas sequencias:
        this.ultimosSorteiosPercentos = Stats.toPercentos(ultimasSequenciasRepetidas, this.qtdSorteios);
    }

    public int rateJogo(int[] jogo) {
        return Stats.countSequencias(jogo);
    }

}
