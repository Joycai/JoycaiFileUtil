package joycai.utils.sheet.csv;

/**
 * 对表头重定向
 */
public interface ICSVHeaderMapper {
    String[] mapHeader(String[] csvHeaders);
}
