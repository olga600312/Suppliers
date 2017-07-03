package com.streetmarket.olga_pc.suppliers.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Olga-PC on 5/12/2016.
 */
public class DAOHelper extends SQLiteOpenHelper {
    private final static String TAG = "DAOHelper";

    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "streetmarket_supply.db";


    DAOHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ITEMS.TABLE_CREATE);
        Log.d(TAG, ITEMS.TABLE_CREATE);

        db.execSQL(RESULTS.TABLE_CREATE);
        Log.d(TAG, RESULTS.TABLE_CREATE);

        db.execSQL(EXTRA.TABLE_CREATE);
        Log.d(TAG, EXTRA.TABLE_CREATE);




        GRP g = new GRP();
        db.execSQL(g.TABLE_CREATE);
        Log.d(TAG, g.TABLE_CREATE);

        DEPARTMENT d = new DEPARTMENT();
        db.execSQL(d.TABLE_CREATE);
        Log.d(TAG, d.TABLE_CREATE);


        SUPPLIER s = new SUPPLIER();
        db.execSQL(s.TABLE_CREATE);
        Log.d(TAG, s.TABLE_CREATE);


        db.execSQL(SETTINGS.TABLE_CREATE);
        Log.d(TAG, SETTINGS.TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    ///////////////// Table interfaces //////////////////////////////
    public interface ITEMS {
        String TABLE_NAME = "items";

        //public final static String ID = BaseColumns._ID;
        String BARCODE = "barcode";
        String NAME = "name";
        String PRICE = "price";
        String PRICE_NETTO = "price_netto";
        String COST = "cost";
        String COUNT = "count";
        String GRP = "grp";
        String DPRT = "dprt";
        String SUPPLIER = "supplier";
        String TAX = "tax";


        String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        BARCODE + " TEXT NOT NULL PRIMARY KEY, " +
                        NAME + " TEXT NOT NULL, " +
                        PRICE + " REAL NOT NULL, " +
                        PRICE_NETTO + " REAL NOT NULL, " +
                        COST + " REAL NOT NULL DEFAULT 0," +
                        COUNT + " REAL NOT NULL DEFAULT 0," +
                        GRP + " INTEGER NOT NULL DEFAULT 0," +
                        DPRT + " INTEGER NOT NULL DEFAULT 0," +
                        SUPPLIER + " INTEGER NOT NULL DEFAULT 0," +
                        TAX + " INTEGER NOT NULL DEFAULT 0"+
                        ");";
        public static final String[] COLUMNS = {
                BARCODE, NAME, PRICE,PRICE_NETTO, COST,COUNT,GRP,DPRT,SUPPLIER,TAX
        };
    }

    public interface RESULTS {
        String TABLE_NAME = "results";
        //public final static String ID = BaseColumns._ID;
        String BARCODE = "barcode";
        String NAME = "name";
        String PRICE = "price";
        String PRICE_NETTO = "price_netto";
        String COST = "cost";
        String COUNT = "count";
        String GRP = "grp";
        String DPRT = "dprt";
        String SUPPLIER = "supplier";
        String TAX = "tax";


        String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        BARCODE + " TEXT NOT NULL PRIMARY KEY, " +
                        NAME + " TEXT NOT NULL, " +
                        PRICE + " REAL NOT NULL, " +
                        PRICE_NETTO + " REAL NOT NULL, " +
                        COST + " REAL NOT NULL DEFAULT 0," +
                        COUNT + " REAL NOT NULL DEFAULT 0," +
                        GRP + " INTEGER NOT NULL DEFAULT 0," +
                        DPRT + " INTEGER NOT NULL DEFAULT 0," +
                        SUPPLIER + " INTEGER NOT NULL DEFAULT 0," +
                        TAX + " INTEGER NOT NULL DEFAULT 0"+
                        ");";
        public static final String[] COLUMNS = {
                BARCODE, NAME, PRICE,PRICE_NETTO, COST,COUNT,GRP,DPRT,SUPPLIER,TAX
        };
    }


    public interface EXTRA {
        String TABLE_NAME = "extra";

        String BARCODE = "barcode";
        String NAME = "name";
        String VALUE = "value";


        String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        BARCODE + " TEXT," +
                        NAME + " TEXT NOT NULL, " +
                        VALUE + " TEXT NOT NULL);" +
                        " CREATE INDEX idx_barcode ON " + TABLE_NAME +
                        "(" + BARCODE + ");";
    }

    public static abstract class MASTER {
        public String TABLE_NAME;
        public String ID = "id";
        public String NAME = "name";


        public String TABLE_CREATE;

        public MASTER(String TABLE_NAME) {
            this.TABLE_NAME = TABLE_NAME;
            TABLE_CREATE =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                            ID + "  INTEGER PRIMARY KEY, " +
                            NAME + " TEXT NOT NULL);";
        }
    }

    public static class GRP extends MASTER {
        public GRP() {
            super("groups");
        }
    }


    public static class DEPARTMENT extends MASTER {
        public DEPARTMENT() {
            super("departments");
        }
    }

    public static class SUPPLIER extends MASTER {
        public SUPPLIER() {
            super("suppliers");
        }
    }






    public interface SETTINGS {
        String TABLE_NAME = "settings";

        String ID = "id";
        String NAME = "name";


        String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ID + "  TEXT PRIMARY KEY, " +
                        NAME + " TEXT NOT NULL);";
    }



}

