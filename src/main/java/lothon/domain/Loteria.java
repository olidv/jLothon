package lothon.domain;

import java.io.File;
import java.nio.file.Path;

public class Loteria {

    public static final Loteria DIA_DE_SORTE = new Loteria("diadesorte", "DIA-DE-SORTE", 31, 7);
    public static final Loteria LOTOFACIL = new Loteria("lotofacil", "LOTOFACIL", 25, 15);
    public static final Loteria DUPLA_SENA = new Loteria("duplasena", "DUPLA-SENA", 50, 6);
    public static final Loteria QUINA = new Loteria("quina", "QUINA", 80, 5);
    public static final Loteria MEGA_SENA = new Loteria("megasena", "MEGA-SENA", 60, 6);
    public static final Loteria LOTOMANIA = new Loteria("lotomania", "LOTOMANIA", 100, 20);
    public static final Loteria TIMEMANIA = new Loteria("timemania", "TIMEMANIA", 80, 7);
    public static final Loteria SUPER_SETE = new Loteria("supersete", "SUPER-SETE", 10, 7);

    public final String id;
    public final String nome;
    public final int qtdDezenas;
    public final int qtdBolas;

    private Loteria(String id, String nome, int qtdDezenas, int qtdBolas) {
        this.id = id;
        this.nome = nome;
        this.qtdDezenas = qtdDezenas;
        this.qtdBolas = qtdBolas;
    }

    public Path getCsvInput(File dataDir) {
        String fileName = "D_" + this.nome + ".csv";
        return new File(dataDir, fileName).toPath();
    }

    public Path getCsvOuput(File dataDir) {
        String fileName = "JC_" + this.nome + ".csv";
        return new File(dataDir, fileName).toPath();
    }

}
