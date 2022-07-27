package lothon.compute;

import lothon.util.Stats;

import static lothon.util.Eve.toRedutor;

public class ComputeMediana extends AbstractCompute {

    private double[] medianasPercentos;
    private double[] ultimasMedianasPercentos;
    private int valMedianaUltimoConcurso;
    private int valMedianaPenultimoConcurso;

    public ComputeMediana(int[][] sorteios, int qtdDezenas, int qtdBolas, int threshold) {
        super(sorteios, qtdDezenas, qtdBolas, threshold);
    }

    public void run() {
        // numero de itens/medianas vai depender do valor da ultima dezena:
        int qtdItens = (int) Math.round(Math.sqrt(this.qtdDezenas)) + 1;

        // efetua analise de todas as combinacoes de jogos da loteria:
        int[][] jogos = Stats.geraCombinacoes(this.qtdDezenas, this.qtdBolas);
        int qtdJogos = jogos.length;

        // calcula a mediana de cada combinacao de jogo:
        int[] medianasJogos = new int[qtdItens];
        for (final int[] jogo : jogos) {
            int valMediana = Stats.calcMediana(jogo);
            medianasJogos[valMediana]++;
        }

        // calcula o percentual das medianas:
        this.medianasPercentos = new double[qtdItens];
        for (int i = 0; i < qtdItens; i++) {
            this.medianasPercentos[i] = (medianasJogos[i] * 100.0d) / qtdJogos;
        }

        // calcula a mediana repetida de cada sorteio dos concursos:
        int[] ultimasMedianasRepetidas = new int[qtdItens];
        this.valMedianaUltimoConcurso = -1;
        this.valMedianaPenultimoConcurso = -1;
        for (final int[] sorteio : this.sorteios) {
            int valMediana = Stats.countPares(sorteio);
            // verifica se repetiu a mediana do ultimo concurso:
            if (valMediana == this.valMedianaUltimoConcurso) {
                ultimasMedianasRepetidas[valMediana]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.valMedianaPenultimoConcurso = this.valMedianaUltimoConcurso;
            this.valMedianaUltimoConcurso = valMediana;
        }

        // contabiliza o percentual das ultimas medianas:
        this.ultimasMedianasPercentos = new double[qtdItens];
        for (int i = 0; i < qtdItens; i++) {
            this.ultimasMedianasPercentos[i] = (ultimasMedianasRepetidas[i] * 100.0d) / this.qtdSorteios;
        }
    }

    public double eval(int ordinal, int[] jogo) {
        // a probabilidade de acerto depende do valor da mediana do jogo:
        int valMediana = Stats.calcMediana(jogo);
        double percent = this.medianasPercentos[valMediana];

        // ignora valores muito baixos de probabilidade:
        if (percent < this.threshold) {
            return 0;
        }

        // calcula o fator de percentual (metrica), para facilitar o calculo seguinte:
        double fatorPercent = toRedutor(percent);

        // verifica se esse jogo repetiu a mediana do ultimo e penultimo concursos:
        if (valMediana != this.valMedianaUltimoConcurso) {
            return fatorPercent;  // nao repetiu, ja pode pular fora
        } else if (valMediana == this.valMedianaPenultimoConcurso) {
            return fatorPercent * 0.1;  // pouco provavel de repetir mais de 2 ou 3 vezes
        }

        // se repetiu apenas o ultimo, obtem a probabilidade de repeticao da ultima mediana:
        double percentRepetida = this.ultimasMedianasPercentos[valMediana];
        if (percentRepetida < 1) {  // baixa probabilidade pode ser descartada
            return 0;
        } else {  // reduz a probabilidade porque esse jogo vai repetir a mediana:
            return fatorPercent * toRedutor(percentRepetida);
        }
    }

}
