package joycai.utils;

public class TestObj {

    String col1;

    String col2;

    public String getCol1() {
        return col1;
    }

    public void setCol1(String col1) {
        this.col1 = col1;
    }

    public String getCol2() {
        return col2;
    }

    public void setCol2(String col2) {
        this.col2 = col2;
    }

    @Override
    public String toString() {
        return "col1:" + col1 + " " + "col2:" + col2;
    }
}
