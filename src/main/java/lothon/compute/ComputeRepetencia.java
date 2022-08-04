package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

public class ComputeRepetencia extends AbstractCompute {

    // estrutura para avaliacao de jogos combinados da loteria:
    private int[] ultimoSorteio;

    public ComputeRepetencia(Loteria loteria, int[][] sorteios, int threshold) {
        super(loteria, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;
        this.ultimoSorteio = this.sorteios[qtdSorteios - 1];

        // numero maximo de repetencias corresponde ao numero de dezenas sorteadas:
        int qtdItens = this.qtdBolas;

        // contabiliza repetencias de cada sorteio com todos os sorteios anteriores:
        int[] repetenciasSorteios = Stats.newArrayInt(qtdItens);
        int[] ultimasRepetenciasRepetidas = Stats.newArrayInt(qtdItens);
        this.valorUltimoConcurso = -1;
        this.valorPenultimoConcurso = -1;
        int[] sorteioAnterior = this.sorteios[0];
        for (int i = 1; i < this.qtdSorteios; i++) {
            int[] sorteio = this.sorteios[i];
            int qtdRepetencias = Stats.countRepetencias(sorteio, sorteioAnterior);
            repetenciasSorteios[qtdRepetencias]++;
            sorteioAnterior = sorteio;  // atualiza o ultimo sorteio para a proxima iteracao...

            // verifica se repetiu a repetencia do ultima concurso:
            if (qtdRepetencias == this.valorUltimoConcurso) {
                ultimasRepetenciasRepetidas[qtdRepetencias]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.valorPenultimoConcurso = this.valorUltimoConcurso;
            this.valorUltimoConcurso = qtdRepetencias;
        }

        // contabiliza o percentual das ultimas repetencias:
        this.jogosPercentos = Stats.toPercentos(repetenciasSorteios, this.qtdSorteios);
        this.ultimosSorteiosPercentos = Stats.toPercentos(ultimasRepetenciasRepetidas, this.qtdSorteios);
    }

    public int rateJogo(int[] jogo) {
        return Stats.countRepetencias(jogo, this.ultimoSorteio);
    }

}
