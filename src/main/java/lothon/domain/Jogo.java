package lothon.domain;

public class Jogo {

    public final int ordinal;
    public final int[] dezenas;
    public final double fator;

    public Jogo(int ordinal, int[] dezenas) {
        this.ordinal = ordinal;
        this.dezenas = dezenas;
        this.fator = 0.0d;
    }

    public Jogo(int ordinal, int[] dezenas, double fator) {
        this.ordinal = ordinal;
        this.dezenas = dezenas;
        this.fator = fator;
    }
}
