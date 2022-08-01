package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

public class ComputeParidade extends AbstractCompute {

    public ComputeParidade(Loteria loteria, int[][] jogos, int[][] sorteios, int threshold) {
        super(loteria, jogos, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;

        // numero maximo de itens/paridades corresponde ao numero de dezenas sorteadas:
        int qtdItens = this.qtdBolas;

        // contabiliza pares (e impares) de cada combinacao de jogo:
        int[] paridadesJogos = Stats.newArrayInt(qtdItens);
        for (final int[] jogo : this.jogos) {
            int qtdPares = Stats.countPares(jogo);
            paridadesJogos[qtdPares]++;
        }

        // contabiliza o percentual das paridades:
        this.jogosPercentos = Stats.toPercentos(paridadesJogos, this.qtdJogos);

        // contabiliza os pares (e impares) repetidos de cada sorteio dos concursos:
        int[] ultimasParidadesRepetidas = Stats.newArrayInt(qtdItens);
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

    public int rateJogo(int ordinal, int[] jogo) {
        return Stats.countPares(jogo);
    }

}
