package lothon.util;

import java.math.BigInteger;

public final class Stats {

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
