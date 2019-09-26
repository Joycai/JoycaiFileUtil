package joycai.utils.sheet.excel;

import com.google.common.base.Strings;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

public class ExcelWrite {

    Workbook workbook;

    CreationHelper createHelper = null;

    final String postfix;

    /**
     * @param type 指定文件类型
     * @throws OfficeXmlFileException        代表你尝试用xls方式读取xlsx
     * @throws OLE2NotOfficeXmlFileException 代表你尝试用xlsx方式读取xls
     * @throws IOException
     */
    public ExcelWrite(ExcelType type) throws OfficeXmlFileException, OLE2NotOfficeXmlFileException, IOException {
        switch (type) {
            case XLS:
                postfix = "xls";
                workbook = new HSSFWorkbook();
                break;
            case XLSX:
                postfix = "xlsx";
                workbook = new XSSFWorkbook();
                break;
            default:
                throw new IOException("unsupport excel file");
        }
    }

    /**
     * @param sheetName
     * @param dataList
     * @param type
     * @param fieldMapper 对象字段的读取顺序，会按照次序写入列
     * @param titleList   null 则不含表头
     */
    public void writeSheet(final String sheetName, List<?> dataList, Class<?> type, String[] fieldMapper, String[] titleList) {
        Sheet sheet = workbook.createSheet(sheetName);
        int rowIdx = 0;

        if (titleList != null && titleList.length > 0) {
            Row titleRow = sheet.createRow(rowIdx);
            fillRowWithString(titleList, titleRow);
            rowIdx++;
        }

        for (Object data : dataList) {
            Row dataRow = sheet.createRow(rowIdx);
            try {
                fillRowWithObject(data, type, fieldMapper, dataRow);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            rowIdx++;
        }
    }

    public byte[] exportExcel() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        return bos.toByteArray();
    }

    private void fillRowWithObject(Object data, Class<?> type, String[] fieldMapper, Row row) throws IllegalAccessException {

        Field[] fieldList = new Field[fieldMapper.length];

        for (int i = 0; i < fieldMapper.length; i++) {
            try {
                if (Strings.isNullOrEmpty(fieldMapper[i])) {
                    fieldList[i] = null;
                } else {
                    fieldList[i] = type.getDeclaredField(fieldMapper[i]);
                    fieldList[i].setAccessible(true);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                fieldList[i] = null;
            }
        }

        int colIdx = 0;

        for (Field field : fieldList) {
            Cell cell = row.createCell(colIdx);
            if (field != null) {
                switch (field.getType().getTypeName()) {
                    case "java.lang.String":
                        cell.setCellValue((String) field.get(data));
                        break;
                    case "java.lang.Boolean":
                        cell.setCellValue((Boolean) field.get(data));
                        break;
                    case "boolean":
                        cell.setCellValue(field.getBoolean(data));
                        break;
                    case "java.lang.Double":
                        cell.setCellValue((Double) field.get(data));
                        break;
                    case "double":
                        cell.setCellValue(field.getDouble(data));
                        break;
                    case "java.lang.Float":
                        cell.setCellValue((Float) field.get(data));
                        break;
                    case "float":
                        cell.setCellValue(field.getFloat(data));
                        break;

                    case "java.lang.Integer":
                        cell.setCellValue((Integer) field.get(data));
                        break;
                    case "int":
                        cell.setCellValue(field.getInt(data));
                        break;

                    case "long":
                        cell.setCellValue(field.getLong(data));
                        break;

                    case "java.lang.Long":
                        cell.setCellValue((Long) field.get(data));
                        break;

                    case "java.util.Date":
                        cell.setCellStyle(getDateStyle("yyyy/MM/dd hh:mm"));
                        cell.setCellValue((Date) field.get(data));
                        break;
                    default:
                        cell.setBlank();
                        break;
                }
            } else {
                cell.setBlank();
            }
            colIdx++;
        }
    }

    private CellStyle getDateStyle(String format) {
        if (createHelper == null) {
            createHelper = workbook.getCreationHelper();
        }
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(
                createHelper.createDataFormat().getFormat(format));

        return dateCellStyle;
    }

    private void fillRowWithString(String[] data, Row row) {
        int colIdx = 0;

        for (String d : data) {
            Cell cell = row.createCell(colIdx, CellType.STRING);
            cell.setCellValue(d);
            colIdx++;
        }
    }

}
