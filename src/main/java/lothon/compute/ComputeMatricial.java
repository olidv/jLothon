package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

public class ComputeMatricial extends AbstractCompute {

    public ComputeMatricial(Loteria loteria, int[][] sorteios, int threshold) {
        super(loteria, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;

        // numero maximo de itens/matrizes corresponde ao numero de dezenas sorteadas ao dobro (para colunas e para linhas):
        int qtdItens = this.qtdBolas * 2;

        // contabiliza o percentual das matrizes:
        this.jogosPercentos = Stats.toPercentos(this.loteria.getMatriciaisJogos(), this.loteria.qtdJogos);

        // contabiliza as matrizes repetidas em cada sorteio dos concursos:
        int[] ultimasMatrizesRepetidas = Stats.newArrayInt(qtdItens);
        this.valorUltimoConcurso = -1;
        this.valorPenultimoConcurso = -1;
        for (final int[] sorteio : this.sorteios) {
            // calculo da matriz (maximo de colunas e linhas):
            int valMaxMatriz = Stats.maxColunas(sorteio) + Stats.maxLinhas(sorteio);
            // verifica se repetiu a matriz do ultimo concurso:
            if (valMaxMatriz == this.valorUltimoConcurso) {
                ultimasMatrizesRepetidas[valMaxMatriz]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.valorPenultimoConcurso = this.valorUltimoConcurso;
            this.valorUltimoConcurso = valMaxMatriz;
        }

        // contabiliza o percentual das ultimas matrizes:
        this.ultimosSorteiosPercentos = Stats.toPercentos(ultimasMatrizesRepetidas, this.qtdSorteios);
    }

    public int rateJogo(int[] jogo) {
        return Stats.maxColunas(jogo) + Stats.maxLinhas(jogo);
    }

}
