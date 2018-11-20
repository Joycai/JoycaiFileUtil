package joycai.utils.sheet.model;

import java.util.Date;

public class TestObj {

    String col1;

    String col2;

    float f1;

    Float f2;

    int i1;

    Integer i2;

    Date date;

    Boolean bl1;

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

    public float getF1() {
        return f1;
    }

    public void setF1(float f1) {
        this.f1 = f1;
    }

    public Float getF2() {
        return f2;
    }

    public void setF2(Float f2) {
        this.f2 = f2;
    }

    public int getI1() {
        return i1;
    }

    public void setI1(int i1) {
        this.i1 = i1;
    }

    public Integer getI2() {
        return i2;
    }

    public void setI2(Integer i2) {
        this.i2 = i2;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getBl1() {
        return bl1;
    }

    public void setBl1(Boolean bl1) {
        this.bl1 = bl1;
    }

    @Override
    public String toString() {
        return "col1:" + col1 + " " + "col2:" + col2;
    }
}
