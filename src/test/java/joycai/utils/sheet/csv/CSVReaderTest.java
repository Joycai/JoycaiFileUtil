package joycai.utils.sheet.csv;

import com.google.common.collect.Lists;
import joycai.utils.sheet.model.TestObj;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

class CSVReaderTest {

    private static final Logger logger = LoggerFactory.getLogger(CSVReaderTest.class);

    @BeforeEach
    void createDir() {
        File dic = new File("output");
        if (!dic.exists()) {
            dic.mkdir();
        }
    }

    @Test
    @Disabled
    void readCSV() {
        Assertions.assertDoesNotThrow(() -> {
            FileInputStream inputStream = new FileInputStream("test.csv");

            BeanReader<TestObj> reader = CSVReader.<TestObj>newBeanReader(inputStream, TestObj.class)
                    .setHeaderMap(headrs -> headrs)
                    .setCellProcessor(new CellProcessor[]{
                            new Optional(),
                            new Optional()
                    }).setPrintInfo(true);

            List<TestObj> result = reader.readCSV();
            result.forEach(it -> {
                logger.debug("{} {}", it.getCol1(), it.getCol2());
            });
        });
    }

    @Test
    void writeCSV() {
        Assertions.assertDoesNotThrow(() -> {
            TestObj testObj = new TestObj();
            testObj.setCol1("ttt");
            testObj.setCol2("tvvt");
            testObj.setF1(1f);
            testObj.setF2(2f);
            testObj.setI1(1);
            testObj.setI2(2);
            testObj.setDate(new Date());
            testObj.setBl1(false);
            FileOutputStream outputStream = new FileOutputStream(new File("output/out.csv"));
            CSVWriter.newBeanWriter(new OutputStreamWriter(outputStream), TestObj.class)
                    .addHeader(new String[]{"列1", "列2"})
                    .setCellProcessor(new CellProcessor[]{
                            new Optional(),
                            new Optional()
                    })
                    .writeFile(Lists.newArrayList(testObj), new String[]{"col1", "col2"});
        });
    }
}