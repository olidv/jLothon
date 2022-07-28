package lothon.compute;

import static lothon.util.Eve.*;

import lothon.domain.Loteria;
import lothon.util.Stats;

public class ComputeParidade extends AbstractCompute {

    private double[] paridadesPercentos;
    private double[] ultimasParidadesPercentos;
    private int qtdParesUltimoConcurso;
    private int qtdParesPenultimoConcurso;

    public ComputeParidade(Loteria loteria, int[][] sorteios, int threshold) {
        super(loteria, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;

        // numero maximo de itens/paridades corresponde ao numero de dezenas sorteadas:
        int qtdItens = this.qtdBolas;

        // efetua analise de todas as combinacoes de jogos da loteria:
        int[][] jogos = Stats.geraCombinacoes(this.qtdDezenas, this.qtdBolas);
        int qtdJogos = jogos.length;

        // contabiliza pares (e impares) de cada combinacao de jogo:
        int[] paridadesJogos = Stats.newArrayInt(qtdItens);
        for (final int[] jogo : jogos) {
            int qtdPares = Stats.countPares(jogo);
            paridadesJogos[qtdPares]++;
        }

        // contabiliza o percentual das paridades:
        this.paridadesPercentos = Stats.toPercentos(paridadesJogos, qtdJogos);

        // contabiliza os pares (e impares) repetidos de cada sorteio dos concursos:
        int[] ultimasParidadesRepetidas = Stats.newArrayInt(qtdItens);
        this.qtdParesUltimoConcurso = -1;
        this.qtdParesPenultimoConcurso = -1;
        for (final int[] sorteio : this.sorteios) {
            int qtdPares = Stats.countPares(sorteio);
            // verifica se repetiu a paridade do ultimo concurso:
            if (qtdPares == this.qtdParesUltimoConcurso) {
                ultimasParidadesRepetidas[qtdPares]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.qtdParesPenultimoConcurso = this.qtdParesUltimoConcurso;
            this.qtdParesUltimoConcurso = qtdPares;
        }

        // contabiliza o percentual das ultimas paridades:
        this.ultimasParidadesPercentos = Stats.toPercentos(ultimasParidadesRepetidas, this.qtdSorteios);
    }

    public double eval(int ordinal, int[] jogo) {
        // a probabilidade de acerto depende do numero de pares no jogo:
        int qtdPares = Stats.countPares(jogo);
        double percent = this.paridadesPercentos[qtdPares];

        // ignora valores muito baixos de probabilidade:
        if (percent < this.threshold) {
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        }

        // calcula o fator de percentual (metrica), para facilitar o calculo seguinte:
        double fatorPercent = toRedutor(percent);

        // verifica se esse jogo repetiu a paridade do ultimo e penultimo concursos:
        if (qtdPares != this.qtdParesUltimoConcurso) {
            return fatorPercent;  // nao repetiu, ja pode pular fora
        } else if (qtdPares == this.qtdParesPenultimoConcurso) {
            return fatorPercent * 0.1;  // pouco provavel de repetir mais de 2 ou 3 vezes
        }

        // se repetiu apenas o ultimo, obtem a probabilidade de repeticao da ultima paridade:
        double percentRepetida = this.ultimasParidadesPercentos[qtdPares];
        if (percentRepetida < 1) {  // baixa probabilidade pode ser descartada
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        } else {  // reduz a probabilidade porque esse jogo vai repetir a paridade:
            return fatorPercent * toRedutor(percentRepetida);
        }
    }

}
