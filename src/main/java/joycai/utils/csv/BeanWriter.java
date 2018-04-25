package joycai.utils.csv;

import joycai.utils.common.TypeNameList;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;

public class BeanWriter<T> {

    private final ICsvBeanWriter csvWriter;

    final Class<T> clazz;

    String[] header = null;
    private CellProcessor[] cellProcessors;


    public BeanWriter(final Writer writer, Class<T> clazz) throws IOException {
        csvWriter = new CsvBeanWriter(writer,
                CsvPreference.STANDARD_PREFERENCE);
        this.clazz = clazz;
    }

    public BeanWriter<T> addHeader(String[] header) {
        this.header = header;
        return this;
    }

    /**
     * 设置单元处理模式
     *
     * @param cellProcessors
     * @return
     */
    public BeanWriter<T> setCellProcessor(CellProcessor[] cellProcessors) {
        this.cellProcessors = cellProcessors;
        return this;
    }

    public void writeFile(List<T> dataList,String[] fieldsArray) throws NoSuchFieldException, IOException {
        if (null != header) {
            csvWriter.writeHeader(header);
        }
        if (null!=cellProcessors) {
            for (T data : dataList) {
                csvWriter.write(data, fieldsArray, cellProcessors);
            }
        }
        if (csvWriter != null) {
            csvWriter.close();
        }
    }

}
