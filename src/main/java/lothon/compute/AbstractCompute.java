package lothon.compute;

import lothon.domain.Loteria;

import static lothon.util.Eve.toRedutor;

public abstract class AbstractCompute extends Thread {

    protected final Loteria loteria;
    protected final int[][] jogos;
    protected final int[][] sorteios;
    protected final int min_threshold;

    // propriedades para auxiliar na computacao dos sorteios:
    protected final int qtdJogos;
    protected final int qtdSorteios;
    protected final int qtdDezenas;
    protected final int qtdBolas;
    protected final int maxTopos;
    public int qtdZerados;

    // propriedades para armazenar as estatisticas:
    protected double[] jogosPercentos;
    protected double[] ultimosSorteiosPercentos;
    protected int valorUltimoConcurso;
    protected int valorPenultimoConcurso;

    public AbstractCompute(Loteria loteria, int[][] jogos, int[][] sorteios, int threshold) {
        this.loteria = loteria;
        this.jogos = jogos;
        this.sorteios = sorteios;
        this.min_threshold = threshold;

        // aproveita para inicializar as propriedades auxiliares:
        this.qtdJogos = jogos.length;
        this.qtdSorteios = sorteios.length;
        this.qtdDezenas = loteria.qtdDezenas;
        this.qtdBolas = loteria.qtdBolas;
        this.maxTopos = loteria.maxTopos;
        this.qtdZerados = 0;
    }

    public abstract int rateJogo(int ordinal, int[] jogo);

    public double eval(int ordinal, int[] jogo) {
        // a probabilidade de acerto depende do numero de pares no jogo:
        int valorRate = this.rateJogo(ordinal, jogo);
        double percent = this.jogosPercentos[valorRate];

        // ignora valores muito baixos de probabilidade:
        if (percent < this.min_threshold) {
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        }

        // calcula o fator de percentual (metrica), para facilitar o calculo seguinte:
        double fatorPercent = toRedutor(percent);

        // verifica se esse jogo repetiu a paridade do ultimo e penultimo concursos:
        if (valorRate != this.valorUltimoConcurso) {
            return fatorPercent;  // nao repetiu, ja pode pular fora
        } else if (valorRate == this.valorPenultimoConcurso) {
            return fatorPercent * 0.1;  // pouco provavel de repetir mais de 2 ou 3 vezes
        }

        // se repetiu apenas o ultimo, obtem a probabilidade de repeticao da ultima paridade:
        double percentRepetida = this.ultimosSorteiosPercentos[valorRate];
        if (percentRepetida < 1) {  // baixa probabilidade pode ser descartada
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        } else {  // reduz a probabilidade porque esse jogo vai repetir a paridade:
            return fatorPercent * toRedutor(percentRepetida);
        }
    }

}
