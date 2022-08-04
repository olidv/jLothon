package lothon.domain;

public class Lotofacil extends Loteria {

    public Lotofacil() {
        super("lotofacil", "LOTOFACIL", 'l', 25, 15, 3268760, 15);
    }

    public int[] getConsecutivasJogos() {
        return new int[]{0, 9042, 402292, 990110, 866888, 519695, 266805, 125840, 55055, 22022, 7865, 2420, 605, 110, 11};
    }

    public int[] getEspacamentosJogos() {
        return new int[]{0, 3268760};
    }

    public int[] getMatriciaisJogos() {
        return new int[]{0, 0, 0, 0, 0, 0, 0, 11253, 269391, 1241300, 1266010, 419110, 59056, 2640, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    public int[] getMedianasJogos() {
        return new int[]{0, 0, 0, 2136157, 1132603, 0};
    }

    public int[] getParidadesJogos() {
        return new int[]{0, 0, 66, 2860, 38610, 226512, 660660, 1019304, 849420, 377520, 84942, 8580, 286, 0, 0, 0};
    }

    public int[] getSequenciasJogos() {
        return new int[]{0, 0, 0, 0, 1001, 22022, 165165, 566280, 990990, 924924, 462462, 120120, 15015, 770, 11};
    }

}
