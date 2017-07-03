package com.streetmarket.olga_pc.suppliers;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.streetmarket.olga_pc.suppliers.async.ItemSearchTask;
import com.streetmarket.olga_pc.suppliers.beans.Item;
import com.streetmarket.olga_pc.suppliers.dao.DatabaseHandler;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHandler.Results results=new DatabaseHandler.Results(this);
        results.clear();
        DatabaseHandler.Settings settings=new DatabaseHandler.Settings(this);
        settings.setValue("filtered",-1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        embeddedScanBar();
    }

    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if ("com.google.zxing.client.android.SCAN".equals(intent.getAction())) {
            IntentResult scan = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scan != null) {
                String contents = scan.getContents();
                if (contents != null) {
                    String format = scan.getFormatName();
                    Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG).show();
                    getContent(contents);
                }
            }
        }
    }
    private void showEmptySearchResult() {
        android.support.v7.app.AlertDialog ad = new AlertDialog.Builder(this).setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setMessage(getString(R.string.alertClientConnectionError)).show();
    }
    private void getContent(String code) {
        DatabaseHandler.Items items=new DatabaseHandler.Items(this);
        DatabaseHandler.Settings settings=new DatabaseHandler.Settings(this);
        ItemSearchCriteria criteria=new ItemSearchCriteria();
        criteria.setCode(code);
        if(items.filter(criteria,false)) {
            settings.setValue("filtered", 1);
        }else{
            showEmptySearchResult();
        }
     }

    public void embeddedScanBar() {
        try {
            // Check that the device will let you use the camera
            PackageManager pm = getPackageManager();

            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setCaptureActivity(ScannerActivity.class);
                integrator.setOrientationLocked(false);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt("Сканирование штрих-кода");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            } else {
                showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
            }
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    //alert dialog for downloadDialog
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }



}
