package joycai.utils.sheet.csv;


import com.google.common.base.Strings;
import joycai.utils.common.ChineseUtil;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class BeanReader<T> {

    Boolean printInfo = false;
    Boolean closeStream = true;

    final static String[] CHARSET_LIST = {"UTF-8", "GBK"};

    final InputStream ins;

    CellProcessor[] cellProcessors = null;

    ICSVHeaderMapper headerMapper = null;

    final Class<T> clazz;

    public BeanReader(InputStream ins, Class<T> clazz) {
        this.ins = ins;
        this.clazz = clazz;
    }

    public BeanReader(byte[] bytes, Class<T> clazz) throws IOException {
        this.ins = new ByteArrayInputStream(bytes);
        this.clazz = clazz;
    }

    private boolean containsMessy(String[] strs){
        boolean flag = false;

        for (String s : strs) {
            if ((!Strings.isNullOrEmpty(s)) && ChineseUtil.isMessyCode(s)) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    public BeanReader setPrintInfo(Boolean flag) {
        this.printInfo = flag;
        return this;
    }

    /**
     * 添加字段和表头的映射
     *
     * @param headerMapper
     * @return
     */
    public BeanReader setHeaderMap(ICSVHeaderMapper headerMapper) {
        this.headerMapper = headerMapper;
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
     * startId endIdx同时设置为0时，则表示读取全部行
     *
     * @param startIdx start from 1. not include header.
     * @param endIdx
     * @return
     */
    public List<T> readCSV(final Long startIdx, final Long endIdx) throws IOException {
        CsvBeanReader beanReader = null;
        String[] headerArray = null;

        for (String charsetName : CHARSET_LIST) {
            InputStreamReader br = new InputStreamReader(ins, Charset.forName(charsetName));
            CsvBeanReader reader = new CsvBeanReader(br, CsvPreference.STANDARD_PREFERENCE);
            String[] headers=reader.getHeader(true);
            if(!containsMessy(headers)){
                beanReader = reader;
                headerArray = headers;
                break;
            }
        }

        List<T> resultList = new ArrayList<>();

        if(null == beanReader){
            throw new NullPointerException("no reader");
        }else {
            T bean = null;

            while ((bean = beanReader.read(clazz, headerMapper.mapHeader(headerArray), cellProcessors)) != null) {

                if(startIdx == 0 && endIdx==0){
                    resultList.add(bean);
                }else {
                    if (beanReader.getLineNumber() >= startIdx + 1 && beanReader.getLineNumber() <= endIdx + 1) {
                        resultList.add(bean);
                    }
                    if (beanReader.getLineNumber() > endIdx + 1) {
                        break;
                    }
                }
            }

            if (null != beanReader && closeStream) {
                beanReader.close();
            }

            if(printInfo){
                System.out.println(resultList.size() + " rows read.");
            }

            return resultList;
        }
    }

    /**
     * @return
     * @throws IOException
     */
    public List<T> readCSV() throws IOException {
        return readCSV(0l, 0l);
    }
}
