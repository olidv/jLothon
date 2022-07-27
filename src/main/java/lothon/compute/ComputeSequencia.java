package lothon.compute;

import lothon.util.Stats;

import static lothon.util.Eve.toRedutor;

public class ComputeSequencia extends AbstractCompute {

    private double[] sequenciasPercentos;
    private double[] ultimasSequenciasPercentos;
    private int qtdSequenciasUltimoConcurso;
    private int qtdSequenciasPenultimoConcurso;

    public ComputeSequencia(int[][] sorteios, int qtdDezenas, int qtdBolas, int threshold) {
        super(sorteios, qtdDezenas, qtdBolas, threshold);
    }

    public void run() {
        // numero de sequencias eh sempre menor que o numero de bolas sorteadas:
        int qtdItens = this.qtdBolas;

        // efetua analise de todas as combinacoes de jogos da loteria:
        int[][] jogos = Stats.geraCombinacoes(this.qtdDezenas, this.qtdBolas);
        int qtdJogos = jogos.length;

        // contabiliza o numero de sequencias de cada combinacao de jogo:
        int[] sequenciasJogos = new int[qtdItens];
        for (final int[] jogo : jogos) {
            int qtdSequencias = Stats.countSequencias(jogo);
            sequenciasJogos[qtdSequencias]++;
        }

        // contabiliza o percentual das sequencias:
        this.sequenciasPercentos = new double[qtdItens];
        for (int i = 0; i < qtdItens; i++) {
            this.sequenciasPercentos[i] = (sequenciasJogos[i] * 100.0d) / qtdJogos;
        }

        // contabiliza as sequencias de cada sorteio dos concursos:
        int[] ultimasSequenciasRepetidas = new int[qtdItens];
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
        this.ultimasSequenciasPercentos = new double[qtdItens];
        for (int i = 0; i < qtdItens; i++) {
            this.ultimasSequenciasPercentos[i] = (ultimasSequenciasRepetidas[i] * 100.0d) / this.qtdSorteios;
        }
    }

    public double eval(int ordinal, int[] jogo) {
        // a probabilidade de acerto depende do numero de sequencias no jogo:
        int qtdSequencias = Stats.countSequencias(jogo);
        double percent = this.sequenciasPercentos[qtdSequencias];

        // ignora valores muito baixos de probabilidade:
        if (percent < this.threshold) {
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
            return 0;
        } else {  // reduz a probabilidade porque esse jogo vai repetir o numero de sequencias:
            return fatorPercent * toRedutor(percentRepetida);
        }
    }

}
