//package joycai.utils;
//
//import com.commonUtil.ChineseUtil;
//import com.expresscne.parcelconfig.data.model.ExcelConfig;
//import com.expresscne.parcelconfig.data.model.excel.ExcelDataExportMapObject;
//import com.expresscne.parcelconfig.data.model.excel.ExcelModelBindConfig;
//import com.expresscne.parcelconfig.data.model.excel.ExcelModelDataConfig;
//import com.expresscne.parcelconfig.data.util.csvcellprocessors.OrderBooleanCellProcessor;
//import com.expresscne.parcelconfig.data.util.csvcellprocessors.OrderIntegerCellProcessor;
//import com.google.common.base.Splitter;
//import com.google.common.base.Strings;
//import com.google.common.collect.Iterables;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import joycai.utils.common.ChineseUtil;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.NumberToTextConverter;
//import org.apache.poi.xssf.usermodel.XSSFCell;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.supercsv.cellprocessor.ParseDouble;
//import org.supercsv.cellprocessor.ift.CellProcessor;
//import org.supercsv.io.CsvBeanReader;
//import org.supercsv.prefs.CsvPreference;
//
//import java.io.*;
//import java.lang.reflect.Field;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class FILEAnalyzer {
//
//    final static String[] CHARSET_LIST = {"UTF-8", "GBK"};
//
//    /**
//     * 猜测文件编码
//     *
//     * @param fileByte
//     * @return
//     * @throws IOException
//     */
//    protected static Optional<String> detectorCharset(byte[] fileByte) throws IOException {
//
//        for (String charset : CHARSET_LIST) {
//            CsvBeanReader reader = new CsvBeanReader(getCSVReader(fileByte, charset), CsvPreference.STANDARD_PREFERENCE);
//            List<String> header = Arrays.asList(reader.getHeader(false));
//            reader.close();
//            Optional<String> messyHead = header.stream()
//                    .filter(h -> !Strings.isNullOrEmpty(h))
//                    .filter(h -> ChineseUtil.isMessyCode(h)).findFirst();
//            //表示有乱码
//            if (messyHead.isPresent()) {
//                continue;
//            } else {
//                return Optional.of(charset);
//            }
//        }
//        return Optional.empty();
//    }
//
//    /**
//     * 获取csvTitle
//     *
//     * @param fileByte
//     * @param sheetIdx
//     * @param titleRowIdx
//     * @return
//     */
//    public static Optional<List<String>> readCSVFileTitleStream(byte[] fileByte, Integer sheetIdx, Integer titleRowIdx) {
//        try {
//            Optional<String> charset = detectorCharset(fileByte);
//            if (!charset.isPresent()) {
//                return Optional.empty();
//            }
//
//            CsvBeanReader reader = new CsvBeanReader(getCSVReader(fileByte, charset.get()), CsvPreference.STANDARD_PREFERENCE);
//            List<String> titles = Arrays.asList(reader.getHeader(false));
//            reader.close();
//
//            return Optional.of(titles.stream().filter(t -> !Strings.isNullOrEmpty(t)).collect(Collectors.toList()));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return Optional.empty();
//    }
//
//    /**
//     * 读取CSV
//     *
//     * @param fileByte
//     * @param sheetIdx
//     * @param titleRowIdx
//     * @param excelModelBindConfig
//     * @return
//     */
//    public static <T> Optional<List<T>> readCSVFileNew(byte[] fileByte, int sheetIdx, Integer titleRowIdx, ExcelModelBindConfig excelModelBindConfig) {
//        CsvBeanReader reader = null;
//        try {
//            Optional<String> charset = detectorCharset(fileByte);
//            if (!charset.isPresent()) {
//                return Optional.empty();
//            }
//
//            reader = new CsvBeanReader(getCSVReader(fileByte, charset.get()), CsvPreference.STANDARD_PREFERENCE);
//            List<String> titles = Arrays.asList(reader.getHeader(false));
//
//            Map<String, String> titleToFieldMap = excelModelBindConfig.getConfigs().stream().distinct().collect(Collectors.toMap(ExcelModelDataConfig::getExcelTitleName, ExcelModelDataConfig::getDataFieldName));
//
//            Class<?> clazz = Class.forName(excelModelBindConfig.getModelName());
//            List<T> result = new LinkedList<>();
//            //构建表头和字段的映射
//            String[] nameMapping = titles.stream().map(t -> {
//                if (!titleToFieldMap.containsKey(t)) {
//                    return null;
//                } else {
//                    return titleToFieldMap.get(t);
//                }
//            }).collect(Collectors.toList()).toArray(new String[]{});
//            //按照字段类型构建cellProcessor
//            List<CellProcessor> processorList = Lists.newArrayList();
//            for (String fieldName : nameMapping) {
//                if (Strings.isNullOrEmpty(fieldName)) {
//                    processorList.add(null);
//                    continue;
//                }
//                Field field = clazz.getDeclaredField(fieldName);
//                field.setAccessible(true);
//                switch (field.getType().getTypeName()) {
//                    case "java.lang.String":
//                        processorList.add(null);
//                        break;
//                    case "java.lang.Boolean":
//                        processorList.add(new org.supercsv.cellprocessor.Optional(new OrderBooleanCellProcessor()));
//                        break;
//                    case "java.lang.Double":
//                        processorList.add(new org.supercsv.cellprocessor.Optional(new ParseDouble()));
//                        break;
//                    case "java.lang.Integer":
//                        processorList.add(new org.supercsv.cellprocessor.Optional(new OrderIntegerCellProcessor()));
//                        break;
//                    default:
//                        processorList.add(null);
//                        break;
//                }
//            }
//
//            int emptyFieldLimit = (int) (titles.stream().filter(t -> !Strings.isNullOrEmpty(t)).count() * 0.8);
//
//            while (true) {
//                T row = (T) clazz.newInstance();
//                Object obj = reader.read(row, nameMapping, processorList.toArray(new CellProcessor[]{}));
//                if (Objects.isNull(obj)) {
//                    reader.close();
//                    break;
//                }
//                //检索空行
//                int emptyFieldCount = 0;
//                for (String fieldName : nameMapping) {
//                    if (Strings.isNullOrEmpty(fieldName)) {
//                        continue;
//                    } else {
//                        Field field = clazz.getDeclaredField(fieldName);
//                        field.setAccessible(true);
//                        Object fieldValue = field.get(row);
//                        if (Objects.isNull(fieldValue)) {
//                            emptyFieldCount++;
//                        } else {
//                            String strObj = fieldValue.toString();
//                            if (Strings.isNullOrEmpty(strObj)) {
//                                emptyFieldCount++;
//                            }
//                        }
//                    }
//                }
//                if (emptyFieldCount >= emptyFieldLimit) {
//                    continue;
//                }
//                result.add(row);
//            }
//            return Optional.of(result);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//
//        return Optional.empty();
//    }
//
//
//    protected static Reader getCSVReader(byte[] fileByte, String charset) {
//        try {
//            InputStreamReader br = new InputStreamReader(new ByteArrayInputStream(fileByte), charset);
//
//            return br;
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public enum FILE_TYPE {
//        NOT_SET,
//        CSV,
//        XLS,
//        XLSX
//    }
//
//    final static String FILE_PATH = "";
//    private static String PACKAGE_NAME = "com.expresscne.parcelconfig.data.model.";
//
//    /**
//     * 获取wb的对象
//     *
//     * @param fileByte
//     * @param FILE_type
//     * @return
//     */
//    public static Optional<Workbook> getWorkbookObJ(final byte[] fileByte, final FILE_TYPE FILE_type) {
//
//        InputStream imp = new ByteArrayInputStream(fileByte);
//        try {
//            switch (FILE_type) {
//                case XLS:
//                    return Optional.of(new HSSFWorkbook(imp));
//                case XLSX:
//                    return Optional.of(new XSSFWorkbook(imp));
//                default:
//                    break;
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return Optional.empty();
//    }
//
//    /**
//     * 读取excel文件头
//     *
//     * @param wb
//     * @param sheetIdx 从0开始
//     * @param rowIdx   从0开始[标题行]
//     * @return
//     */
//    public static Optional<List<String>> readExcelFileTitleStream(final Workbook wb, final Integer sheetIdx, final Integer rowIdx) {
//
//        List<String> titleList = Lists.newArrayList();
//
//        Sheet sheet = wb.getSheetAt(sheetIdx);
//
//        if (Objects.isNull(sheet)) {
//            return Optional.empty();
//        } else {
//            Row titleRow = sheet.getRow(rowIdx);
//            if (Objects.isNull(titleRow)) {
//                return Optional.empty();
//            } else {
//                titleRow.forEach(cell -> {
//                    String titleName = cell.getStringCellValue();
//                    if (!Strings.isNullOrEmpty(titleName)) {
//                        titleList.add(titleName);
//                    }
//                });
//            }
//        }
//
//        return Optional.of(titleList);
//    }
//
//    /**
//     * 获取文件
//     *
//     * @param wb
//     * @param sheetIdx             从0开始
//     * @param rowIdx               从0开始[标题行]
//     * @param excelModelBindConfig
//     * @param <T>
//     * @return
//     */
//    public static <T> Optional<List<T>> readExcelFileNew(final Workbook wb,
//                                                         final Integer sheetIdx,
//                                                         final Integer rowIdx,
//                                                         final ExcelModelBindConfig excelModelBindConfig)
//            throws NumberFormatException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
//        //检查
//        Sheet sheet = wb.getSheetAt(sheetIdx);
//        if (Objects.isNull(sheet)) {
//            return Optional.empty();
//        }
//
//        Row titleRow = sheet.getRow(rowIdx);
//        if (Objects.isNull(titleRow)) {
//            return Optional.empty();
//        }
//        //检查结束
//
//        //获取excel映射配置
//        List<ExcelModelDataConfig> excelConfigs = excelModelBindConfig.getConfigs();
//        if (Objects.isNull(excelConfigs) || Iterables.isEmpty(excelConfigs)) {
//            //excel配置参数为空
//            return Optional.empty();
//        }
//        if (!validateExcelModelDataConfig(excelModelBindConfig.getModelName(), excelConfigs)) {
//            //excel配置中包含不存在的目标字段
//            return Optional.empty();
//        }
//
//        //建立 列号->字段名的映射
//        //先读取表头,获取表头名->列号的映射
//        Map<String, Integer> titleToColIdx = Maps.newHashMap();
//        titleRow.forEach(cell -> {
//            if (!Strings.isNullOrEmpty(cell.getStringCellValue()) && !titleToColIdx.containsKey(cell.getStringCellValue())) {
//                titleToColIdx.put(cell.getStringCellValue(), cell.getColumnIndex());
//            }
//        });
//        //列号->字段名
//        Map<Integer, String> colIdxToField = excelConfigs.stream()
//                .filter(config -> titleToColIdx.containsKey(config.getExcelTitleName()))
//                .collect(Collectors.toMap(excelModelDataConfig ->
//                                titleToColIdx.get(excelModelDataConfig.getExcelTitleName())
//                        , ExcelModelDataConfig::getDataFieldName)
//                );
//
//        //开始读取数据
//        String modelName = excelModelBindConfig.getModelName();
//
//
//        Class<?> clazz = Class.forName(modelName);
//        List<T> result = new LinkedList<>();
//
//
//        for (Iterator<Row> itr = sheet.rowIterator(); itr.hasNext(); ) {
//            Row row = itr.next();
//            if (row.getRowNum() > rowIdx) {
//
//                T rowData = (T) clazz.newInstance();
//                //检索空行
//                //1.获取单元格数
//                //2.检索值为空的单元格
//                //3.相等即为空行
//                int cellNum = row.getPhysicalNumberOfCells();
//                int cellCount = 0;
//                for (Iterator iter = row.cellIterator(); iter.hasNext(); ) {
//                    Cell cell = (Cell) iter.next();
//                    //获取单元格的值
//                    String value = null;
//                    switch (cell.getCellType()) {
//                        case XSSFCell.CELL_TYPE_NUMERIC:
//                            value = NumberToTextConverter.toText(cell.getNumericCellValue());
//                            break;
//                        case XSSFCell.CELL_TYPE_STRING:
//                            value = cell.getStringCellValue();
//                            break;
//                        case XSSFCell.CELL_TYPE_BOOLEAN:
//                            value = String.valueOf(cell.getBooleanCellValue());
//                            break;
//                        default:
//                    }
//                    if (Strings.isNullOrEmpty(value)) {
//                        cellCount++;
//                    }
//                }
//
//                //不为空行,读取
//                if (cellCount < cellNum) {
//
//                    for (Iterator<Cell> cellitr = row.cellIterator(); cellitr.hasNext(); ) {
//                        Cell cell = cellitr.next();
//                        String fieldName = colIdxToField.get(cell.getColumnIndex());
//                        if (!Strings.isNullOrEmpty(fieldName)) {
//                            Field field = rowData.getClass().getDeclaredField(fieldName);
//                            //按照字段类型设置值
//                            fieldSetWithRTTI(field, rowData, cell);
//                        }
//                    }
//
//                    result.add(rowData);
//                }
//            }
//        }
//        return Optional.of(result);
//    }
//
//    public static void fieldSetWithRTTI(Field field, Object obj, String value) throws IllegalAccessException {
//        field.setAccessible(true);
//        switch (field.getType().getTypeName()) {
//            case "java.lang.String":
//                field.set(obj, value);
//                break;
//            case "java.lang.Boolean":
//                field.set(obj, Boolean.valueOf(value));
//                break;
//            case "java.lang.Double":
//                field.set(obj, Double.parseDouble(value));
//                break;
//            case "java.lang.Integer":
//                field.set(obj, Integer.valueOf(value));
//                break;
//            default:
//        }
//    }
//
//    public static void fieldSetWithRTTI(Field field, Object obj, Cell cell) throws NumberFormatException, IllegalAccessException {
//
//        field.setAccessible(true);
//
//        switch (field.getType().getTypeName()) {
//            case "java.lang.String":
//
//                String strValue = "";
//
//                switch (cell.getCellType()) {
//                    case XSSFCell.CELL_TYPE_NUMERIC:
//                        strValue = NumberToTextConverter.toText(cell.getNumericCellValue());
//                        break;
//                    case XSSFCell.CELL_TYPE_STRING:
//                        strValue = cell.getStringCellValue();
//                        break;
//                    case XSSFCell.CELL_TYPE_BOOLEAN:
////                        boolValue = cell.getBooleanCellValue();
//                        break;
//                    default:
//                }
//
//                field.set(obj, strValue);
//                break;
//            case "java.lang.Boolean":
//
//                Boolean boolValue = false;
//                //获取单元格的值
//                switch (cell.getCellType()) {
//                    case XSSFCell.CELL_TYPE_NUMERIC:
//                        if (cell.getNumericCellValue() == 0) {
//                            boolValue = false;
//                        } else {
//                            boolValue = true;
//                        }
//                        break;
//                    case XSSFCell.CELL_TYPE_STRING:
//                        if (DataUtil.compareStr(cell.getStringCellValue(), "是")) {
//                            boolValue = true;
//                        }
//                        break;
//                    case XSSFCell.CELL_TYPE_BOOLEAN:
//                        boolValue = cell.getBooleanCellValue();
//                        break;
//                    default:
//                }
//                field.set(obj, boolValue);
//                break;
//
//            case "java.lang.Double":
//
//                Double dbValue = 0d;
//
//                switch (cell.getCellType()) {
//                    case XSSFCell.CELL_TYPE_NUMERIC:
//                        dbValue = cell.getNumericCellValue();
//                        break;
//                    case XSSFCell.CELL_TYPE_STRING:
//                        dbValue = Double.parseDouble(cell.getStringCellValue());
//                        break;
//                    case XSSFCell.CELL_TYPE_BOOLEAN:
//                        break;
//                    default:
//                        break;
//                }
//                field.set(obj, dbValue);
//                break;
//            case "java.lang.Integer":
//                Integer intValue = 0;
//                switch (cell.getCellType()) {
//                    case XSSFCell.CELL_TYPE_NUMERIC:
//                        intValue = (int) cell.getNumericCellValue();
//                        break;
//                    case XSSFCell.CELL_TYPE_STRING:
//
//                        if (DataUtil.compareStr(cell.getStringCellValue().trim(), "文件")) {
//                            intValue = 0;
//                        } else if (DataUtil.compareStr(cell.getStringCellValue().trim(), "包裹") || DataUtil.compareStr(cell.getStringCellValue().trim(), "物品")) {
//                            intValue = 1;
//                        } else if (DataUtil.compareStr(cell.getStringCellValue().trim(), "防水袋")) {
//                            intValue = 2;
//                        } else {
//                            intValue = Integer.parseInt(cell.getStringCellValue());
//                        }
//                        break;
//                    case XSSFCell.CELL_TYPE_BOOLEAN:
//                        break;
//                    default:
//                        break;
//                }
//                field.set(obj, intValue);
//                break;
//            default:
//        }
//    }
//
//    /*******************************************
//     * 导出对象数据
//     ****************************************/
//
//    /**
//     *
//     * @param dataCollection
//     * @param excelDataExportMapObjectsIn
//     * @return
//     */
//    public static byte[] exportToExcelFile(Collection<? extends Object> dataCollection, List<ExcelDataExportMapObject> excelDataExportMapObjectsIn) {
//        return exportToExcelFile(dataCollection, excelDataExportMapObjectsIn, true);
//    }
//
//    /**
//     *
//     * @param dataCollection
//     * @param excelDataExportMapObjectsIn
//     * @param needSerialNo
//     * @return
//     */
//    public static byte[] exportToExcelFile(Collection<? extends Object> dataCollection, List<ExcelDataExportMapObject> excelDataExportMapObjectsIn, boolean needSerialNo) {
//
//        Workbook wb = new HSSFWorkbook();
//
//        //创建日期格式
//        CreationHelper createHelper = wb.getCreationHelper();
//        CellStyle dateCellStyle = wb.createCellStyle();
//        dateCellStyle.setDataFormat(
//                createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
//
//        Sheet sheet = wb.createSheet("订单导出");
//
//        List<ExcelDataExportMapObject> excelDataExportMapObjects = Lists.newArrayList();
//
//        //添加序号列
//        if (needSerialNo) {
//            ExcelDataExportMapObject idx = new ExcelDataExportMapObject();
//            idx.setDisplayName("序号");
//            idx.setFieldName("recidx");
//            excelDataExportMapObjects.add(idx);
//        }
//
//        excelDataExportMapObjects.addAll(excelDataExportMapObjectsIn);
//
//
//        /**
//         * 表头列表
//         */
//        List<String> titleList = excelDataExportMapObjects.stream()
//                .map(excelDataExportConfig -> excelDataExportConfig.getDisplayName())
//                .collect(Collectors.toList());
//
//        /**
//         * 表头和字段映射
//         */
//        Map<String, String> titleToField = excelDataExportMapObjects.stream()
//                .collect(Collectors.toMap(ExcelDataExportMapObject::getDisplayName, ExcelDataExportMapObject::getFieldName));
//
//        int rowIdx = 0;
//        //创建表头
//        if (DataUtil.hasCollections(titleList)) {
//            Row titleRow = sheet.createRow(rowIdx);
//            Iterator<String> itr = titleList.iterator();
//            for (int cellIdx = 0; itr.hasNext(); cellIdx++) {
//                String value = itr.next();
//                Cell cell = titleRow.createCell(cellIdx);
//                cell.setCellValue(value);
//            }
//        }
//        rowIdx++;
//
//        if (DataUtil.hasCollections(dataCollection)) {
//            //填充数据
//            Iterator itr = dataCollection.iterator();
//            for (; itr.hasNext(); rowIdx++) {
//                Row dataRow = sheet.createRow(rowIdx);
//                Object obj = itr.next();
//                fillRow(rowIdx, dateCellStyle, dataRow, obj, titleList, titleToField);
//            }
//        }
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try {
//            wb.write(bos);
//            return bos.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * 填充数据
//     *
//     * @param rowIdx
//     * @param dateCellStyle
//     * @param dataRow
//     * @param obj
//     * @param titleList
//     * @param titleToField
//     */
//    private static void fillRow(int rowIdx, CellStyle dateCellStyle, Row dataRow, Object obj, List<String> titleList, Map<String, String> titleToField) {
//
//        Class<? extends Object> clazz = obj.getClass();
//        Iterator<String> itr = titleList.iterator();
//        for (int i = 0; itr.hasNext(); i++) {
//
//
//            String titleName = itr.next();
//            String fieldName = titleToField.get(titleName);
//
//            if (DataUtil.compareStr("recidx", fieldName)) {
//                //如果是序号列的话
//
//                Cell cell = dataRow.createCell(i);
//                cell.setCellValue(rowIdx);
//
//            } else {
//                try {
//                    Field field = clazz.getDeclaredField(fieldName);
//
//                    field.setAccessible(true);
//
//                    Cell cell = dataRow.createCell(i);
//
//                    switch (field.getType().getTypeName()) {
//                        case "java.lang.String":
//                            cell.setCellValue((String) field.get(obj));
//                            break;
//                        case "java.lang.Boolean":
//                            cell.setCellValue((Boolean) field.get(obj));
//                            break;
//                        case "java.lang.Float":
//                            cell.setCellValue((Float) field.get(obj));
//                            break;
//                        case "java.lang.Double":
//                            cell.setCellValue((Double) field.get(obj));
//                            break;
//                        case "java.lang.Integer":
//                            cell.setCellValue((Integer) field.get(obj));
//                            break;
//                        case "java.util.Date":
//                            cell.setCellStyle(dateCellStyle);
//                            cell.setCellValue((Date) field.get(obj));
//                        default:
//                            break;
//                    }
//
//                } catch (NoSuchFieldException e) {
//                    e.printStackTrace();
//                    break;
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//        }
//    }
//
//    /***********************************
//     * 老方法
//     ********************************************/
//
//    @Deprecated
//    protected static Workbook getWorkbookObJ(final File localFile) {
//
//        try {
//            InputStream inp = new FileInputStream(localFile);
//            String fileName = localFile.getName();
//
//            try {
//                if (fileName.endsWith("xlsx")) {
//                    return new XSSFWorkbook(inp);
//                } else if (fileName.endsWith("xls")) {
//                    return new HSSFWorkbook(inp);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    //获取excel的标题
//    @Deprecated
//    public static List<String> readExcelFileTitle(final File localFile,
//                                                  final int sheetIndex,
//                                                  final int titleIndex) throws IOException {
//        //检测参数是否正确
//        if (Objects.isNull(localFile)) {
//            return null;
//        }
//        Workbook wb = getWorkbookObJ(localFile);
//        if (Objects.isNull(wb)) {
//            return null;
//        }
//
//        int sheetCount = wb.getNumberOfSheets();
//        if (sheetIndex > sheetCount) {
//            return null;
//        }
//        Sheet sheet = wb.getSheetAt(sheetIndex);
//        int rows = sheet.getPhysicalNumberOfRows();
//        if (titleIndex < 0 || rows < titleIndex) {
//            return null;
//        }
//
//        Row titleRow = sheet.getRow(titleIndex);
//
//        if (Objects.isNull(titleRow)) {
//            return null;
//        }
//
//        List<String> titleList = Lists.newArrayList();
//
//        titleRow.forEach(cell -> {
//            String titleName = cell.getStringCellValue();
//            if (!Strings.isNullOrEmpty(titleName)) {
//                titleList.add(titleName);
//            }
//        });
//
//        return titleList;
//    }
//
//    //校验配置文件和model字段是否匹配
//    @Deprecated
//    private static boolean validateExcelModelDataConfig(String className, List<ExcelModelDataConfig> excelConfigs) {
//
//        try {
//            Field[] fields = Class.forName(className).getDeclaredFields();
//            Set<String> fieldsSets = Sets.newHashSet();
//
//            for (Field field : fields) {
//                fieldsSets.add(field.getName());
//            }
//
//            for (ExcelModelDataConfig excelConfig : excelConfigs) {
//                if (!fieldsSets.contains(excelConfig.getDataFieldName())) {
//                    return false;
//                }
//            }
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return true;
//    }
//
//    @Deprecated
//    public static <T> List<T> readExcelFile(final File localFile,
//                                            final int si, final int ti,
//                                            final List<ExcelConfig> excelConfigMap,
//                                            final String className) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
//        InputStream inp = new FileInputStream(FILE_PATH + localFile.getName());
//
//        if (excelConfigMap == null) return null;
//
//        int sheetindex = si - 1;
//        int titleindex = ti - 1;
//        int dataIndex = ti;
//
//        if (!validateObjAndConfig(className, excelConfigMap)) {
//            return null;
//        }
//
//        Workbook wb = getWorkbookObJ(localFile);
//        //检查文件
//        if (wb == null) {
//            return null;
//        }
//        int sheetCount = wb.getNumberOfSheets();
//        if (sheetindex > sheetCount) {
//            return null;
//        }
//        Sheet sheet = wb.getSheetAt(sheetindex);
//        int rows = sheet.getPhysicalNumberOfRows();
//        if (titleindex < 0 || dataIndex > rows) {
//            return null;
//        }
//
//        Row titleRow = sheet.getRow(titleindex);
//        int cells = titleRow.getPhysicalNumberOfCells();
//
//        //表单列索引数据，对应列序号查找表头名
//        Map<Integer, String> tableDataMap = Maps.newHashMap();
//
//        for (int c = 0; c < cells; c++) {
//            Cell cell = titleRow.getCell(c);
//            if (cell == null) {
//                continue;
//            }
//            tableDataMap.put(c, cell.getStringCellValue());
//        }
//
//        //获取配置信息
//        String modelName = excelConfigMap.get(0).getModelName();
//        Map<String, ExcelConfig> tableAndFieldMap = new LinkedHashMap<>();//表头映射字段名
//        for (ExcelConfig config : excelConfigMap) {
//            tableAndFieldMap.put(config.getExcelTitleName(), config);
//        }
//
//        //建立结果
//        //TODO:以后再说
//        List<T> result = new LinkedList<>();
//        Class<?> clazz = Class.forName(PACKAGE_NAME + modelName);
//        T obj = null;
//
//
//        //读取行
//        for (int r = dataIndex; r <= rows; r++) {
//
//            Row row = sheet.getRow(r);
//            if (row == null) {
//                continue;
//            }
//            //为一行创建对象
//            obj = (T) clazz.newInstance();
//
//            cells = row.getPhysicalNumberOfCells();
//
//            Iterator<Cell> iterator = row.cellIterator();
//
//
//            //遍历单元格
//            do {
//                Cell cell = iterator.next();
//
//
//                if (cell == null) {
//                    System.out.println("skip");
//                    continue;
//                }
//
//                ExcelConfig config = tableAndFieldMap.get(tableDataMap.get(cell.getColumnIndex()));
//
//                if (config == null) continue;
//
//                Field field = obj.getClass().getDeclaredField(config.getFieldName());
//                field.setAccessible(true);
//
//                String value = null;
//                switch (cell.getCellType()) {
//
//                    case XSSFCell.CELL_TYPE_NUMERIC:
//                        value = String.valueOf(cell.getNumericCellValue());
//                        break;
//
//                    case XSSFCell.CELL_TYPE_STRING:
//                        value = cell.getStringCellValue();
//                        break;
//                    case XSSFCell.CELL_TYPE_BOOLEAN:
//                        value = String.valueOf(cell.getBooleanCellValue());
//                        break;
//
//                    default:
//                }
//
//                String dataType = config.getDataType();
//
//                if (dataType.equalsIgnoreCase("string")) {
//                    if (value.contains(".")) {
//
//                        List<String> stringArray = Splitter.on(".").trimResults().omitEmptyStrings().splitToList(value);
//                        field.set(obj, stringArray.get(0));
//                    } else {
//                        field.set(obj, value);
//                    }
//
//                } else if (dataType.equalsIgnoreCase("double")) {
//                    field.set(obj, Double.parseDouble(value));
//                } else if (dataType.equalsIgnoreCase("boolean")) {
//                    if (value.equalsIgnoreCase("是")) {
//                        field.set(obj, true);
//                    } else if (value.equalsIgnoreCase("否")) {
//                        field.set(obj, false);
//                    } else {
//                        field.set(obj, Boolean.parseBoolean(value));
//                    }
//                }
//            } while (iterator.hasNext());
//
//            result.add(obj);
//        }
//        return result;
//
//    }
//
//    //校验cofig 和obj field的一致性
//    @Deprecated
//    private static boolean validateObjAndConfig(String className, List<ExcelConfig> excelConfigMap) {
//
//        try {
//            Field[] fields = Class.forName(PACKAGE_NAME + className).getDeclaredFields();
//            //
//            Map<String, Boolean> validationMap = new LinkedHashMap<>();
//
//            for (Field field : fields) {
//                validationMap.put(field.getName(), false);
//            }
//
//            for (ExcelConfig config : excelConfigMap) {
//                String filedName = config.getFieldName();
//                if (!validationMap.containsKey(filedName)) {
//                    return false;
//                } else {
//                    validationMap.put(filedName, true);
//                }
//            }
//
//            return true;
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//}
