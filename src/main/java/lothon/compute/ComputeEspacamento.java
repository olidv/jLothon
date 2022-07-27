package lothon.compute;

import lothon.util.Stats;

import static lothon.util.Eve.toRedutor;

public class ComputeEspacamento extends AbstractCompute {

    private double[] espacamentosPercentos;
    private double[] ultimosEspacamentosPercentos;
    private int qtdEspacosUltimoConcurso;
    private int qtdEspacosPenultimoConcurso;

    public ComputeEspacamento(int[][] sorteios, int qtdDezenas, int qtdBolas, int threshold) {
        super(sorteios, qtdDezenas, qtdBolas, threshold);
    }

    public void run() {
        // numero de itens/espacamentos deve compensar pelo primeiro item do array ser 0:
        int qtdItens = (this.qtdDezenas / (this.qtdBolas - 1)) + 1;

        // efetua analise de todas as combinacoes de jogos da loteria:
        int[][] jogos = Stats.geraCombinacoes(this.qtdDezenas, this.qtdBolas);
        int qtdJogos = jogos.length;

        // contabiliza espacos em cada combinacao de jogo:
        int[] espacamentosJogos = new int[qtdItens];
        for (final int[] jogo : jogos) {
            int qtdEspacos = Stats.countEspacos(jogo);
            espacamentosJogos[qtdEspacos]++;
        }

        // contabiliza o percentual dos espacamentos:
        this.espacamentosPercentos = new double[qtdItens];
        for (int i = 0; i < qtdItens; i++) {
            this.espacamentosPercentos[i] = (espacamentosJogos[i] * 100.0d) / qtdJogos;
        }

        // contabiliza os espacos repetidos em cada sorteio dos concursos:
        int[] ultimosEspacamentosRepetidas = new int[qtdItens];
        this.qtdEspacosUltimoConcurso = -1;
        this.qtdEspacosPenultimoConcurso = -1;
        for (final int[] sorteio : this.sorteios) {
            int qtdEspacos = Stats.countEspacos(sorteio);
            // verifica se repetiu a espacamento do ultimo concurso:
            if (qtdEspacos == this.qtdEspacosUltimoConcurso) {
                ultimosEspacamentosRepetidas[qtdEspacos]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.qtdEspacosPenultimoConcurso = this.qtdEspacosUltimoConcurso;
            this.qtdEspacosUltimoConcurso = qtdEspacos;
        }

        // contabiliza o percentual dos ultimos espacamentos:
        this.ultimosEspacamentosPercentos = new double[qtdItens];
        for (int i = 0; i < qtdItens; i++) {
            this.ultimosEspacamentosPercentos[i] = (ultimosEspacamentosRepetidas[i] * 100.0d) / this.qtdSorteios;
        }
    }

    public double eval(int ordinal, int[] jogo) {
        // a probabilidade de acerto depende do numero de espacos no jogo:
        int qtdEspacos = Stats.countEspacos(jogo);
        double percent = this.espacamentosPercentos[qtdEspacos];

        // ignora valores muito baixos de probabilidade:
        if (percent < this.threshold) {
            return 0;
        }

        // calcula o fator de percentual (metrica), para facilitar o calculo seguinte:
        double fatorPercent = toRedutor(percent);

        // verifica se esse jogo repetiu a espacamento do ultimo e penultimo concursos:
        if (qtdEspacos != this.qtdEspacosUltimoConcurso) {
            return fatorPercent;  // nao repetiu, ja pode pular fora
        } else if (qtdEspacos == this.qtdEspacosPenultimoConcurso) {
            return fatorPercent * 0.1;  // pouco provavel de repetir mais de 2 ou 3 vezes
        }

        // se repetiu apenas o ultimo, obtem a probabilidade de repeticao do ultimo espacamento:
        double percentRepetida = this.ultimosEspacamentosPercentos[qtdEspacos];
        if (percentRepetida < 1) {  // baixa probabilidade pode ser descartada
            return 0;
        } else {  // reduz a probabilidade porque esse jogo vai repetir o espacamento:
            return fatorPercent * toRedutor(percentRepetida);
        }
    }

}
