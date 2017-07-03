package com.streetmarket.olga_pc.suppliers.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.streetmarket.olga_pc.suppliers.ItemSearchCriteria;
import com.streetmarket.olga_pc.suppliers.beans.Item;
import com.streetmarket.olga_pc.suppliers.beans.KeyValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Olga-PC on 5/12/2016.
 */
public class DatabaseHandler {
    private final static String TAG = "DatabaseHelper";


    /**
     * CRUD
     * Create
     * Retrieve
     * Update
     * Delete
     */


    public abstract static class Master<T extends DAOHelper.MASTER> {
        private DAOHelper helper;
        protected T t;

        public Master(Context context, T t) {
            helper = new DAOHelper(context);
            this.t = t;
        }

        public T getHelper() {
            return t;
        }

        public Cursor getCursor() {
            SQLiteDatabase database = helper.getReadableDatabase();
            return database.query(t.TABLE_NAME, null, null, null, null, null, null);
        }

        public Cursor getRawCursor() {
            SQLiteDatabase database = helper.getReadableDatabase();
            return database.query(t.TABLE_NAME, new String[]{
                    "rowid _id",
                    t.ID, t.NAME
            }, null, null, null, null, null);
        }

        public String getValue(int key) {
            String result = null;
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(t.TABLE_NAME);
            queryBuilder.appendWhere(t.ID + " = " + key);
            SQLiteDatabase db = helper.getReadableDatabase();
            String str = queryBuilder.buildQuery(new String[]{t.NAME}, null, null, null, null, null);
            Cursor cursor = queryBuilder.query(db, new String[]{t.NAME}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                final int valueColumn = cursor.getColumnIndex(t.NAME);
                do {
                    result = cursor.getString(valueColumn);
                }
                while (result == null && cursor.moveToNext());

            }
            cursor.close();
            return result;
        }

        public boolean setValue(int key, String value) {
            SQLiteDatabase database = helper.getWritableDatabase();
            boolean fl = false;
            if (value != null) {
                ContentValues values = new ContentValues();
                values.put(t.ID, key);
                values.put(t.NAME, value);
                fl = database.insert(t.TABLE_NAME, null, values) > 0;
            } else {
                fl = database.delete(t.TABLE_NAME, t.ID + "=" + key, null) > 0;
            }
            return fl;
        }

        public boolean setValue(Collection<KeyValue> set) {
            SQLiteDatabase database = helper.getWritableDatabase();
            database.delete(t.TABLE_NAME, null, null);
            ContentValues values = new ContentValues();
            boolean fl = true;
            for (KeyValue k : set) {
                values.put(t.ID, k.getKey());
                values.put(t.NAME, k.getValue());
                fl = fl && database.insert(t.TABLE_NAME, null, values) > 0;
                values.clear();
            }
            return fl;
        }


        public boolean replaceValue(int key, String value) {
            SQLiteDatabase database = helper.getWritableDatabase();
            boolean fl;
            if (value != null) {
                ContentValues values = new ContentValues();
                values.put(t.ID, key);
                values.put(t.NAME, value);
                fl = database.replace(t.TABLE_NAME, null, values) > 0;
            } else {
                fl = database.delete(t.TABLE_NAME, t.ID + "=" + key, null) > 0;
            }
            return fl;
        }

        public abstract String getName();

        public ArrayList<KeyValue> retrive() {
            SQLiteDatabase db = helper.getReadableDatabase();
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(t.TABLE_NAME);
            Cursor cursor = queryBuilder.query(db, new String[]{t.ID, t.NAME}, null, null, null, null, null);
            ArrayList<KeyValue> arr = new ArrayList<>();
            if (cursor.moveToFirst()) {
                final int valueColumn = cursor.getColumnIndex(t.NAME);
                final int keyColumn = cursor.getColumnIndex(t.ID);
                do {
                    KeyValue kv = new KeyValue(cursor.getInt(keyColumn), cursor.getString(valueColumn));
                    arr.add(kv);
                }
                while (cursor.moveToNext());

            }
            cursor.close();
            return arr;
        }
    }

    public static class Groups extends Master<DAOHelper.GRP> {

        public Groups(Context context) {
            super(context, new DAOHelper.GRP());
        }

        @Override
        public String getName() {
            return "groups";
        }
    }

    public static class Departments extends Master<DAOHelper.DEPARTMENT> {

        public Departments(Context context) {
            super(context, new DAOHelper.DEPARTMENT());
        }

        @Override
        public String getName() {
            return "departments";
        }
    }

    public static class Suppliers extends Master<DAOHelper.SUPPLIER> {

        public Suppliers(Context context) {
            super(context, new DAOHelper.SUPPLIER());
        }

        @Override
        public String getName() {
            return "suppliers";
        }
    }


    public static class Settings {
        private DAOHelper helper;

        public Settings(Context context) {
            helper = new DAOHelper(context);
        }

        public String getValue(String key) {
            String result = null;
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(DAOHelper.SETTINGS.TABLE_NAME);
            queryBuilder.appendWhere(DAOHelper.SETTINGS.ID + " = '" + key + "'");
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = queryBuilder.query(db, new String[]{DAOHelper.SETTINGS.NAME}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                final int valueColumn = cursor.getColumnIndex(DAOHelper.SETTINGS.NAME);
                do {
                    result = cursor.getString(valueColumn);
                }
                while (result == null && cursor.moveToNext());

            }
            cursor.close();
            return result;
        }

        public <T> boolean setValue(String key, T value) {
            SQLiteDatabase database = helper.getWritableDatabase();
            boolean fl = false;
            if (value != null) {
                ContentValues values = new ContentValues();
                values.put(DAOHelper.SETTINGS.ID, key);
                values.put(DAOHelper.SETTINGS.NAME, String.valueOf(value));
                fl = database.replace(DAOHelper.SETTINGS.TABLE_NAME, null, values) > 0;
            } else {
                fl = database.delete(DAOHelper.SETTINGS.TABLE_NAME, DAOHelper.SETTINGS.ID + "='" + key + "'", null) > 0;
            }
            return fl;
        }


        public <T> boolean replaceValue(String key, T value) {
            SQLiteDatabase database = helper.getWritableDatabase();
            boolean fl;
            if (value != null) {
                ContentValues values = new ContentValues();
                values.put(DAOHelper.SETTINGS.ID, key);
                values.put(DAOHelper.SETTINGS.NAME, String.valueOf(value));
                fl = database.replace(DAOHelper.SETTINGS.TABLE_NAME, null, values) > 0;
            } else {
                fl = database.delete(DAOHelper.SETTINGS.TABLE_NAME, DAOHelper.SETTINGS.ID + "='" + key + "'", null) > 0;
            }
            return fl;
        }

    }

    public static class Items {
        private DAOHelper helper;
        protected String tableName;
        Context context;

        public Items(Context context) {
            helper = new DAOHelper(context);
            tableName = DAOHelper.ITEMS.TABLE_NAME;
            this.context = context;
        }

        public Cursor getCursor() {
            SQLiteDatabase database = helper.getReadableDatabase();
            return database.query(tableName, null, null, null, null, null, null);
        }

        public ArrayList<Item> retrieve(boolean extra) {
            ArrayList<Item> list = new ArrayList<>();
            SQLiteDatabase database = helper.getReadableDatabase();
            Cursor cursor = database.query(tableName, null, null, null, null, null, DAOHelper.ITEMS.COUNT + " DESC");
            if (cursor.moveToFirst()) {
                do {
                    Item item = retrive(cursor, extra);
                    list.add(item);
                }
                while (cursor.moveToNext());

            }
            cursor.close();
            return list;
        }

        public Item retrieveItem(String code, boolean extra) {
            Item item = null;
            SQLiteDatabase database = helper.getReadableDatabase();
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(tableName);
            queryBuilder.appendWhere(DAOHelper.ITEMS.BARCODE + " = '" + code + "'");
            Cursor cursor = queryBuilder.query(database, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    item = retrive(cursor, extra);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
            return item;
        }

        public boolean filter(ItemSearchCriteria criteria, boolean like) {
            boolean fl = false;
            SQLiteDatabase database = helper.getReadableDatabase();
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(tableName);
            String code = criteria.getCode();
            String where = null;
            if (code != null && !code.isEmpty()) {
                where = DAOHelper.ITEMS.BARCODE + (like ? " LIKE '%" : "='") + code + "'";
            } else {
                String name = criteria.getName();
                if (name != null && !name.isEmpty()) {
                    where = DAOHelper.ITEMS.NAME + " LIKE '%" + name + "%'";
                }
            }
            if (criteria.getSupplier() > 0) {
                where = (where == null ? "" : where + " AND ") + DAOHelper.ITEMS.SUPPLIER + " =" + criteria.getSupplier();
            }
            if (where != null)
                queryBuilder.appendWhere(where);
            Cursor cursor = queryBuilder.query(database, null, null, null, null, null, null);
            DatabaseHandler.Results results = new DatabaseHandler.Results(context);
            results.clear();
            if (cursor.moveToFirst()) {
                do {
                    fl = results.create(retrive(cursor, false), false) != null;
                }
                while (cursor.moveToNext());
            }
            cursor.close();
            return fl;
        }

        public ArrayList<Item> retrieve(ItemSearchCriteria criteria, boolean like, boolean extra) {
            ArrayList<Item> arr = new ArrayList<>();
            SQLiteDatabase database = helper.getReadableDatabase();
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(tableName);
            String code = criteria.getCode();
            if (code != null && !code.isEmpty()) {
                queryBuilder.appendWhere(DAOHelper.ITEMS.BARCODE + " LIKE '%" + code + "'");
            } else {
                String name = criteria.getName();
                if (name != null && !name.isEmpty()) {
                    queryBuilder.appendWhere(DAOHelper.ITEMS.NAME + " LIKE '%" + name + "%'");
                }
            }
            Cursor cursor = queryBuilder.query(database, null, null, null, null, null, DAOHelper.ITEMS.COUNT + " DESC");
            if (cursor.moveToFirst()) {
                do {
                    Item item = retrive(cursor, extra);
                    arr.add(item);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
            return arr;
        }

        public Hashtable<String, Object> getExtra(String code) {
            Hashtable<String, Object> ht = new Hashtable<>();
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(DAOHelper.EXTRA.TABLE_NAME);
            queryBuilder.appendWhere(DAOHelper.EXTRA.BARCODE + " = '" + code + "'");
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = queryBuilder.query(db, new String[]{DAOHelper.EXTRA.NAME, DAOHelper.EXTRA.VALUE}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                final int nameColumn = cursor.getColumnIndex(DAOHelper.EXTRA.NAME);
                final int valueColumn = cursor.getColumnIndex(DAOHelper.EXTRA.VALUE);
                do {
                    ht.put(cursor.getString(nameColumn), cursor.getString(valueColumn));
                }
                while (cursor.moveToNext());

            }
            cursor.close();
            return ht;
        }


        private int version;

        /**
         * Must be called from Thread or AsyncTask . do in the background!!
         *
         * @param arr the items array
         * @return
         */
        public boolean init(Item arr[]) {
            SQLiteDatabase database = helper.getWritableDatabase();
            database.delete(tableName, null, null);
            database.delete(DAOHelper.EXTRA.TABLE_NAME, null, null);
            for (Item item : arr) {
                create(item, true);
            }
            return true;
        }

        public Item retrive(Cursor cursor, boolean extra) {

            Item item = new Item();
            item.setCode(cursor.getString(cursor.getColumnIndex(DAOHelper.ITEMS.BARCODE)));
            item.setName(cursor.getString(cursor.getColumnIndex(DAOHelper.ITEMS.NAME)));
            item.setPrice(cursor.getFloat(cursor.getColumnIndex(DAOHelper.ITEMS.PRICE)));
            item.setPriceNetto(cursor.getFloat(cursor.getColumnIndex(DAOHelper.ITEMS.PRICE_NETTO)));
            item.setCost(cursor.getFloat(cursor.getColumnIndex(DAOHelper.ITEMS.COST)));
            item.setCount(cursor.getFloat(cursor.getColumnIndex(DAOHelper.ITEMS.COUNT)));
            item.setGrp(cursor.getInt(cursor.getColumnIndex(DAOHelper.ITEMS.GRP)));
            item.setDprt(cursor.getInt(cursor.getColumnIndex(DAOHelper.ITEMS.DPRT)));
            item.setSupplier(cursor.getInt(cursor.getColumnIndex(DAOHelper.ITEMS.SUPPLIER)));
            item.setTax(cursor.getInt(cursor.getColumnIndex(DAOHelper.ITEMS.TAX)) == 1);

            if (extra) {
                item.setExtra(getExtra(item.getCode()));
            }
            return item;
        }

        public boolean replace(Item item) {
            SQLiteDatabase database = helper.getWritableDatabase();
            String code = item.getCode();
            ContentValues values = createValues(item);

            boolean fl = database.replaceOrThrow(tableName, null, values) > 0;
            database.delete(DAOHelper.EXTRA.TABLE_NAME, DAOHelper.EXTRA.BARCODE + "='" + code + "'", null);
            if (fl) {
                insertExtra(item, database, values, code);
            }
            return fl;

        }

        private long insertExtra(Item item, SQLiteDatabase database, ContentValues values, String code) {
            long res = 0;
            for (Iterator<Map.Entry<String, Object>> it = item.getExtra().entrySet().iterator(); it.hasNext() && res >= 0; ) {
                Map.Entry<String, Object> entry = it.next();
                values.clear();
                values.put(DAOHelper.EXTRA.BARCODE, code);
                values.put(DAOHelper.EXTRA.NAME, entry.getKey());
                values.put(DAOHelper.EXTRA.VALUE, entry.getValue() != null ? entry.getValue().toString() : "");
                res = database.insert(DAOHelper.EXTRA.TABLE_NAME, null, values);
            }
            return res;
        }

        public Item create(Item item, boolean extra) {
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues values = createValues(item);
            database.insert(tableName, null, values);
            if (extra) {
                insertExtra(item, database, values, item.getCode());
            }
            return item;
        }

        public void clear() {
            SQLiteDatabase database = helper.getWritableDatabase();
            database.delete(tableName, null, null);
        }

        private ContentValues createValues(Item item) {
            ContentValues values = new ContentValues();
            String code = item.getCode();
            values.put(DAOHelper.ITEMS.BARCODE, code);
            values.put(DAOHelper.ITEMS.NAME, item.getName());
            values.put(DAOHelper.ITEMS.PRICE, item.getPrice());
            values.put(DAOHelper.ITEMS.PRICE_NETTO, item.getPriceNetto());
            values.put(DAOHelper.ITEMS.COST, item.getCost());
            values.put(DAOHelper.ITEMS.COUNT, item.getCount());
            values.put(DAOHelper.ITEMS.GRP, item.getGrp());
            values.put(DAOHelper.ITEMS.DPRT, item.getDprt());
            values.put(DAOHelper.ITEMS.SUPPLIER, item.getSupplier());
            values.put(DAOHelper.ITEMS.TAX, item.isTax() ? 1 : 0);
            return values;
        }
    }


    public static class Results extends Items {
        private DAOHelper helper;

        public Results(Context context) {
            super(context);
            tableName = DAOHelper.RESULTS.TABLE_NAME;

        }


    }


}





