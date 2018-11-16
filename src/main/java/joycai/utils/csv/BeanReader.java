package joycai.utils.csv;

import com.google.common.base.Strings;
import joycai.utils.common.ChineseUtil;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BeanReader<T> {

    Boolean printInfo = false;
    Boolean closeStream = true;

    final static String[] CHARSET_LIST = {"UTF-8", "GBK"};

    final ICsvBeanReader beanReader;

    String[] headerMap = null;

    CellProcessor[] cellProcessors = null;

    final Class<T> clazz;

    public BeanReader(final Reader reader, Class<T> clazz) {
        beanReader = new CsvBeanReader(reader, CsvPreference.STANDARD_PREFERENCE);
        this.clazz = clazz;
    }

    public BeanReader(byte[] bytes, Class<T> clazz) throws IOException {
        CsvBeanReader reader = null;
        for (String charset : CHARSET_LIST) {
            reader = new CsvBeanReader(getCSVReader(bytes, charset), CsvPreference.STANDARD_PREFERENCE);
            List<String> header = Arrays.asList(reader.getHeader(false));
            reader.close();
            Optional<String> messyHead = header.stream()
                    .filter(h -> !Strings.isNullOrEmpty(h))
                    .filter(h -> ChineseUtil.isMessyCode(h)).findFirst();
            //表示有乱码
            if (messyHead.isPresent()) {
                continue;
            } else {
                break;
            }
        }
        beanReader = reader;
        this.clazz = clazz;
    }

    /**
     * 判断编码
     *
     * @param fileByte
     * @param charset
     * @return
     */
    private static Reader getCSVReader(byte[] fileByte, String charset) {
        try {
            InputStreamReader br = new InputStreamReader(new ByteArrayInputStream(fileByte), charset);
            return br;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BeanReader setPrintInfo(Boolean flag) {
        this.printInfo = flag;
        return this;
    }

    /**
     * 添加字段和表头的映射
     *
     * @param headerMap
     * @return
     */
    public BeanReader setHeaderMap(String[] headerMap) {
        this.headerMap = headerMap;
        return this;
    }

    /**
     * 设置单元处理模式
     *
     * @param cellProcessors
     * @return
     */
    public BeanReader setCellProcessor(CellProcessor[] cellProcessors) {
        this.cellProcessors = cellProcessors;
        return this;
    }

    /**
     * @param startIdx start from 1. not include header
     * @param endIdx
     * @return
     */
    public List<T> readCSV(final Long startIdx, final Long endIdx) throws IOException {

        List<T> resultList = new ArrayList<>();

        if (null == headerMap) {
            throw new NullPointerException("headerMap not set");
        } else {
            beanReader.getHeader(true);
            T bean = null;
            while ((bean = beanReader.read(clazz, headerMap, cellProcessors)) != null) {
                if (beanReader.getLineNumber() >= startIdx + 1 && beanReader.getLineNumber() <= endIdx + 1) {
                    resultList.add(bean);
                }
                if (beanReader.getLineNumber() > endIdx + 1) {
                    break;
                }
            }
            return resultList;
        }
    }

    /**
     * @return
     * @throws IOException
     */
    public List<T> readCSV() throws IOException {

        List<T> resultList = new ArrayList<>();

        if (null == headerMap) {
            throw new NullPointerException("headerMap not set");
        } else {
            beanReader.getHeader(true);
            T bean = null;
            while ((bean = beanReader.read(clazz, headerMap, cellProcessors)) != null) {
                resultList.add(bean);
            }

            if (null != beanReader && closeStream) {
                beanReader.close();
            }

            return resultList;
        }
    }


}
