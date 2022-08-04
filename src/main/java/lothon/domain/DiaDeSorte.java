package lothon.domain;

public class DiaDeSorte extends Loteria {

    public DiaDeSorte() {
        super("diadesorte", "DIA-DE-SORTE", 'd', 31, 7, 2629575, 10);
    }

    public int[] getEspacamentosJogos() {
        return new int[]{0, 9570, 183142, 860021, 1458087, 118755};
    }

    public int[] getMatriciaisJogos() {
        return new int[]{0, 0, 0, 7560, 229068, 1319904, 874683, 185274, 13086, 0, 0, 0, 0, 0, 0};
    }

    public int[] getMedianasJogos() {
        return new int[]{0, 0, 3185, 643844, 1862453, 120093, 0};
    }

    public int[] getParidadesJogos() {
        return new int[]{11440, 120120, 458640, 828100, 764400, 360360, 80080, 6435};
    }

    public int[] getSequenciasJogos() {
        return new int[]{480700, 1062600, 796950, 253000, 34500, 1800, 25};
    }

}
