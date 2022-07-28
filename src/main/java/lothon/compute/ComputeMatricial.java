package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

import static lothon.util.Eve.toRedutor;

public class ComputeMatricial extends AbstractCompute {

    private double[] matrizesPercentos;
    private double[] ultimasMatrizesPercentos;
    private int valMatrizUltimoConcurso;
    private int valMatrizPenultimoConcurso;

    public ComputeMatricial(Loteria loteria, int[][] sorteios, int threshold) {
        super(loteria, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;

        // numero maximo de itens/matrizes corresponde ao numero de dezenas sorteadas ao dobro (para colunas e para linhas):
        int qtdItens = this.qtdBolas * 2;

        // efetua analise de todas as combinacoes de jogos da loteria:
        int[][] jogos = Stats.geraCombinacoes(this.qtdDezenas, this.qtdBolas);
        int qtdJogos = jogos.length;

        // identifica o numero maximo de colunas e linhas de cada combinacao de jogo:
        int[] matrizesJogos = Stats.newArrayInt(qtdItens);
        for (final int[] jogo : jogos) {
            // calculo da matriz (maximo de colunas e linhas):
            int valMaxMatriz = Stats.maxColunas(jogo) + Stats.maxLinhas(jogo);
            matrizesJogos[valMaxMatriz]++;
        }

        // contabiliza o percentual das matrizes:
        this.matrizesPercentos = Stats.toPercentos(matrizesJogos, qtdJogos);

        // contabiliza as matrizes repetidas em cada sorteio dos concursos:
        int[] ultimasMatrizesRepetidas = Stats.newArrayInt(qtdItens);
        this.valMatrizUltimoConcurso = -1;
        this.valMatrizPenultimoConcurso = -1;
        for (final int[] sorteio : this.sorteios) {
            // calculo da matriz (maximo de colunas e linhas):
            int valMaxMatriz = Stats.maxColunas(sorteio) + Stats.maxLinhas(sorteio);
            // verifica se repetiu a matriz do ultimo concurso:
            if (valMaxMatriz == this.valMatrizUltimoConcurso) {
                ultimasMatrizesRepetidas[valMaxMatriz]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.valMatrizPenultimoConcurso = this.valMatrizUltimoConcurso;
            this.valMatrizUltimoConcurso = valMaxMatriz;
        }

        // contabiliza o percentual das ultimas matrizes:
        this.ultimasMatrizesPercentos = Stats.toPercentos(ultimasMatrizesRepetidas, this.qtdSorteios);
    }

    public double eval(int ordinal, int[] jogo) {
        // probabilidade de acerto depende do numero maximo de colunas e linhas do jogo:
        int valMaxMatriz = Stats.maxColunas(jogo) + Stats.maxLinhas(jogo);
        double percent = this.matrizesPercentos[valMaxMatriz];

        // ignora valores muito baixos de probabilidade:
        if (percent < this.threshold) {
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        }

        // calcula o fator de percentual (metrica), para facilitar o calculo seguinte:
        double fatorPercent = toRedutor(percent);

        // verifica se esse jogo repetiu a matriz do ultimo e penultimo concursos:
        if (valMaxMatriz != this.valMatrizUltimoConcurso) {
            return fatorPercent;  // nao repetiu, ja pode pular fora
        } else if (valMaxMatriz == this.valMatrizPenultimoConcurso) {
            return fatorPercent * 0.1;  // pouco provavel de repetir mais de 2 ou 3 vezes
        }

        // se repetiu apenas o ultimo, obtem a probabilidade de repeticao da ultima matriz:
        double percentRepetida = this.ultimasMatrizesPercentos[valMaxMatriz];
        if (percentRepetida < 1) {  // baixa probabilidade pode ser descartada
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        } else {  // reduz a probabilidade porque esse jogo vai repetir a matriz:
            return fatorPercent * toRedutor(percentRepetida);
        }
    }

}
