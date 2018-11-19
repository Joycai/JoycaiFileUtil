package joycai.utils.sheet.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class CSVReader {

    public static BeanReader newBeanReader(final byte[] fileByte, Class clazz) throws IOException {
        return new BeanReader(fileByte, clazz);
    }
    public static BeanReader newBeanReader(final InputStream ins, Class clazz) throws IOException {
        return new BeanReader(ins, clazz);
    }
}
