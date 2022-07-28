package lothon.compute;

import lothon.domain.Loteria;
import lothon.util.Stats;

import static lothon.util.Eve.toRedutor;

public class ComputeRecorrencia extends AbstractCompute {

    private double[] recorrenciasPercentos;
    private double[] ultimasRecorrenciasPercentos;
    private int qtdRecorrenciasUltimaConcurso;
    private int qtdRecorrenciasPenultimaConcurso;

    public ComputeRecorrencia(Loteria loteria, int[][] sorteios, int threshold) {
        super(loteria, sorteios, threshold);
    }

    public void run() {
        // inicializa variaveis de controle e monitoramento:
        this.qtdZerados = 0;

        // numero maximo de recorrencias corresponde ao numero de dezenas sorteadas:
        int qtdItens = this.qtdBolas;

        // contabiliza recorrencias de cada sorteio com todos os sorteios anteriores:
        int[] recorrenciasSorteios = Stats.newArrayInt(qtdItens);
        int[] ultimasRecorrenciasRepetidas = Stats.newArrayInt(qtdItens);
        this.qtdRecorrenciasUltimaConcurso = -1;
        this.qtdRecorrenciasPenultimaConcurso = -1;
        for (int i = 0; i < this.qtdSorteios; i++) {
            int qtdRecorrencias = Stats.maxRecorrencias(this.sorteios[i], this.sorteios, i);
            recorrenciasSorteios[qtdRecorrencias]++;

            // verifica se repetiu a recorrencia do ultima concurso:
            if (qtdRecorrencias == this.qtdRecorrenciasUltimaConcurso) {
                ultimasRecorrenciasRepetidas[qtdRecorrencias]++;
            }
            // atualiza ambos flags, para ultimo e penultimo concursos
            this.qtdRecorrenciasPenultimaConcurso = this.qtdRecorrenciasUltimaConcurso;
            this.qtdRecorrenciasUltimaConcurso = qtdRecorrencias;
        }

        // contabiliza o percentual das ultimas recorrencias:
        this.recorrenciasPercentos = Stats.toPercentos(recorrenciasSorteios, this.qtdSorteios);
        this.ultimasRecorrenciasPercentos = Stats.toPercentos(ultimasRecorrenciasRepetidas, this.qtdSorteios);
    }

    public double eval(int ordinal, int[] jogo) {
        // a probabilidade de acerto depende do numero de recorrencias no jogo:
        int qtdRecorrencias = Stats.maxRecorrencias(jogo, this.sorteios, -1);  // nao corresponde a nenhum sorteio...
        double percent = this.recorrenciasPercentos[qtdRecorrencias];

        // ignora valores muito baixos de probabilidade:
        if (percent < this.threshold) {
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        }

        // calcula o fator de percentual (metrica), para facilitar o calculo seguinte:
        double fatorPercent = toRedutor(percent);

        // verifica se esse jogo repetiu a recorrencia do ultimo e penultimo concursos:
        if (qtdRecorrencias != this.qtdRecorrenciasUltimaConcurso) {
            return fatorPercent;  // nao repetiu, ja pode pular fora
        } else if (qtdRecorrencias == this.qtdRecorrenciasPenultimaConcurso) {
            return fatorPercent * 0.5;  // aqui eh razoavel considerar que repete mais de 2 ou 3 vezes
        }

        // se repetiu apenas a ultima, obtem a probabilidade de repeticao da ultima recorrencia:
        double percentRepetida = this.ultimasRecorrenciasPercentos[qtdRecorrencias];
        if (percentRepetida < 1) {  // baixa probabilidade pode ser descartada
            this.qtdZerados++;  // contabiliza para posterior acompanhamento...
            return 0;
        } else {  // reduz a probabilidade porque esse jogo vai repetir a recorrencia:
            return fatorPercent * toRedutor(percentRepetida);
        }
    }

}
