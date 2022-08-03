package lothon.domain;

import java.io.File;
import java.nio.file.Path;

public class Loteria {

    public static final Loteria DIA_DE_SORTE = new Loteria("diadesorte", "DIA-DE-SORTE", 31, 7, 10);
    public static final Loteria LOTOFACIL = new Loteria("lotofacil", "LOTOFACIL", 25, 15, 15);
    public static final Loteria DUPLA_SENA = new Loteria("duplasena", "DUPLA-SENA", 50, 6, 10);
    public static final Loteria QUINA = new Loteria("quina", "QUINA", 80, 5, 10);
    public static final Loteria MEGA_SENA = new Loteria("megasena", "MEGA-SENA", 60, 6, 10);
    public static final Loteria LOTOMANIA = new Loteria("lotomania", "LOTOMANIA", 100, 20, 20);
    public static final Loteria TIMEMANIA = new Loteria("timemania", "TIMEMANIA", 80, 7, 10);
    public static final Loteria SUPER_SETE = new Loteria("supersete", "SUPER-SETE", 10, 7, 10);

    public final String id;
    public final String nome;
    public final int qtdDezenas;
    public final int qtdBolas;
    public final int maxTopos;

    private Loteria(String id, String nome, int qtdDezenas, int qtdBolas, int maxTopos) {
        this.id = id;
        this.nome = nome;
        this.qtdDezenas = qtdDezenas;
        this.qtdBolas = qtdBolas;
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

}
