package joycai.utils.sheet.csv;


import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class CSVWriter {

    public static BeanWriter newBeanWriter(final Writer writer, Class clazz) throws IOException {
        return new BeanWriter(writer, clazz);
    }
}
