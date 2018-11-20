package joycai.utils.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    String fileName;
    ByteArrayOutputStream bos;
    ZipOutputStream zipOut;

    public static ZipUtil getNewZip(String fileName) {
        return new ZipUtil(fileName);
    }

    public ZipUtil(String fileName) {
        this.fileName = fileName;
        this.bos = new ByteArrayOutputStream();
        this.zipOut = new ZipOutputStream(this.bos);
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ZipUtil addZipFile(String fileName, byte[] fileByts) {
        try {
            this.zipOut.putNextEntry(new ZipEntry(fileName));
            this.zipOut.write(fileByts);
            this.zipOut.closeEntry();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return this;
    }

    public byte[] getZip() {
        try {
            this.zipOut.flush();
            this.zipOut.close();
            this.bos.flush();
            this.bos.close();
            return this.bos.toByteArray();
        } catch (IOException var2) {
            var2.printStackTrace();
            return null;
        }
    }
}
