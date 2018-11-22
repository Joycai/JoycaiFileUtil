package joycai.utils.sheet.csv;

import com.google.common.collect.Lists;
import joycai.utils.sheet.csv.BeanReader;
import joycai.utils.sheet.csv.CSVReader;
import joycai.utils.sheet.model.TestObj;
import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.*;
import java.util.Date;
import java.util.List;

public class CSVReaderTest {

    @Before
    public void createDir(){
        File dic = new File("output");
        if (!dic.exists()) {
            dic.mkdir();
        }
    }

    @Test
    public void readCSV() {
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

    @Test
    public void writeCSV(){
        try {
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
                    .addHeader(new String[]{"列1","列2"})
                    .setCellProcessor(new CellProcessor[]{
                            new Optional(),
                            new Optional()
                    })
                    .writeFile(Lists.newArrayList(testObj),new String[]{"col1","col2"});
            assert true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}