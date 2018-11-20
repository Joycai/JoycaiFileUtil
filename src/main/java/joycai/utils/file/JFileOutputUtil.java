package joycai.utils.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class JFileOutputUtil {

    final FileOutputStream fos;

    private JFileOutputUtil(String path) throws FileNotFoundException {
        this.fos = new FileOutputStream(new File(path));
    }

    public static JFileOutputUtil newJFileOutputUtil(String path) throws FileNotFoundException {
        return new JFileOutputUtil(path);
    }

    public void writeByte(byte[] bytes) throws IOException {
        fos.write(bytes);
    }

    public void writeAndClose(byte[] bytes) throws IOException {
        writeByte(bytes);
        close();
    }

    public void close() throws IOException {
        fos.flush();
        fos.close();
    }
}
