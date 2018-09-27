import com.google.common.collect.Lists;
import joycai.utils.csv.BeanReader;
import joycai.utils.csv.CSVReader;
import joycai.utils.csv.CSVWriter;
import joycai.utils.file.JFileWriter;
import org.junit.Test;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class TestUtil {

    @Test
    public void testFileWriter() {

        try {
            JFileWriter.appendFile("testFile/test.txt").write("\"0545:12\",").close();
            JFileWriter.appendFile("testFile/test.txt").write("\"0555:12\"").close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert true;
    }

    @Test
    public void testWrite() {
        TestBean o1 = new TestBean();
        o1.setId(122);
        o1.setInfo("测试信息");
        o1.setPhone("59595959");

        TestBean o2 = new TestBean();
        o2.setId(123);
        o2.setInfo("测试信息2");
        o2.setPhone("1852444");

        try {
            CSVWriter.newBeanWriter(new FileWriter("testFile/testDoc.csv"), TestBean.class)
                    .setCellProcessor(new CellProcessor[]{
                            new NotNull(new ParseInt()),
                            new Optional(),
                            new Optional()})
                    .addHeader(new String[]{"id", "电话", "信息"})
                    .writeFile(Lists.newArrayList(o1,o2),new String[]{"id","phone","info"});
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert true;
    }

    @Test
    public void testRead() {
        try {
            List<TestBean> result= CSVReader.newBeanReader(new FileReader("testFile/testDoc.csv"), TestBean.class)
                    .setCellProcessor(new CellProcessor[]{
                            new NotNull(new ParseInt()),
                            new Optional(),
                            new Optional()
                    })
                    .setHeaderMap(new String[]{"id","info","phone"})
                    .readCSV();
            for (TestBean obj : result) {
                System.out.println(obj.getId() + " " + obj.getInfo() + " " + obj.getPhone());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert true;
    }

}
