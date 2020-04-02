package ps.room.BoltHeadClearanceFree;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FastenerType {
    private String id;
    private String fastenerType;
    private Bitmap imageBitmap;

    FastenerType(String id, String fastenerType, Bitmap imageBitmap){
        this.id = id;
        this.fastenerType = fastenerType;
        this.imageBitmap = imageBitmap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String getFastenerType() {
        return fastenerType;
    }

    public void setFastenerType(String fastenerType) {
        this.fastenerType = fastenerType;
    }

    Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(byte[] image) {
        this.imageBitmap = BitmapFactory.decodeByteArray(image,0,image.length);
    }
}

