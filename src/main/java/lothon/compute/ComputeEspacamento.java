package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

import static lothon.util.Eve.toRedutor;

public class ComputeEspacamento extends AbstractCompute {

    private double[] espacamentosPercentos;
    private double[] ultimosEspacamentosPercentos;
    private int qtdEspacosUltimoConcurso;
    private int qtdEspacosPenultimoConcurso;

    public ComputeEspacamento(Loteria loteria, int[][] sorteios, int threshold) {
        super(loteria, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;

        // numero maximo de espacos deve considerar que a soma dos espacos eh o total de dezenas:
        int qtdItens = this.qtdDezenas / (this.qtdBolas - 1);

        // efetua analise de todas as combinacoes de jogos da loteria:
        int[][] jogos = Stats.geraCombinacoes(this.qtdDezenas, this.qtdBolas);
        int qtdJogos = jogos.length;

        // contabiliza espacos em cada combinacao de jogo:
        int[] espacamentosJogos = Stats.newArrayInt(qtdItens);
        for (final int[] jogo : jogos) {
            int qtdEspacos = Stats.countEspacos(jogo);
            espacamentosJogos[qtdEspacos]++;
        }

        // contabiliza o percentual dos espacamentos:
        this.espacamentosPercentos = Stats.toPercentos(espacamentosJogos, qtdJogos);

        // contabiliza os espacos repetidos em cada sorteio dos concursos:
        int[] ultimosEspacamentosRepetidas = Stats.newArrayInt(qtdItens);
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
        this.ultimosEspacamentosPercentos = Stats.toPercentos(ultimosEspacamentosRepetidas, this.qtdSorteios);
    }

    public double eval(int ordinal, int[] jogo) {
        // a probabilidade de acerto depende do numero de espacos no jogo:
        int qtdEspacos = Stats.countEspacos(jogo);
        double percent = this.espacamentosPercentos[qtdEspacos];

        // ignora valores muito baixos de probabilidade:
        if (percent < this.threshold) {
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
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
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        } else {  // reduz a probabilidade porque esse jogo vai repetir o espacamento:
            return fatorPercent * toRedutor(percentRepetida);
        }
    }

}
