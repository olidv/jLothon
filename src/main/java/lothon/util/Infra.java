package lothon.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

public final class Infra {

    private Infra() {}

    public static int[][] loadSorteios(Path csvPath) throws IOException {
        // efetua a leitura do arquivo CSV com sorteios da loteria:
        try (final CsvReader csvReader = CsvReader.builder().build(csvPath)) {
            List<List<String>> list_sorteios = new ArrayList<List<String>>();
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
            return null;
        }
    }

}
