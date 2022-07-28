package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

import static lothon.util.Eve.toRedutor;

public class ComputeSequencia extends AbstractCompute {

    private double[] sequenciasPercentos;
    private double[] ultimasSequenciasPercentos;
    private int qtdSequenciasUltimoConcurso;
    private int qtdSequenciasPenultimoConcurso;

    public ComputeSequencia(Loteria loteria, int[][] sorteios, int threshold) {
        super(loteria, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;

        // numero de sequencias eh sempre menor que o numero de bolas sorteadas:
        int qtdItens = this.qtdBolas - 1;

        // efetua analise de todas as combinacoes de jogos da loteria:
        int[][] jogos = Stats.geraCombinacoes(this.qtdDezenas, this.qtdBolas);
        int qtdJogos = jogos.length;

        // contabiliza o numero de sequencias de cada combinacao de jogo:
        int[] sequenciasJogos = Stats.newArrayInt(qtdItens);
        for (final int[] jogo : jogos) {
            int qtdSequencias = Stats.countSequencias(jogo);
            sequenciasJogos[qtdSequencias]++;
        }

        // contabiliza o percentual das sequencias:
        this.sequenciasPercentos = Stats.toPercentos(sequenciasJogos, qtdJogos);

        // contabiliza as sequencias repetidas de cada sorteio dos concursos:
        int[] ultimasSequenciasRepetidas = Stats.newArrayInt(qtdItens);
        this.qtdSequenciasUltimoConcurso = -1;
        this.qtdSequenciasPenultimoConcurso = -1;
        for (final int[] sorteio : this.sorteios) {
            int qtdSequencias = Stats.countSequencias(sorteio);
            // verifica se repetiu o numero de sequencias do ultimo concurso:
            if (qtdSequencias == this.qtdSequenciasUltimoConcurso) {
                ultimasSequenciasRepetidas[qtdSequencias]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.qtdSequenciasPenultimoConcurso = this.qtdSequenciasUltimoConcurso;
            this.qtdSequenciasUltimoConcurso = qtdSequencias;
        }

        // contabiliza o percentual das ultimas sequencias:
        this.ultimasSequenciasPercentos = Stats.toPercentos(ultimasSequenciasRepetidas, this.qtdSorteios);
    }

    public double eval(int ordinal, int[] jogo) {
        // a probabilidade de acerto depende do numero de sequencias no jogo:
        int qtdSequencias = Stats.countSequencias(jogo);
        double percent = this.sequenciasPercentos[qtdSequencias];

        // ignora valores muito baixos de probabilidade:
        if (percent < this.threshold) {
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        }

        // calcula o fator de percentual (metrica), para facilitar o calculo seguinte:
        double fatorPercent = toRedutor(percent);

        // verifica se esse jogo repetiu o numero de sequencias do ultimo e penultimo concursos:
        if (qtdSequencias != this.qtdSequenciasUltimoConcurso) {
            return fatorPercent;  // nao repetiu, ja pode pular fora
        } else if (qtdSequencias == this.qtdSequenciasPenultimoConcurso) {
            return fatorPercent * 0.1;  // pouco provavel de repetir mais de 2 ou 3 vezes
        }

        // se repetiu apenas o ultimo, obtem a probabilidade de repeticao do ultimo numero de sequencias:
        double percentRepetida = this.ultimasSequenciasPercentos[qtdSequencias];
        if (percentRepetida < 1) {  // baixa probabilidade pode ser descartada
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        } else {  // reduz a probabilidade porque esse jogo vai repetir o numero de sequencias:
            return fatorPercent * toRedutor(percentRepetida);
        }
    }

}
