package ps.room.BoltHeadClearanceFree;

import android.provider.BaseColumns;

final class DatabaseTablesContract {
    private DatabaseTablesContract(){

    }

    static final class FastenerTypesEntry implements BaseColumns{
        static final String ID = "ID";
        static final String FASTENER_TYPE = "FastenerType";
        static final String IMAGE = "Image";
    }

    static final class FastenerDescriptionEntry implements BaseColumns{
        static final String FASTENER_TYPE = "FastenerType";
        static final String FASTENER_TYPE_ID = "FastenerTypeId";
        static final String ID = "ID";
        static final String FASTENER_NAME = "FastenerName";
        static final String STANDARD = "Standard";
        static final String STANDARD_DETAILED = "Standard_detailed";
        static final String IMAGE = "Image";
        static final String IS_FAVORITE = "Is_favorite";
        static final String IS_AVAILABLE = "Available";
    }

    static final class FastenerSizesEntry implements BaseColumns{
        static final String FASTENER_TYPE = "FastenerType";
        static final String ID = "ID";
        static final String FASTENER_NAME = "FastenerName";
        static final String IMAGE = "Image";
        static final String MAIN_SIZE = "MainSize";
        static final String A_MIN_SIZE = "A_MIN_SIZE";
        static final String A_SIZE = "A_SIZE";
        static final String A_MAX_SIZE = "A_MAX_SIZE";
        static final String B_SIZE = "B_SIZE";
        static final String C_SIZE = "C_SIZE";
    }
}
