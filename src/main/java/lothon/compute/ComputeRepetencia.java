package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

import static lothon.util.Eve.toRedutor;

public class ComputeRepetencia extends AbstractCompute {

    private double[] repetenciasPercentos;
    private double[] ultimasRepetenciasPercentos;
    private int qtdRepetenciasUltimaConcurso;
    private int qtdRepetenciasPenultimaConcurso;

    // estrutura para avaliacao de jogos combinados da loteria:
    private int[] ultimoSorteio;

    public ComputeRepetencia(Loteria loteria, int[][] sorteios, int threshold) {
        super(loteria, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;
        this.ultimoSorteio = this.sorteios[qtdSorteios - 1];

        // numero maximo de repetencias corresponde ao numero de dezenas sorteadas:
        int qtdItens = this.qtdBolas;

        // contabiliza repetencias de cada sorteio com todos os sorteios anteriores:
        int[] repetenciasSorteios = Stats.newArrayInt(qtdItens);
        int[] ultimasRepetenciasRepetidas = Stats.newArrayInt(qtdItens);
        this.qtdRepetenciasUltimaConcurso = -1;
        this.qtdRepetenciasPenultimaConcurso = -1;
        int[] sorteioAnterior = this.sorteios[0];
        for (int i = 1; i < this.qtdSorteios; i++) {
            int[] sorteio = this.sorteios[i];
            int qtdRepetencias = Stats.countRepetencias(sorteio, sorteioAnterior);
            repetenciasSorteios[qtdRepetencias]++;
            sorteioAnterior = sorteio;  // atualiza o ultimo sorteio para a proxima iteracao...

            // verifica se repetiu a repetencia do ultima concurso:
            if (qtdRepetencias == this.qtdRepetenciasUltimaConcurso) {
                ultimasRepetenciasRepetidas[qtdRepetencias]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.qtdRepetenciasPenultimaConcurso = this.qtdRepetenciasUltimaConcurso;
            this.qtdRepetenciasUltimaConcurso = qtdRepetencias;
        }

        // contabiliza o percentual das ultimas repetencias:
        this.repetenciasPercentos = Stats.toPercentos(repetenciasSorteios, this.qtdSorteios);
        this.ultimasRepetenciasPercentos = Stats.toPercentos(ultimasRepetenciasRepetidas, this.qtdSorteios);
    }

    public double eval(int ordinal, int[] jogo) {
        // a probabilidade de acerto depende do numero de repetencias no jogo:
        int qtdRepetencias = Stats.countRepetencias(jogo, this.ultimoSorteio);
        double percent = this.repetenciasPercentos[qtdRepetencias];

        // ignora valores muito baixos de probabilidade:
        if (percent < this.threshold) {
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        }

        // calcula o fator de percentual (metrica), para facilitar o calculo seguinte:
        double fatorPercent = toRedutor(percent);

        // verifica se esse jogo repetiu a repetencia do ultimo e penultimo concursos:
        if (qtdRepetencias != this.qtdRepetenciasUltimaConcurso) {
            return fatorPercent;  // nao repetiu, ja pode pular fora
        } else if (qtdRepetencias == this.qtdRepetenciasPenultimaConcurso) {
            return fatorPercent * 0.1;  // pouco provavel de repetir mais de 2 ou 3 vezes
        }

        // se repetiu apenas a ultima, obtem a probabilidade de repeticao da ultima repetencia:
        double percentRepetida = this.ultimasRepetenciasPercentos[qtdRepetencias];
        if (percentRepetida < 1) {  // baixa probabilidade pode ser descartada
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        } else {  // reduz a probabilidade porque esse jogo vai repetir a repetencia:
            return fatorPercent * toRedutor(percentRepetida);
        }
    }

}
