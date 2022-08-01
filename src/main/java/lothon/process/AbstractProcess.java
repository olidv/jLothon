package lothon.process;

import lothon.compute.*;
import lothon.domain.Jogo;
import lothon.domain.Loteria;
import lothon.util.Stats;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractProcess implements Runnable {

    protected final File dataDir;
    protected List<Jogo> jogosComputados;

    public AbstractProcess(File dataDir) {
        this.dataDir = dataDir;
        this.jogosComputados = null;
    }

    protected AbstractCompute[] getComputeChain(Loteria loteria, int[][] jogos, int[][] sorteios, int threshold) {
        return new AbstractCompute[]{
                new ComputeMatricial(loteria, jogos, sorteios, threshold),
                new ComputeEspacamento(loteria, jogos, sorteios, threshold),
                new ComputeSequencia(loteria, jogos, sorteios, threshold),
                new ComputeParidade(loteria, jogos, sorteios, threshold),
                new ComputeFrequencia(loteria, jogos, sorteios, threshold),
                new ComputeAusencia(loteria, jogos, sorteios, threshold),
                new ComputeMediana(loteria, jogos, sorteios, threshold),
                new ComputeRecorrencia(loteria, jogos, sorteios, threshold),
                new ComputeRepetencia(loteria, jogos, sorteios, threshold)
        };
    }

    protected AbstractCompute[] initComputeChain(Loteria loteria, int[][] jogos, int[][] sorteios, int threshold) {
        AbstractCompute[] computeChain = this.getComputeChain(loteria, jogos, sorteios, threshold);

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
