package joycai.utils.sheet.excel;

import com.google.common.collect.Lists;
import joycai.utils.file.JFileOutputUtil;
import joycai.utils.sheet.model.TestObj;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

class ExcelReaderTest {

    final static String path = "output";

    @BeforeEach
    public void createDir() {
        File dic = new File(path);
        if (!dic.exists()) {
            dic.mkdir();
        }
    }

    @Test
    @Disabled
    void testRead() {
        Assertions.assertDoesNotThrow(() -> {
            ExcelReader reader = new ExcelReader(new FileInputStream(path + "/test.xlsx"), ExcelType.XLSX);
            System.out.println(reader.firstLineIdx(0));
            TestObj obj = (TestObj) reader.readLineToObject(0, 5, new String[]{"col2", "i1", "col1"}, TestObj.class);
            System.out.println(obj);
            reader.close();
        });

    }

    @Test
    void testWrite() {

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
                    new String[]{"date", "col2", "f1", "f2", "i1", "i2", "bl1"},
                    new String[]{"日期", "", "浮点1", "浮点2", "整数1", "整数2"});
            writer.writeSheet("testSheet2",
                    Lists.newArrayList(testObj),
                    TestObj.class,
                    new String[]{"date", "col2", "f1", "f2", "i1", "i2", "bl1"},
                    new String[]{"日期", "", "浮点1", "浮点2", "整数1", "整数2"});
            byte[] byteArray = writer.exportExcel();

            JFileOutputUtil.newJFileOutputUtil(path + "/export.xlsx").writeAndClose(byteArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}