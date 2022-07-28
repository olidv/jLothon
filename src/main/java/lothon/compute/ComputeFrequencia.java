package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

import java.util.ArrayList;
import java.util.List;

import static lothon.util.Eve.toRedutor;

public class ComputeFrequencia extends AbstractCompute {

    private int[] toposDezenas;
    private double[] toposPercentos;
    private double[] ultimosToposPercentos;
    private int qtdToposUltimoConcurso;
    private int qtdToposPenultimoConcurso;

    public ComputeFrequencia(Loteria loteria, int[][] sorteios, int threshold) {
        super(loteria, sorteios, threshold);
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
        this.qtdToposUltimoConcurso = -1;
        this.qtdToposPenultimoConcurso = -1;
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
            if (qtdTopos == this.qtdToposUltimoConcurso) {
                ultimosToposRepetidas[qtdTopos]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.qtdToposPenultimoConcurso = this.qtdToposUltimoConcurso;
            this.qtdToposUltimoConcurso = qtdTopos;
        }

        // contabiliza o percentual das ultimos repetencias:
        this.toposPercentos = Stats.toPercentos(toposFrequentes, this.qtdSorteios);
        this.ultimosToposPercentos = Stats.toPercentos(ultimosToposRepetidas, this.qtdSorteios);

        // extrai os topos do ranking com as dezenas com maior frequencia em todos os concursos:
        this.toposDezenas = Stats.calcToposFrequencia(this.sorteios, this.qtdDezenas, this.maxTopos);
    }

    public double eval(int ordinal, int[] jogo) {
        // a probabilidade de acerto depende do numero de topos no jogo:
        int qtdTopos = this.countToposFrequencia(jogo);
        double percent = this.toposPercentos[qtdTopos];

        // ignora valores muito baixos de probabilidade:
        if (percent < this.threshold) {
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        }

        // calcula o fator de percentual (metrica), para facilitar o calculo seguinte:
        double fatorPercent = toRedutor(percent);

        // verifica se esse jogo repetiu o numero de topos do ultimo e penultimo concursos:
        if (qtdTopos != this.qtdToposUltimoConcurso) {
            return fatorPercent;  // nao repetiu, ja pode pular fora
        } else if (qtdTopos == this.qtdToposPenultimoConcurso) {
            return fatorPercent * 0.1;  // pouco provavel de repetir mais de 2 ou 3 vezes
        }

        // se repetiu apenas o ultimo, obtem a probabilidade de repeticao do ultimo numero de topos:
        double percentRepetida = this.ultimosToposPercentos[qtdTopos];
        if (percentRepetida < 1) {  // baixa probabilidade pode ser descartada
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        } else {  // reduz a probabilidade porque esse jogo vai repetir o numero de topos:
            return fatorPercent * toRedutor(percentRepetida);
        }
    }

}
