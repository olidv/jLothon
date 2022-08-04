package lothon.domain;

public class MegaSena extends Loteria {

    public MegaSena() {
        super("megasena", "MEGA-SENA", 'm', 60, 6, 	50063860, 10);
    }

    public int[] getEspacamentosJogos() {
        return new int[]{0, 6510, 88585, 408785, 1160860, 2491685, 4423135, 6773960, 9081660, 10524360, 9842685, 5261635, 0};
    }

    public int[] getMatriciaisJogos() {
        return new int[]{0, 0, 151200, 6739200, 26822700, 13974750, 2262240, 113770, 0, 0, 0, 0, 0};
    }

    public int[] getMedianasJogos() {
        return new int[]{0, 0, 2514, 432949, 7263919, 24304221, 16712821, 1347392, 44};
    }

    public int[] getParidadesJogos() {
        return new int[]{593775, 4275180, 11921175, 16483600, 11921175, 4275180, 593775};
    }

    public int[] getSequenciasJogos() {
        return new int[]{28989675, 17393805, 3410550, 262350, 7425, 55};
    }

}
