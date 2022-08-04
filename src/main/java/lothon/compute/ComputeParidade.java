package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

public class ComputeParidade extends AbstractCompute {

    public ComputeParidade(Loteria loteria, int[][] sorteios, int threshold) {
        super(loteria, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;

        // contabiliza o percentual das paridades:
        this.jogosPercentos = Stats.toPercentos(this.loteria.getParidadesJogos(), this.loteria.qtdJogos);

        // contabiliza os pares (e impares) repetidos de cada sorteio dos concursos:
        int[] ultimasParidadesRepetidas = Stats.newArrayInt(this.qtdBolas);
        this.valorUltimoConcurso = -1;
        this.valorPenultimoConcurso = -1;
        for (final int[] sorteio : this.sorteios) {
            int qtdPares = Stats.countPares(sorteio);
            // verifica se repetiu a paridade do ultimo concurso:
            if (qtdPares == this.valorUltimoConcurso) {
                ultimasParidadesRepetidas[qtdPares]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.valorPenultimoConcurso = this.valorUltimoConcurso;
            this.valorUltimoConcurso = qtdPares;
        }

        // contabiliza o percentual das ultimas paridades:
        this.ultimosSorteiosPercentos = Stats.toPercentos(ultimasParidadesRepetidas, this.qtdSorteios);
    }

    public int rateJogo(int[] jogo) {
        return Stats.countPares(jogo);
    }

}
