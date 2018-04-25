package joycai.utils.common;

import java.util.Date;

public interface TypeNameList {
    String STRING = String.class.getTypeName();
    String INT = Integer.class.getTypeName();
    String DOUBLE = Double.class.getTypeName();
    String FLOAT = Float.class.getTypeName();
    String LONG = Long.class.getTypeName();
    String BOOLEAN = Boolean.class.getTypeName();
    String DATE = Date.class.getTypeName();
}
