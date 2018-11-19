package joycai.utils.sheet.excel;

import com.google.common.base.Strings;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ExcelWorker<T> {

    Workbook workbook;

    /**
     * @param ins
     * @param type
     * @throws OfficeXmlFileException        代表你尝试用xls方式读取xlsx
     * @throws OLE2NotOfficeXmlFileException 代表你尝试用xlsx方式读取xls
     * @throws IOException
     */
    public ExcelWorker(final InputStream ins, FILE_TYPE type) throws OfficeXmlFileException, OLE2NotOfficeXmlFileException, IOException {
        switch (type) {
            case XLS:
                workbook = new HSSFWorkbook(ins);
                break;
            case XLSX:
                workbook = new XSSFWorkbook(ins);
                break;
            default:
                throw new IOException("unsupport excel file");
        }
    }

    /**
     * @param sheetIdx startWith 0
     * @param rowIdx   startWith 0
     * @return
     */
    public List<String> readLineWithString(final Integer sheetIdx, final Integer rowIdx) {
        List<String> resultData = new ArrayList<String>();
        Sheet sheet = workbook.getSheetAt(sheetIdx);
        if (null != sheet) {
            Row row = sheet.getRow(rowIdx);
            if (null != row) {
                return readRowAsString(row);
            }
        }
        return resultData;
    }

    /**
     * 寻找一个不为空的行
     *
     * @param sheetIdx
     * @return 0 开始，-1 没有数据
     */
    public int firstLineIdx(final Integer sheetIdx){
        Sheet sheet = workbook.getSheetAt(sheetIdx);
        for (Iterator<Row> rowItr = sheet.rowIterator(); rowItr.hasNext(); ) {
            Row row = rowItr.next();
            if (row.getPhysicalNumberOfCells() > 0) {
                List<String> rowData = readRowAsString(row);
                if(!emptyRow(rowData)){
                    return row.getRowNum();
                }
            }
        }
        return -1;
    }

    /**
     *
     * @param sheetIdx
     * @param rowIdx
     * @param fieldMapper ""代表忽略这一列
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public T readLineToObject(final Integer sheetIdx, final Integer rowIdx, String[] fieldMapper, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        List<String> rowData = readLineWithString(sheetIdx, rowIdx);
        T obj = clazz.newInstance();

        if (fieldMapper.length == 0 || fieldMapper.length > rowData.size()) {
            return null;
        }

        int dataSize = rowData.size();
        for (int idx = 0; idx < dataSize; idx++) {
            String fieldName = fieldMapper[idx];
            if (Strings.isNullOrEmpty(fieldName)) {
                continue;
            }
            String data = rowData.get(idx);

            try {
                fillData(clazz.getDeclaredField(fieldName), obj, data);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    /**
     * 填充数据
     *
     * @param field
     * @param obj
     * @param value
     */
    private void fillData(Field field, Object obj, String value){
        field.setAccessible(true);
        try {
            switch (field.getType().getTypeName()) {
                case "java.lang.String":
                    field.set(obj, value);
                    break;
                case "java.lang.Boolean":
                    field.set(obj, Boolean.valueOf(value));
                    break;
                case "java.lang.Double":
                    field.set(obj, Double.parseDouble(value));
                    break;
                case "java.lang.Integer":
                    field.set(obj, Integer.valueOf(value));
                    break;
                default:
            }
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    private boolean emptyRow(List<String> rowData){
        long emptyDataNum = rowData.stream().filter(s ->
                Strings.isNullOrEmpty(s)
        ).count();

        return emptyDataNum == rowData.size();
    }

    private List<String> readRowAsString(Row row){
        List<String> resultData = new ArrayList<String>();
        row.forEach(cell -> {
            String cellValue = readCell(cell,cell.getCellType());

            if (!Strings.isNullOrEmpty(cellValue)) {
                resultData.add(cellValue);
            } else {
                resultData.add("");
            }
        });
        return resultData;
    }

    private String readCell(Cell cell, CellType type) {
        //获取单元格的值
        switch (type) {
            case NUMERIC:
                return NumberToTextConverter.toText(cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return readCell(cell, cell.getCachedFormulaResultType());
            default:
                return "";
        }
    }

    public enum FILE_TYPE {
        XLS,
        XLSX
    }
}
