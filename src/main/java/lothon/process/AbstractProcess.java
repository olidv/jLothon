package lothon.process;

import lothon.compute.*;
import lothon.domain.Jogo;
import lothon.domain.Loteria;
import lothon.util.Stats;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractProcess extends Thread {

    protected final File dataDir;
    protected List<Jogo> jogosComputados;

    public AbstractProcess(File dataDir) {
        this.dataDir = dataDir;
        this.jogosComputados = null;
    }

    protected AbstractCompute[] getComputeChain(Loteria loteria, int[][] sorteios, int threshold) {
        return new AbstractCompute[]{
                new ComputeMatricial(loteria, sorteios, threshold),
                new ComputeEspacamento(loteria, sorteios, threshold),
                new ComputeSequencia(loteria, sorteios, threshold),
                new ComputeParidade(loteria, sorteios, threshold),
                new ComputeFrequencia(loteria, sorteios, threshold),
                new ComputeAusencia(loteria, sorteios, threshold),
                new ComputeMediana(loteria, sorteios, threshold),
                new ComputeRecorrencia(loteria, sorteios, threshold),
                new ComputeRepetencia(loteria, sorteios, threshold)
        };
    }

    protected AbstractCompute[] initComputeChain(Loteria loteria, int[][] sorteios, int threshold) {
        AbstractCompute[] computeChain = this.getComputeChain(loteria, sorteios, threshold);

        for (final AbstractCompute compute : computeChain)
            try {
                compute.start();
                compute.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        return computeChain;
    }

    protected List<Jogo> relacionarJogos(int qtdMaxRecorrencias) {
        List<Jogo> jogosSorteados = new ArrayList<>();

        jogosSorteados.add(this.jogosComputados.get(0));
        int qtdJogos = this.jogosComputados.size();
        for (int i = 1; i < qtdJogos; i++) {
            final Jogo jogo = this.jogosComputados.get(i);

            int maxRepetidas = -1;
            for (final Jogo sorteado : jogosSorteados) {
                int qtdRepetidas = Stats.countRepetencias(jogo.dezenas, sorteado.dezenas);
                if (qtdRepetidas > maxRepetidas)
                    maxRepetidas = qtdRepetidas;
            }
            if (maxRepetidas <= qtdMaxRecorrencias)
                jogosSorteados.add(jogo);
        }

        return jogosSorteados;
    }

}
