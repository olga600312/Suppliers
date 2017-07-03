package com.streetmarket.olga_pc.suppliers.async;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.streetmarket.olga_pc.suppliers.ScannerActivity;
import com.streetmarket.olga_pc.suppliers.beans.Item;
import com.streetmarket.olga_pc.suppliers.beans.KeyValue;
import com.streetmarket.olga_pc.suppliers.dao.DatabaseHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Olga-PC on 5/13/2016.
 */
public class InitTask extends AsyncTask<String, String, Boolean> {
    private static final String TAG = "AAInitTask";
    public static final String RESPONSE = "response";
    public static final String RESPONSE_MSG = "response_msg";

    private ScannerActivity mContext;
    private String mActionTag;
    private ProgressDialog progress;

    public InitTask(ScannerActivity context, String action) {
        mContext = context;
        mActionTag = action;
    }


    @Override
    protected Boolean doInBackground(String... params) {
        String fileName = params.length > 0 ? params[0] : null;
        DatabaseHandler.Settings settings = new DatabaseHandler.Settings(mContext);
        String isLoaded = settings.getValue("dataLoaded");
        boolean result = "true".equalsIgnoreCase(isLoaded);
        if (!result && fileName != null) {
            result = initMasterTable(fileName + "/suppliers.csv", new DatabaseHandler.Suppliers(mContext));

            if (result) {
                result = initItems(fileName);
            }
            if (result) {
                result = initMasterTable(fileName + "/groups.csv", new DatabaseHandler.Groups(mContext));
            }
            if (result) {
                result = initMasterTable(fileName + "/departments.csv", new DatabaseHandler.Departments(mContext));
            }


            if (result) {
                settings.replaceValue("dataLoaded", "true");
            }
        }
        return result;
    }

    private boolean initItems(String fileName) {
        boolean result = false;
        try (InputStream json = mContext.getAssets().open(fileName + "/items.csv");
             BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));) {
            String str;
            publishProgress("Open data...");

            DatabaseHandler.Items items = new DatabaseHandler.Items(mContext);
            while ((str = in.readLine()) != null) {
                String[] arr = str.split(",");
                Item item = new Item();
                item.setCode(arr[0]);
                item.setName(arr[1]);
                item.setPrice(parseFloat(arr[2], 0));
                item.setPriceNetto(parseFloat(arr[3], 0));
                item.setCost(parseFloat(arr[4], 0));
                item.setCount(parseFloat(arr[5], 0));
                item.setGrp(parseInt(arr[6], 0));
                item.setDprt(parseInt(arr[7], 0));
                item.setSupplier(parseInt(arr[8], 0));
                item.setTax(parseInt(arr[9], 1) == 1);
                items.create(item,false);
                publishProgress("Create item "+item.getCode()+" ...");
            }

            result = true;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return result;
    }

    private float parseFloat(String s, float defaultValue) {
        float val = defaultValue;
        try {
            val = Float.parseFloat(s);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return val;
    }

    private int parseInt(String s, int defaultValue) {
        int val = defaultValue;
        try {
            val = Integer.parseInt(s);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return val;
    }

    private boolean initMasterTable(String fileName, DatabaseHandler.Master master) {
        publishProgress("Init " + master.getName() + "...");
        boolean result = false;
        try (InputStream json = mContext.getAssets().open(fileName);
             BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));) {
            String str;
            while ((str = in.readLine()) != null) {
                String[] arr = str.split(",");
                master.setValue(parseInt(arr[0], 0), arr[1]);
            }

            result = true;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return result;

    }


    @Override
    protected void onPostExecute(Boolean result) {
        if (!isCancelled()) {
            Intent intent = new Intent(mActionTag);
            intent.putExtra(RESPONSE, result);

            // broadcast the completion
            mContext.sendBroadcast(intent);
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (mContext != null && values != null && values.length > 0) {
            mContext.setProgressMessage(values[0]);
        }
    }
}
