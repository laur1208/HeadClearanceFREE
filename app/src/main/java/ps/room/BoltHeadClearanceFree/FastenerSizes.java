package ps.room.BoltHeadClearanceFree;

import android.graphics.Bitmap;

public class FastenerSizes {
    private String fastenerType;
    private int id;
    private String fastenerName;
    private Bitmap image;
    private String mainSize;
    private double aMinSize;
    private double aSize;
    private double aMaxSize;
    private double bSize;
    private double cSize;
    private double dSize;

    FastenerSizes(String fastenerType, int id, String fastenerName, Bitmap image, String mainSize, double aMinSize, double aSize, double aMaxSize, double bSize, double cSize, double dSize){
        this.setFastenerType(fastenerType);
        this.setId(id);
        this.setFastenerName(fastenerName);
        this.setImage(image);
        this.setMainSize(mainSize);
        this.setaMinSize(aMinSize);
        this.setaSize(aSize);
        this.setaMaxSize(aMaxSize);
        this.setbSize(bSize);
        this.setcSize(cSize);
        this.setdSize(dSize);
    }

    public String getFastenerType() {
        return fastenerType;
    }

    private void setFastenerType(String fastenerType) {
        this.fastenerType = fastenerType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFastenerName() {
        return fastenerName;
    }

    private void setFastenerName(String fastenerName) {
        this.fastenerName = fastenerName;
    }

    public Bitmap getImage() {
        return image;
    }

    private void setImage(Bitmap image) {
        this.image = image;
    }

    String getMainSize() {
        return mainSize;
    }

    private void setMainSize(String mainSize) {
        this.mainSize = mainSize;
    }

    double getaMinSize() {
        return aMinSize;
    }

    private void setaMinSize(double aMinSize) {
        this.aMinSize = aMinSize;
    }

    double getaSize() {
        return aSize;
    }

    private void setaSize(double aSize) {
        this.aSize = aSize;
    }

    double getaMaxSize() {
        return aMaxSize;
    }

    private void setaMaxSize(double aMaxSize) {
        this.aMaxSize = aMaxSize;
    }

    double getbSize() {
        return bSize;
    }

    private void setbSize(double bSize) {
        this.bSize = bSize;
    }

    double getcSize() {
        return cSize;
    }

    private void setcSize(double cSize) {
        this.cSize = cSize;
    }

    double getdSize() {
        return dSize;
    }

    private void setdSize(double dSize) {
        this.dSize = dSize;
    }
}
