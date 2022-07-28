package lothon.compute;

import lothon.domain.Loteria;

public abstract class AbstractCompute extends Thread {

    protected final Loteria loteria;
    protected final int[][] sorteios;
    protected final int threshold;

    // propriedades para auxiliar na computacao dos sorteios:
    protected final int qtdSorteios;
    protected final int qtdDezenas;
    protected final int qtdBolas;
    protected final int maxTopos;
    public int qtdZerados;

    public AbstractCompute(Loteria loteria, int[][] sorteios, int threshold) {
        this.loteria = loteria;
        this.sorteios = sorteios;
        this.threshold = threshold;

        // aproveita para inicializar as propriedades auxiliares:
        this.qtdSorteios = sorteios.length;
        this.qtdDezenas = loteria.qtdDezenas;
        this.qtdBolas = loteria.qtdBolas;
        this.maxTopos = loteria.maxTopos;
        this.qtdZerados = 0;
    }

    public abstract double eval(int ordinal, int[] jogo);

}
