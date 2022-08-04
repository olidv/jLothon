package lothon.domain;

import java.io.File;
import java.nio.file.Path;

public abstract class Loteria {

    public final String id;
    public final String nome;
    public final char tag;
    public final int qtdDezenas;
    public final int qtdBolas;
    public final int qtdJogos;
    public final int maxTopos;

    protected Loteria(String id, String nome, char tag, int qtdDezenas, int qtdBolas, int qtdJogos, int maxTopos) {
        this.id = id;
        this.nome = nome;
        this.tag = tag;
        this.qtdDezenas = qtdDezenas;
        this.qtdBolas = qtdBolas;
        this.qtdJogos = qtdJogos;
        this.maxTopos = maxTopos;
    }

    public Path getCsvInput(File dataDir) {
        String fileName = "D_" + this.nome + ".csv";
        return new File(dataDir, fileName).toPath();
    }

    public Path getCsvOuput(File dataDir) {
        String fileName = "JC_" + this.nome + ".csv";
        return new File(dataDir, fileName).toPath();
    }

    public abstract int[] getEspacamentosJogos();

    public abstract int[] getMatriciaisJogos();

    public abstract int[] getMedianasJogos();

    public abstract int[] getParidadesJogos();

    public abstract int[] getSequenciasJogos();

}
