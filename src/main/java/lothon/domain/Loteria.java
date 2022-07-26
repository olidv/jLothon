package lothon.domain;

import java.io.File;

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
    public final int qtdBolas;
    public final int qtdSorteadas;

    private Loteria(String id, String nome, int qtdBolas, int qtdSorteadas) {
        this.id = id;
        this.nome = nome;
        this.qtdBolas = qtdBolas;
        this.qtdSorteadas = qtdSorteadas;
    }

    public File getDataFile(File dataPath) {
        String fileName = "D_" + this.nome + ".csv";
        return new File(dataPath, fileName);
    }

}
