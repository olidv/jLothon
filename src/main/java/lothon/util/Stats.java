package lothon.util;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Stats {

    // --- CALCULOS ESTATISTICOS ----------------------------------------------

    public static int[] getToposReverse(int[] valores, int qtdTopos) {
        // valida os parametros:
        if (valores == null || valores.length == 0) {
            return null;
        }

        // identifica os valores mapeados para cada dezena (indice do array):
        Map<Integer, Integer> dictDezenas = new HashMap<>();
        for (int key = 0; key < valores.length; key++) {
            dictDezenas.put(key, valores[key]);
        }

        // converte o dicionario para uma lista com as dezenas em ordem reversa pelo valor:
        return dictDezenas.entrySet().stream()
                          .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
                          .mapToInt(Map.Entry::getKey)
                          .limit(qtdTopos).toArray();
    }

    public static int[] calcToposFrequencia(int[][] sorteios, int qtdDezenas, int qtdTopos) {
        return calcToposFrequencia(Arrays.asList(sorteios), qtdDezenas, qtdTopos);
    }

    public static int[] calcToposFrequencia(List<int[]> sorteios, int qtdDezenas, int qtdTopos) {
        // valida os parametros:
        if (sorteios == null || sorteios.size() == 0 || qtdDezenas == 0 || qtdTopos == 0) {
            return null;
        }

        // extrai as frequencias de todas as dezenas ate o concurso atual:
        int[] frequenciasDezenas = newArrayInt(qtdDezenas);
        for (final int[] sorteio: sorteios)
            // registra a frequencia geral de todas as bolas dos concursos anteriores:
            for (final int dezena: sorteio)
                frequenciasDezenas[dezena]++;

        // identifica as frequencias das dezenas em ordem reversa da ocorrencia ate o ultimo sorteio
        // e extrai o topo do ranking com as dezenas com maior frequencia:
        return getToposReverse(frequenciasDezenas, qtdTopos);
    }

    public static int[] calcToposAusencia(int[][] sorteios, int qtdDezenas, int qtdTopos) {
        return calcToposAusencia(Arrays.asList(sorteios), qtdDezenas, qtdTopos);
    }

    public static int[] calcToposAusencia(List<int[]> sorteios, int qtdDezenas, int qtdTopos) {
        // valida os parametros:
        if (sorteios == null || sorteios.size() == 0 || qtdDezenas == 0 || qtdTopos == 0) {
            return null;
        }

        // inicializa o array contador das dezenas:
        int[] ausenciasDezenas = newArrayInt(qtdDezenas, -1);
        ausenciasDezenas[0] = 0;  // para ignorar o index-0, que nao corresponde a nenhuma dezena...

        // contabiliza o numero de concursos em que cada dezena ficou ausente ate ser sorteada:
        int qtdConcursos = 0;
        for (int i = sorteios.size() - 1; i >= 0; i--) {
            int[] sorteio = sorteios.get(i);
            // registra o sorteio da dezena com o numero de concursos em que ficou ausente:
            for (final int dezena : sorteio)
                if (ausenciasDezenas[dezena] == -1)  // se a dezena ainda estiver ausente:
                    ausenciasDezenas[dezena] = qtdConcursos;

            // vai continuar processando enquanto não tiver contado todas as dezenas:
            if (Arrays.stream(ausenciasDezenas).anyMatch(n -> n == -1))
                qtdConcursos++;
            else  // nao tendo mais dezenas a processar, ja pode pular fora:
                break;
        }

        // identifica as ausencias das dezenas em ordem reversa do atraso ate o ultimo sorteio
        // e extrai o topo do ranking com as dezenas com maior ausencia e retorna:
        return getToposReverse(ausenciasDezenas, qtdTopos);
    }

    // --- CALCULOS DE DEZENAS E JOGOS ----------------------------------------

    public static int getColuna(int dezena) {
        return dezena % 10;
    }

    public static int getLinha(int dezena) {
        return ((dezena - 1) / 10);
    }

    public static int maxColunas(int[] dezenas) {
        // valida o parametro:
        if (dezenas == null || dezenas.length == 0) {
            return 0;
        }

        // prepara contador de dezenas por coluna
        int[] colunas = newArrayInt(9);

        // verifica quantas dezenas existem em cada coluna:
        for (final int dezena : dezenas)
            colunas[getColuna(dezena)]++;

        // informa o maior numero de dezenas encontradas em determinada coluna:
        return Arrays.stream(colunas).max().getAsInt();
    }

    public static int maxLinhas(int[] dezenas) {
            // valida o parametro:
            if (dezenas == null || dezenas.length == 0) {
                return 0;
            }

        // prepara contador de dezenas por linha
        int[] linhas = newArrayInt(9);

        // verifica quantas dezenas existem em cada linha:
        for (final int dezena : dezenas)
            linhas[getLinha(dezena)]++;

        // informa o maior numero de dezenas encontradas em determinada linha:
        return Arrays.stream(linhas).max().getAsInt();
    }

    public static int maxRecorrencias(int[] dezenas, int[][] sorteios, int idxSorteio) {
        // valida os parametros:
        if (dezenas == null || dezenas.length == 0
                || sorteios == null || sorteios.length == 0) {
            return 0;
        }

        // percorre todos os sorteios e retorna o numero maximo de recorrencias de [dezenas]:
        int qtdMaxRecorrencias = 0;
        for (int i = 0; i < sorteios.length; i++) {
            // nao deixa comparar com o mesmo sorteio:
            if (i == idxSorteio) continue;

            int qtdRecorrencias = countRepetencias(dezenas, sorteios[i]);
            if (qtdRecorrencias > qtdMaxRecorrencias)
                qtdMaxRecorrencias = qtdRecorrencias;
        }

        return qtdMaxRecorrencias;
    }

    public static int countRepetencias(int[] dezenas1, int[] dezenas2) {
        // valida os parametros:
        if (dezenas1 == null || dezenas1.length == 0
                || dezenas2 == null || dezenas2.length == 0) {
            return 0;
        }

        // calcula o numero de dezenas repetidas entre os dois jogos:
        int qtdRepete = 0;
        for (final int dezena1 : dezenas1) {
            for (final int dezena2 : dezenas2) {
                if (dezena1 == dezena2) {
                    qtdRepete++;
                    break;  // nao precisa testar as demais dezenas se ja achou alguma...
                }
            }
        }

        return qtdRepete;
    }

    public static int calcMediana(int[] dezenas) {
        // valida o parametro:
        if (dezenas == null || dezenas.length == 0) {
            return 0;
        }

        // primeiro calcula a soma das raizes dos numeros:
        int qtdDezenas = dezenas.length;
        double soma = 0.0;
        for (final int dezena : dezenas) {
            soma += Math.sqrt(dezena);
        }

        return (int) Math.round(soma / qtdDezenas);
    }

    public static int countEspacos(int[] dezenas) {
        // valida o parametro:
        if (dezenas == null || dezenas.length == 0) {
            return 0;
        }

        // calcula o espacamento medio entre cada bola:
        int qtdLacunas = dezenas.length - 1;
        int soma = 0;
        int dezAnterior = dezenas[0];
        for (final int dezena : dezenas) {
            soma += dezena - dezAnterior;
            dezAnterior = dezena;
        }

        return soma / qtdLacunas;
    }

    public static int countSequencias(int[] dezenas) {
        // valida o parametro:
        if (dezenas == null || dezenas.length == 0) {
            return 0;
        }

        // considera que o array ja esta ordenado:
        int qtdSequencias = 0;
        int seqPosterior = -1;
        for (final int dezena : dezenas) {
            if (dezena == seqPosterior) {
                qtdSequencias++;
            }
            seqPosterior = dezena + 1;
        }

        return qtdSequencias;
    }

    public static int countPares(int[] dezenas) {
        // valida o parametro:
        if (dezenas == null || dezenas.length == 0) {
            return 0;
        }

        // contabiliza as dezenas pares:
        int qtdPares = 0;
        for (final int dezena : dezenas) {
            if (dezena % 2 == 0)
                qtdPares++;
        }

        return qtdPares;
    }

    // --- CONVERSAO DE VALORES -----------------------------------------------

    public static double[] toPercentos(int[] valores, int qtd) {
        // valida os parametros:
        if (valores == null || valores.length == 0 || qtd == 0) {
            return null;
        }

        // contabiliza o percentual das matrizes:
        int qtdValores = valores.length;
        double[] percentos = new double[qtdValores];
        for (int i = 0; i < qtdValores; i++)
            percentos[i] = (valores[i] * 100.0d) / qtd;

        return percentos;
    }

    // --- ESTRUTURAS DE DADOS ------------------------------------------------

    public static int[] newArrayInt(int qtdItems) {
        return newArrayInt(qtdItems, 0);
    }

    public static int[] newArrayInt(int qtdItems, int valDefault) {
        // adiciona 1 para ignorar zero-index:
        int [] arranjo = new int[qtdItems + 1];
        Arrays.fill(arranjo, valDefault);

        return arranjo;
    }

    public static double[] newArrayDouble(int qtdItems) {
        return newArrayDouble(qtdItems, 0.0d);
    }

    public static double[] newArrayDouble(int qtdItems, double valDefault) {
        // adiciona 1 para ignorar zero-index:
        double [] arranjo = new double[qtdItems + 1];
        Arrays.fill(arranjo, valDefault);

        return arranjo;
    }

    public static String[] newArrayString(int qtdItems) {
        return newArrayString(qtdItems, "");
    }

    public static String[] newArrayString(int qtdItems, String valDefault) {
        // adiciona 1 para ignorar zero-index:
        String[] arranjo = new String[qtdItems + 1];
        Arrays.fill(arranjo, valDefault);

        return arranjo;
    }

    // --- COMBINATORIA -------------------------------------------------------

    public static BigInteger fatorial(final int n) {
        if (n <= 0) return BigInteger.ONE; // fatorial de 0 eh 1.

        BigInteger result = BigInteger.valueOf(n);
        for (int i = n - 1; i > 0; i--)
            result = result.multiply(BigInteger.valueOf(i));
        return result;
    }

    public static BigInteger combinacoes(final int n, final int p) {
        if (n <= p) return BigInteger.ONE;

        BigInteger nFat = fatorial(n);
        BigInteger pFat = fatorial(p);
        BigInteger nMinusPFat = fatorial(n - p);
        return nFat.divide(pFat.multiply(nMinusPFat));
    }

    public static int[][] geraCombinacoes(final int n, final int p) {
        final int c = combinacoes(n, p).intValue();
        final int[][] m = new int[c][p];
        final int[] vN = new int[p];

        // gera as combinacoes de jogos:
        for (int i = 0; i < p; i++) {
            vN[i] = i;
            m[0][i] = i;
        }

        for (int i = 1; i < c; i++) {
            for (int j = p - 1; j >= 0; j--) {
                if (vN[j] < (n - p + j)) {
                    vN[j]++;
                    for (int k = j + 1; k < p; k++) {
                        vN[k] = vN[j] + k - j;
                    }
                    System.arraycopy(vN, 0, m[i], 0, p);
                    break;
                }
            }
        }

        // normaliza os jogos, que estao com as dezenas iniciando em zero:
        for (int i = 0; i < c; i++) {
            for (int j = 0; j < p; j++) {
                m[i][j]++;
            }
        }

        return m;
    }

}
