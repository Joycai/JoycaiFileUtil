package joycai.utils.csv;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class BeanReader<T> {

    Boolean printInfo = false;
    Boolean closeStream = true;


    final ICsvBeanReader beanReader;

    String[] headerMap = null;

    CellProcessor[] cellProcessors = null;

    final Class<T> clazz;

    public BeanReader(final Reader reader, Class<T> clazz) {
        beanReader = new CsvBeanReader(reader, CsvPreference.STANDARD_PREFERENCE);
        this.clazz = clazz;
    }

    public BeanReader setPrintInfo(Boolean flag) {
        this.printInfo = flag;
        return this;
    }

    /**
     * 添加字段和表头的映射
     * @param headerMap
     * @return
     */
    public BeanReader setHeaderMap(String[] headerMap) {
        this.headerMap = headerMap;
        return this;
    }

    /**
     * 设置单元处理模式
     * @param cellProcessors
     * @return
     */
    public BeanReader setCellProcessor(CellProcessor[] cellProcessors) {
        this.cellProcessors = cellProcessors;
        return this;
    }

    /**
     *
     * @param startIdx start from 1. not include header
     * @param endIdx
     * @return
     */
    public List<T> readCSV(final Long startIdx,final Long endIdx) throws IOException {

        List<T> resultList = new ArrayList<>();

        if (null == headerMap) {
            throw new NullPointerException("headerMap not set");
        }else {
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
            return  resultList;
        }
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public List<T> readCSV() throws IOException {

        List<T> resultList = new ArrayList<>();

        if (null == headerMap) {
            throw new NullPointerException("headerMap not set");
        }else {
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
