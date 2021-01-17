package joycai.utils.sheet.csv;


import java.io.IOException;
import java.io.Writer;

public class CSVWriter {

    public static <T> BeanWriter<T> newBeanWriter(final Writer writer, Class<T> clazz) throws IOException {
        return new BeanWriter<T>(writer, clazz);
    }
}
