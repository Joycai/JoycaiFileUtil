package joycai.utils.file;

import java.io.File;
import java.io.IOException;

public class JFileWriter {

    java.io.FileWriter fw = null;

    public JFileWriter(File file, boolean append) throws IOException {
        fw = new java.io.FileWriter(file, append);
    }

    public static JFileWriter newFile(String path) throws IOException {
        return new JFileWriter(new File(path), false);
    }

    public static JFileWriter appendFile(String path) throws IOException {
        return new JFileWriter(new File(path), true);
    }


    public JFileWriter write(String content) throws IOException {

        if (null != fw) {
            fw.write(content);
        }
        return this;
    }

    public void close() {
        if (fw != null) {
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
