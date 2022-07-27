package lothon.compute;

public abstract class AbstractCompute extends Thread {

    protected final int[][] sorteios;
    protected final int qtdSorteios;
    protected final int qtdDezenas;
    protected final int qtdBolas;
    protected final int threshold;

    public AbstractCompute(int[][] sorteios, int qtdDezenas, int qtdBolas, int threshold) {
        this.sorteios = sorteios;
        this.qtdSorteios = (sorteios == null) ? 0 : sorteios.length;
        this.qtdDezenas = qtdDezenas;
        this.qtdBolas = qtdBolas;
        this.threshold = threshold;
    }

    public abstract double eval(int ordinal, int[] jogo);

}
