package lothon.domain;

public class Quina extends Loteria {

    public Quina() {
        super("quina", "QUINA",'q', 80, 5, 24040016, 10);
    }

    public int[] getConsecutivasJogos() {
        return new int[]{18474840, 5342800, 216600, 5700, 76};
    }

    public int[] getEspacamentosJogos() {
        return new int[]{0, 2576, 20656, 68496, 156336, 290320, 472496, 700816, 969136, 1267216, 1580720, 1891216, 2176176, 2408976, 2558896, 2591120, 2466736, 2142736, 1572016, 703376, 0};
    }

    public int[] getMatriciaisJogos() {
        return new int[]{0, 0, 1693440, 8930880, 10997280, 2283120, 135296, 0, 0, 0, 0};
    }

    public int[] getMedianasJogos() {
        return new int[]{0, 0, 1428, 91574, 1195262, 5451657, 9819770, 6449336, 1027160, 3829};
    }

    public int[] getParidadesJogos() {
        return new int[]{658008, 3655600, 7706400, 7706400, 3655600, 658008};
    }

    public int[] getSequenciasJogos() {
        return new int[]{18474840, 5131900, 421800, 11400, 76};
    }

}
