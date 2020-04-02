package ps.room.BoltHeadClearanceFree;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    @SuppressLint("SdCardPath")
    private static String DB_PATH = "/data/data/ps.room.BoltHeadClearanceFree/databases/";
    private static String DB_NAME = "HeadClearanceFree";
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    @SuppressLint("StaticFieldLeak")
    private static MyDatabaseHelper mInstance;

    static synchronized MyDatabaseHelper getInstance(Context context){
        if (mInstance == null) {
        mInstance = new MyDatabaseHelper(context.getApplicationContext());
    }
        return mInstance;
    }

    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     *
     */
    private MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with own database.
     *
     */
    void createDataBase() {

        if(!checkDataBase()){
            SQLiteDatabase db_Read;

            // Creates empty database default system path
            db_Read = this.getReadableDatabase();
            db_Read.close();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Checks if the database already exist to avoid re-copying the file each
     * time whenever the application opened.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    /**
     * Copies database from local assets-folder to the just created empty
     * database in the system folder and from where it can be accessed and
     * handled using byte stream transferring.
     *
     */
    private void copyDataBase() throws IOException {

        // Open local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME + ".db");

        // Path of the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }
}