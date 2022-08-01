package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

import java.util.ArrayList;
import java.util.List;

public class ComputeFrequencia extends AbstractCompute {

    private int[] toposDezenas;

    public ComputeFrequencia(Loteria loteria, int[][] jogos, int[][] sorteios, int threshold) {
        super(loteria, jogos, sorteios, threshold);
    }

    private int countToposFrequencia(int[] dezenas) {
        return Stats.countRepetencias(dezenas, this.toposDezenas);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;

        // numero maximo de topos eh definido em cada loteria:
        int qtdItens = this.maxTopos;

        // contabiliza repetencias de cada sorteio com todos os sorteios anteriores:
        int[] toposFrequentes = Stats.newArrayInt(qtdItens);
        int[] ultimosToposRepetidas = Stats.newArrayInt(qtdItens);
        this.valorUltimoConcurso = -1;
        this.valorPenultimoConcurso = -1;
        List<int[]> sorteiosAnteriores = new ArrayList<>(this.qtdSorteios);
        sorteiosAnteriores.add(this.sorteios[0]);
        for (int i = 1; i < this.qtdSorteios; i++) {
            int[] sorteio = this.sorteios[i];
            // extrai o topo do ranking com as dezenas com maior frequencia ate o concurso atual:
            int[] toposSorteio = Stats.calcToposFrequencia(sorteiosAnteriores, this.qtdDezenas, this.maxTopos);

            // identifica o numero de dezenas do concurso que estao entre o topo de frequencia:
            int qtdTopos = Stats.countRepetencias(sorteio, toposSorteio);
            toposFrequentes[qtdTopos]++;

            // adiciona o concurso atual para a proxima iteracao (ai ele sera um concurso anterior):
            sorteiosAnteriores.add(sorteio);

            // verifica se repetiu a repetencia do ultimo concurso:
            if (qtdTopos == this.valorUltimoConcurso) {
                ultimosToposRepetidas[qtdTopos]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.valorPenultimoConcurso = this.valorUltimoConcurso;
            this.valorUltimoConcurso = qtdTopos;
        }

        // contabiliza o percentual das ultimos repetencias:
        this.jogosPercentos = Stats.toPercentos(toposFrequentes, this.qtdSorteios);
        this.ultimosSorteiosPercentos = Stats.toPercentos(ultimosToposRepetidas, this.qtdSorteios);

        // extrai os topos do ranking com as dezenas com maior frequencia em todos os concursos:
        this.toposDezenas = Stats.calcToposFrequencia(this.sorteios, this.qtdDezenas, this.maxTopos);
    }

    public int rateJogo(int ordinal, int[] jogo) {
        return this.countToposFrequencia(jogo);
    }

}
