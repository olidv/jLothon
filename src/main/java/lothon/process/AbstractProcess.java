package lothon.process;

import lothon.compute.*;
import lothon.domain.Jogo;
import lothon.domain.Loteria;

import java.io.File;
import java.util.List;

public abstract class AbstractProcess implements Runnable {

    public final Loteria loteria;
    protected final File dataDir;
    protected final int min_threshold;
    protected List<Jogo> jogosComputados;

    public AbstractProcess(Loteria loteria, File dataDir, int threshold) {
        this.loteria = loteria;
        this.dataDir = dataDir;
        this.min_threshold = threshold;
        this.jogosComputados = null;
    }

    protected AbstractCompute[] getComputeChain(int[][] sorteios, int threshold) {
        return new AbstractCompute[]{
                new ComputeMatricial(this.loteria, sorteios, threshold),
                new ComputeEspacamento(this.loteria, sorteios, threshold),
                new ComputeSequencia(this.loteria, sorteios, threshold),
                new ComputeParidade(this.loteria, sorteios, threshold),
                new ComputeAusencia(this.loteria, sorteios, threshold),
                new ComputeFrequencia(this.loteria, sorteios, threshold),
                new ComputeMediana(this.loteria, sorteios, threshold),
                new ComputeRepetencia(this.loteria, sorteios, threshold),
                new ComputeConsecutiva(this.loteria, sorteios, threshold)
        };
    }

    protected AbstractCompute[] initComputeChain(int[][] sorteios, int threshold) {
        AbstractCompute[] computeChain = this.getComputeChain(sorteios, threshold);

        for (final AbstractCompute compute : computeChain)
            try {
                compute.start();
                compute.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        return computeChain;
    }

}
