package joycai.utils.sheet.csv;

import java.io.IOException;
import java.io.InputStream;

public class CSVReader {

    public static <T> BeanReader<T> newBeanReader(final byte[] fileByte, Class<T> clazz) throws IOException {
        return new BeanReader<T>(fileByte, clazz);
    }

    public static <T> BeanReader<T> newBeanReader(final InputStream ins, Class<T> clazz) throws IOException {
        return new BeanReader<T>(ins, clazz);
    }
}
