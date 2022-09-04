package lothon.domain;

public class MaisMilionaria extends Loteria {

    public MaisMilionaria() {
        super("maismilionaria", "MAIS-MILIONARIA", 'r', 50, 6, 15890700, 10);
    }

    public int[] getConsecutivasJogos() {
        return new int[]{8145060, 7016955, 682110, 44550, 1980, 45};
    }

    public int[] getEspacamentosJogos() {
        return new int[]{0, 5250, 69825, 312525, 852100, 1729175, 2828125, 3798950, 3979150, 2315600, 0};
    }

    public int[] getMatriciaisJogos() {
        return new int[]{0, 0, 0, 1701000, 7810800, 5304900, 1014900, 59100, 0, 0, 0, 0, 0};
    }

    public int[] getMedianasJogos() {
        return new int[]{0, 0, 2514, 390497, 4739380, 8722526, 2027854, 7929};
    }

    public int[] getParidadesJogos() {
        return new int[]{177100, 1328250, 3795000, 5290000, 3795000, 1328250, 177100};
    }

    public int[] getSequenciasJogos() {
        return new int[]{8145060, 6108795, 1489950, 141900, 4950, 45};
    }

}
