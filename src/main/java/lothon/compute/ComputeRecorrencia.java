package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

public class ComputeRecorrencia extends AbstractCompute {

    public ComputeRecorrencia(Loteria loteria, int[][] jogos, int[][] sorteios, int threshold) {
        super(loteria, jogos, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;

        // numero maximo de recorrencias corresponde ao numero de dezenas sorteadas:
        int qtdItens = this.qtdBolas;

        // contabiliza recorrencias de cada sorteio com todos os sorteios anteriores:
        int[] recorrenciasSorteios = Stats.newArrayInt(qtdItens);
        int[] ultimasRecorrenciasRepetidas = Stats.newArrayInt(qtdItens);
        this.valorUltimoConcurso = -1;
        this.valorPenultimoConcurso = -1;
        for (int i = 0; i < this.qtdSorteios; i++) {
            int qtdRecorrencias = Stats.maxRecorrencias(this.sorteios[i], this.sorteios, i);
            recorrenciasSorteios[qtdRecorrencias]++;

            // verifica se repetiu a recorrencia do ultima concurso:
            if (qtdRecorrencias == this.valorUltimoConcurso) {
                ultimasRecorrenciasRepetidas[qtdRecorrencias]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.valorPenultimoConcurso = this.valorUltimoConcurso;
            this.valorUltimoConcurso = qtdRecorrencias;
        }

        // contabiliza o percentual das ultimas recorrencias:
        this.jogosPercentos = Stats.toPercentos(recorrenciasSorteios, this.qtdSorteios);
        this.ultimosSorteiosPercentos = Stats.toPercentos(ultimasRecorrenciasRepetidas, this.qtdSorteios);
    }

    public int rateJogo(int ordinal, int[] jogo) {
        return Stats.maxRecorrencias(jogo, this.sorteios, -1);  // nao corresponde a nenhum sorteio...
    }

}
