package com.streetmarket.olga_pc.suppliers.async;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.streetmarket.olga_pc.suppliers.ItemSearchCriteria;
import com.streetmarket.olga_pc.suppliers.ScannerActivity;
import com.streetmarket.olga_pc.suppliers.beans.Item;
import com.streetmarket.olga_pc.suppliers.dao.DatabaseHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Olga-PC on 5/12/2016.
 */
public class ItemSearchTask extends AsyncTask<ItemSearchCriteria, String, Integer> {
    private static final String TAG = "AASearchTask";
    public static final String RESPONSE = "response";



    private ScannerActivity mContext;
    private String mActionTag;

    public ItemSearchTask(ScannerActivity context, String action) {
        mContext = context;
        mActionTag = action;
    }


    @Override
    protected Integer doInBackground(ItemSearchCriteria... params) {
        ItemSearchCriteria criteria = params.length > 0 ? params[0] : null;
        int result=ItemSearchCriteria.NOT_FILTERED;
        if (criteria != null&&!criteria.isEmpty()) {
            try {
                DatabaseHandler.Items items=new DatabaseHandler.Items(mContext);
                result=items.filter(criteria,true)?ItemSearchCriteria.FILTERED_FOUND:ItemSearchCriteria.FILTERED_NOT_FOUND;


            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                result=ItemSearchCriteria.FILTERED_NOT_FOUND;
            }
        }
        return result;
    }


    @Override
    protected void onPostExecute(Integer result) {
        Log.d(TAG, "RESULT = " + result);
        if (!isCancelled()) {
            Intent intent = new Intent(mActionTag);
            if (result != null) {
                intent.putExtra(RESPONSE, result);
            } else
                intent.putExtra(RESPONSE, ItemSearchCriteria.FILTERED_NOT_FOUND);
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

