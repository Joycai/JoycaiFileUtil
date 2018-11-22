package joycai.utils.sheet.excel;

import com.google.common.collect.Lists;
import joycai.utils.file.JFileOutputUtil;
import joycai.utils.sheet.model.TestObj;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

public class ExcelReaderTest {

    final static String path = "output";

    @Before
    public void createDir(){
        File dic = new File(path);
        if (!dic.exists()) {
            dic.mkdir();
        }
    }

    @Test
    public void testRead() {
        try {
            ExcelReader reader = new ExcelReader(new FileInputStream(path+"/test.xlsx"), ExcelType.XLSX);

            System.out.println(reader.firstLineIdx(0));

            TestObj obj = (TestObj) reader.readLineToObject(0, 4, new String[]{"col2", "", "col1"}, TestObj.class);
            reader.close();
            System.out.println(obj.toString());
            assert true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWrite(){

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
            ExcelWrite writer = new ExcelWrite(ExcelType.XLSX);
            writer.writeSheet("testSheet",
                    Lists.newArrayList(testObj),
                    TestObj.class,
                    new String[]{"date", "col2", "f1", "f2", "i1", "i2","bl1"},
                    new String[]{"日期", "", "浮点1", "浮点2", "整数1", "整数2"});
            writer.writeSheet("testSheet2",
                    Lists.newArrayList(testObj),
                    TestObj.class,
                    new String[]{"date", "col2", "f1", "f2", "i1", "i2","bl1"},
                    new String[]{"日期", "", "浮点1", "浮点2", "整数1", "整数2"});
            byte[] byteArray = writer.exportExcel();

            JFileOutputUtil.newJFileOutputUtil(path + "/export.xlsx").writeAndClose(byteArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}