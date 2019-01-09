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
import java.util.*;

public class ExcelReader {

    Workbook workbook;

    /**
     * @param ins
     * @param type
     * @throws OfficeXmlFileException        代表你尝试用xls方式读取xlsx
     * @throws OLE2NotOfficeXmlFileException 代表你尝试用xlsx方式读取xls
     * @throws IOException
     */
    public ExcelReader(final InputStream ins, ExcelType type) throws OfficeXmlFileException, OLE2NotOfficeXmlFileException, IOException {
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

    public int getLineCount(final Integer sheetIdx) {
        Sheet sheet = workbook.getSheetAt(sheetIdx);
        return sheet.getLastRowNum();
    }

    /**
     * 寻找一个不为空的行
     *
     * @param sheetIdx
     * @return 0 开始，-1 没有数据
     */
    public int firstLineIdx(final Integer sheetIdx) {
        Sheet sheet = workbook.getSheetAt(sheetIdx);
        for (Iterator<Row> rowItr = sheet.rowIterator(); rowItr.hasNext(); ) {
            Row row = rowItr.next();
            if (row.getPhysicalNumberOfCells() > 0) {
                List<String> rowData = readRowAsString(row);
                if (!emptyRow(rowData)) {
                    return row.getRowNum();
                }
            }
        }
        return -1;
    }

    /**
     * @param sheetIdx
     * @param rowIdx
     * @param fieldMapper ""代表忽略这一列
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public Object readLineToObject(final Integer sheetIdx, final Integer rowIdx, String[] fieldMapper, Class clazz) throws IllegalAccessException, InstantiationException {
        List<String> rowData = readLineWithString(sheetIdx, rowIdx);
        Object obj = clazz.newInstance();

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
     * 为对象的制定字段填充数据
     *
     * @param field
     * @param obj
     * @param value
     */
    private void fillData(Field field, Object obj, String value) {
        field.setAccessible(true);
        try {
            switch (field.getType().getTypeName()) {
                case "java.lang.String":
                    field.set(obj, value);
                    break;
                case "java.lang.Boolean":
                case "boolean":
                    if(!value.isEmpty()){
                        field.set(obj, Boolean.valueOf(value));
                    }
                    break;

                case "java.lang.Double":
                case "double":
                    if(!value.isEmpty()){
                        field.set(obj, Double.parseDouble(value));
                    }
                    break;

                case "java.lang.Float":
                case "float":
                    if(!value.isEmpty()){
                        field.set(obj, Float.parseFloat(value));
                    }
                    break;

                case "java.lang.Integer":
                case "int":
                    if(!value.isEmpty()){
                        field.set(obj, Integer.valueOf(value));
                    }
                    break;

                default:
                    break;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private boolean emptyRow(List<String> rowData) {
        long emptyDataNum = rowData.stream().filter(s ->
                Strings.isNullOrEmpty(s)
        ).count();

        return emptyDataNum == rowData.size();
    }

    private List<String> readRowAsString(Row row) {
        List<String> resultData = new ArrayList<String>();

        Map<Integer, Cell> nonullCell = new HashMap<Integer, Cell>();

        row.forEach(cell -> {
            //加入有效的cell
            nonullCell.put(cell.getColumnIndex(), cell);
        });

        if (!nonullCell.isEmpty()) {
            int max = nonullCell.keySet().stream().mapToInt(value -> value).max().getAsInt();

            for (int idx = 0; idx <= max; idx++) {
                if (nonullCell.containsKey(idx)) {
                    Cell cell = nonullCell.get(idx);
                    String value = readCell(cell, cell.getCellType());
                    if(value ==null || value.isEmpty()){
                        resultData.add("");
                    }else {
                        resultData.add(value);
                    }
                } else {
                    resultData.add("");
                }
            }
        }

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

    public void close() {
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
