package ps.room.BoltHeadClearanceFree;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ps.room.BoltHeadClearanceFree.DatabaseTablesContract.FastenerDescriptionEntry;
import ps.room.BoltHeadClearanceFree.DatabaseTablesContract.FastenerSizesEntry;
import ps.room.BoltHeadClearanceFree.DatabaseTablesContract.FastenerTypesEntry;

class DataManager {
    private static DataManager ourInstance = null;
    private List<FastenerType> fastenerTypes = new ArrayList<>();
    private List<FastenerDescription> fastenerDescriptionList = new ArrayList<>();
    private Map<String, FastenerSizes> mSizesHashMap =  new LinkedHashMap<>();
    private String fastenerName;
    private Bitmap fastenerImage;

    private DataManager() {
    }

    static DataManager getInstance() {
        if(ourInstance == null) {
            ourInstance = new DataManager();
        }
        return ourInstance;
    }

    static void loadFastenerTypes(Cursor cursor){
        int cursorIdPos = cursor.getColumnIndex(FastenerTypesEntry.ID);
        int cursorFastener = cursor.getColumnIndex(FastenerTypesEntry.FASTENER_TYPE);
        int cursorImage = cursor.getColumnIndex(FastenerTypesEntry.IMAGE);

        DataManager dm = getInstance();
        dm.fastenerTypes.clear();
        while(cursor.moveToNext()){
            String id = cursor.getString(cursorIdPos);
            String fastener_type = cursor.getString(cursorFastener);
            byte[] image = cursor.getBlob(cursorImage);
            Bitmap imgBitmap = BitmapFactory.decodeByteArray(image,0,image.length);

            FastenerType fastenerType = new FastenerType(id, fastener_type, imgBitmap);
            dm.fastenerTypes.add(fastenerType);
        }
        cursor.close();
    }

    static void loadFastenerDescriptionList(Cursor cursor){
        int fastenerTypePos = cursor.getColumnIndex(FastenerDescriptionEntry.FASTENER_TYPE);
        int fastenerTypeIdPos = cursor.getColumnIndex(FastenerDescriptionEntry.FASTENER_TYPE_ID);
        int idPos = cursor.getColumnIndex(FastenerDescriptionEntry.ID);
        int fastenerNamePos = cursor.getColumnIndex(FastenerDescriptionEntry.FASTENER_NAME);
        int standardPos = cursor.getColumnIndex(FastenerDescriptionEntry.STANDARD);
        int standard_detailedPos = cursor.getColumnIndex(FastenerDescriptionEntry.STANDARD_DETAILED);
        int imagePos = cursor.getColumnIndex(FastenerDescriptionEntry.IMAGE);
        int isFavoritePos = cursor.getColumnIndex(FastenerDescriptionEntry.IS_FAVORITE);
        int isAvailablePos = cursor.getColumnIndex(FastenerDescriptionEntry.IS_AVAILABLE);

        DataManager dm = getInstance();
        dm.fastenerDescriptionList.clear();
        while (cursor.moveToNext()){
            String fastenerType = cursor.getString(fastenerTypePos);
            String fastenerTypeId = cursor.getString(fastenerTypeIdPos);
            String id = cursor.getString(idPos);
            String fastenerName = cursor.getString(fastenerNamePos);
            String standard = cursor.getString(standardPos);
            String standardDetailed = cursor.getString(standard_detailedPos);
            byte[] image = cursor.getBlob(imagePos);
            Bitmap imgBitmap = BitmapFactory.decodeByteArray(image,0,image.length);
            int isFavorite = cursor.getInt(isFavoritePos);
            int isAvailable = cursor.getInt(isAvailablePos);

            dm.fastenerDescriptionList.add(new FastenerDescription(fastenerType,fastenerTypeId,id,fastenerName,standard,standardDetailed,imgBitmap,isFavorite,isAvailable));
        }
        cursor.close();
    }

    static void loadFastenerSizes(Cursor cursor){
        int fastenerTypePos = cursor.getColumnIndex(FastenerSizesEntry.FASTENER_TYPE);
        int idPOs = cursor.getColumnIndex(FastenerSizesEntry.ID);
        int fastenerNamePOs = cursor.getColumnIndex(FastenerSizesEntry.FASTENER_NAME);
        int imagePos = cursor.getColumnIndex(FastenerSizesEntry.IMAGE);
        int mainSizePos = cursor.getColumnIndex(FastenerSizesEntry.MAIN_SIZE);
        int aMinSizePos = cursor.getColumnIndex(FastenerSizesEntry.A_MIN_SIZE);
        int aSizePos = cursor.getColumnIndex(FastenerSizesEntry.A_SIZE);
        int aMaxSizePos = cursor.getColumnIndex(FastenerSizesEntry.A_MAX_SIZE);
        int bSizePos = cursor.getColumnIndex(FastenerSizesEntry.B_SIZE);
        int cSizePos = cursor.getColumnIndex(FastenerSizesEntry.C_SIZE);

        DataManager dm = getInstance();
        dm.mSizesHashMap.clear();
        dm.fastenerImage = null;
        dm.fastenerName = null;
        while (cursor.moveToNext()){
            String fastenerType = cursor.getString(fastenerTypePos);
            int id = cursor.getInt(idPOs);
            String fastenerName = cursor.getString(fastenerNamePOs);
            byte[] image = cursor.getBlob(imagePos);
            Bitmap imgBitmap = BitmapFactory.decodeByteArray(image,0,image.length);
            String mainSize = cursor.getString(mainSizePos);
            double aMinSize = cursor.getDouble(aMinSizePos);
            double aSize = cursor.getDouble(aSizePos);
            double aMaxSize = cursor.getDouble(aMaxSizePos);
            double bSize = cursor.getDouble(bSizePos);
            double cSize = cursor.getDouble(cSizePos);
            double dSize = calculateDSize(cSize, bSize);

            //dm.fastenerSizesList.add(new FastenerSizes(fastenerType,id,fastenerName,imgBitmap,mainSize,aMinSize,aSize,aMaxSize,bSize,cSize,dSize));
            dm.mSizesHashMap.put(mainSize, new FastenerSizes(fastenerType,id,null,null,mainSize,aMinSize,aSize,aMaxSize,bSize,cSize,dSize));
            dm.fastenerImage = imgBitmap;
            dm.fastenerName = fastenerName;
        }
        cursor.close();
    }

    private static double calculateDSize(double cSize, double bSize) {
        double angle = Math.PI * cSize / 2 / 180;
        double sinus = Math.sin(angle);
        double hypotenuse = bSize / 2 / sinus;
        DecimalFormat doubleFormat = new DecimalFormat("#.##");
        return Double.isInfinite(hypotenuse) ? 0 : Double.parseDouble(doubleFormat.format(Math.sqrt(hypotenuse * hypotenuse - bSize * bSize / 4)));
    }

    String getFastenerName(){return fastenerName;}
    Bitmap getFastenerImage(){return fastenerImage;}
    Map<String, FastenerSizes> getSizesHashMap(){return mSizesHashMap;}
    List<FastenerType> getFastenerTypes(){
        return fastenerTypes;
    }
    List<FastenerDescription> getFastenersList(){
        return fastenerDescriptionList;
    }
}
