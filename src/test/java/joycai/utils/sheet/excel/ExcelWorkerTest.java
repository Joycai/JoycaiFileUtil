package joycai.utils.sheet.excel;

import joycai.utils.TestObj;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class ExcelWorkerTest {

    @Test
    public void getWorkbookObJ() {
        try {
            ExcelWorker<TestObj> worker = new ExcelWorker(new FileInputStream("test.xlsx"), ExcelWorker.FILE_TYPE.XLSX);

            System.out.println(worker.firstLineIdx(0));

            TestObj obj = worker.readLineToObject(0, 4, new String[]{"col2","", "col1"}, TestObj.class);
            System.out.println(obj.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}