package lothon.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import de.siegmar.fastcsv.writer.CsvWriter;
import lothon.domain.Jogo;

public final class Infra {

    public static int[][] loadSorteios(Path csvInput) {
        // efetua a leitura do arquivo CSV com sorteios da loteria:
        try (final CsvReader csvReader = CsvReader.builder().build(csvInput)) {
            List<List<String>> list_sorteios = new ArrayList<>();
            for (final CsvRow csvRow : csvReader) {
                list_sorteios.add(csvRow.getFields());
            }

            // verifica se os sorteios foram mesmo lidos:
            int qtdSorteios = list_sorteios.size();
            if (qtdSorteios == 0) {
                return null;
            }
            int qtdDezenas = list_sorteios.get(0).size();
            if (qtdDezenas == 0) {
                return null;
            }

            // repassa os sorteios das listas para os arrays (mais rapido):
            int[][] sorteios = new int[qtdSorteios][qtdDezenas];
            int lin = 0;
            for (final List<String> csvRow : list_sorteios) {
                int col = 0;
                for (final String field : csvRow) {
                    sorteios[lin][col++] = Integer.parseInt(field);
                }
                lin++;
            }
            return sorteios;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void saveJogos(Path csvOutput, List<Jogo> jogos) {
        // efetua a gravacao do arquivo CSV com os jogos computados da loteria:
        try (final CsvWriter csvWriter = CsvWriter.builder().build(csvOutput)) {
            for (final Jogo jogo : jogos) {
                final int[] row = jogo.dezenas;
                Iterable<String> fields = () -> new Iterator<>() {
                    private int pos=0;

                    public boolean hasNext() {
                        return pos < row.length;
                    }

                    public String next() {
                        return String.valueOf(row[pos++]);
                    }
                };
                csvWriter.writeRow(fields);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
