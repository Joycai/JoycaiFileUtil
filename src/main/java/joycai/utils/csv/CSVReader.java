package joycai.utils.csv;

import java.io.Reader;

public class CSVReader {

    public static BeanReader newBeanReader(final Reader reader,Class clazz){
        return new BeanReader(reader, clazz);
    }

    public static BeanReader newBeanReader(byte[] bytes,Class clazz){
        return new BeanReader(bytes, clazz);
    }
}
