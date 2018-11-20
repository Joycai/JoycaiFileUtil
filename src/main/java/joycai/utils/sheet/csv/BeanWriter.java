package joycai.utils.sheet.csv;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BeanWriter<T> {

    final ICsvBeanWriter csvWriter;

    final Class<T> clazz;

    String[] header = null;
    private CellProcessor[] cellProcessors;


    public BeanWriter(final Writer writer, Class<T> clazz) {
        csvWriter = new CsvBeanWriter(writer,
                CsvPreference.STANDARD_PREFERENCE);
        this.clazz = clazz;

        try {
            writer.write('\uFEFF');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BeanWriter(final OutputStream out,Class<T> clazz){
        this(new OutputStreamWriter(out, StandardCharsets.UTF_8), clazz);
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

    public void writeFile(List<T> dataList,String[] fieldsArray) throws IOException {
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
