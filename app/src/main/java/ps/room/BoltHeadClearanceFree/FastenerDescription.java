package ps.room.BoltHeadClearanceFree;

import android.graphics.Bitmap;

public class FastenerDescription {
    private String fastenerType;
    private String fastenerTypeId;
    private String id;
    private String fastenerName;
    private String standard;
    private String standard_detailed;
    private Bitmap image;
    private int isFavorite;
    private int isAvailable;

    FastenerDescription(String fastenerType, String fastenerTypeId, String id, String fastenerName, String standard, String standard_detailed, Bitmap image, int isFavorite, int isAvailable){
        this.setFastenerType(fastenerType);
        this.setFastenerTypeId(fastenerTypeId);
        this.setId(id);
        this.setFastenerName(fastenerName);
        this.setStandard(standard);
        this.setStandard_detailed(standard_detailed);
        this.setImage(image);
        this.setIsFavorite(isFavorite);
        this.setIsAvailable(isAvailable);
    }

    private void setIsAvailable(int isAvailable) {
        this.isAvailable = isAvailable;
    }

    int getIsAvailable(){
        return isAvailable;
    }

    public String getFastenerType() {
        return fastenerType;
    }

    private void setFastenerType(String fastenerType) {
        this.fastenerType = fastenerType;
    }

    String getFastenerTypeId() {
        return fastenerTypeId;
    }

    private void setFastenerTypeId(String fastenerTypeId) {
        this.fastenerTypeId = fastenerTypeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String getFastenerName() {
        return fastenerName;
    }

    private void setFastenerName(String fastenerName) {
        this.fastenerName = fastenerName;
    }

    public String getStandard() {
        return standard;
    }

    private void setStandard(String standard) {
        this.standard = standard;
    }

    String getStandard_detailed() {
        return standard_detailed;
    }

    private void setStandard_detailed(String standard_detailed) {
        this.standard_detailed = standard_detailed;
    }

    Bitmap getImage() {
        return image;
    }

    private void setImage(Bitmap image) {
        this.image = image;
    }

    int getIsFavorite() {
        return isFavorite;
    }

    private void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }
}
