package joycai.utils.csv;

import joycai.utils.sheet.csv.BeanReader;
import joycai.utils.sheet.csv.CSVReader;
import joycai.utils.sheet.model.TestObj;
import org.junit.Test;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class CSVReaderTest {

    @Test
    public void newBeanReader() {
        try {
            FileInputStream inputStream = new FileInputStream("test.csv");

            BeanReader reader = CSVReader.newBeanReader(inputStream, TestObj.class)
                    .setHeaderMap(headrs -> headrs)
                    .setCellProcessor(new CellProcessor[]{
                            new Optional(),
                            new Optional()
                    }).setPrintInfo(true);

            List<TestObj> result = reader.readCSV();
            result.forEach(it -> {
                System.out.println(it.getCol1() + " " + it.getCol2());
            });
            assert true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}