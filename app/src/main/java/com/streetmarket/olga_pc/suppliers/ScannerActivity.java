package com.streetmarket.olga_pc.suppliers;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.streetmarket.olga_pc.suppliers.async.InitTask;
import com.streetmarket.olga_pc.suppliers.async.ItemSearchTask;
import com.streetmarket.olga_pc.suppliers.beans.Item;
import com.streetmarket.olga_pc.suppliers.dao.DatabaseHandler;

import java.util.ArrayList;

public class ScannerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ScannerFragment.OnFragmentInteractionListener, ItemListFragment.OnFragmentInteractionListener {
    private static final String TAG = "ScannerActivity";
    private static final String INIT_ACTION_FOR_INTENT_CALLBACK = "INIT_ACTION_FOR_INTENT_CALLBACK";
    private static final String SEARCH_ACTION_FOR_INTENT_CALLBACK = "SEARCH_ACTION_FOR_INTENT_CALLBACK";
    public static final String SCAN_ACTION_FOR_INTENT_CALLBACK = "SCAN_ACTION_FOR_INTENT_CALLBACK";
    private final static int SCAN_BAR = 1;
    private final static int SCAN_QR = 2;
    private static final int TAB_RESULTS = 2;
    private static final int TAB_SEARCH = 1;
    private static final int TAB_SCAN = 0;

    private DrawerLayout mDrawerLayout;
    private static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private ProgressDialog progress;
    public final static String ZFETCH_PARAM_PINTENT = "pendingIntent";
    public final static String ZFETCH_RESULT = "zfetchresult";
    public final static int ZFETCH_STATUS_START = 100;
    public final static int ZFETCH_STATUS_FINISH = 300;
    public final static int ZFETCH_TASK_CODE = 999;

    private ViewPager viewPager;
    private ItemListFragment itemListFragment;
    private boolean zFetchRunning;
    private ScannerFragment scannerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);


        //final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        //ab.setDisplayHomeAsUpEnabled(true);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        int numTab = tab.getPosition();
                        if (scannerFragment != null) {
                            if (numTab == 0) {

                                scannerFragment.resumeCapture();

                            } else {
                                scannerFragment.onPause();
                            }
                        }
                    }
                });



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            // disable going back to the MainActivity
            moveTaskToBack(true);
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_load:
                loadData();
                return true;
            case android.R.id.home:
                Utilities.hideKeyboard(this.viewPager);
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void loadData() {

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_status) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
            i.putExtra(Intent.EXTRA_TEXT, "body of email");
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(scannerFragment = ScannerFragment.newInstance(SCAN_BAR, SCAN_QR), "Поиск");
        adapter.addFragment(itemListFragment = ItemListFragment.newInstance(), "Результат");
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Ловим сообщения об окончании задач
        if (requestCode == ZFETCH_TASK_CODE) {
            switch (resultCode) {
                case ZFETCH_STATUS_START:
                    zFetchRunning = true;
                    break;
            }

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

    @Override
    public void onScanerFragmentInteraction(ItemSearchCriteria criteria) {
        getContent(criteria);
    }

    private void getContent(ItemSearchCriteria c) {
        // the request
        if (c != null) {
            try {
                final ItemSearchTask task = new ItemSearchTask(this, SEARCH_ACTION_FOR_INTENT_CALLBACK);
                task.execute(c);
                progress = new ProgressDialog(this);
                progress.setTitle(getString(R.string.loading));
                progress.setIndeterminate(true);
                progress.setMessage(getString(R.string.waiting_for_result));
                progress.setCancelable(false);
                progress.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        task.cancel(true);
                    }
                });
                progress.show();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public void setProgressMessage(String msg) {
        if (progress != null) {
            progress.setMessage(msg);
        }
    }


    /**
     * Our Broadcast Receiver. We get notified that the data is ready this way.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // clear the progress indicator
            if (progress != null) {
                progress.dismiss();
            }
            String action = intent.getAction();
            switch (action) {
                case INIT_ACTION_FOR_INTENT_CALLBACK:
                    boolean response = intent.getBooleanExtra(InitTask.RESPONSE, false);
                    if (response) {

                        itemListFragment.updateList(true);
                        viewPager.setCurrentItem(TAB_RESULTS, true);
                        Toast.makeText(ScannerActivity.this, "Item list loaded successfully !", Toast.LENGTH_SHORT).show();

                    } else {
                        showEmptySearchResult();
                    }
                    break;
                case SEARCH_ACTION_FOR_INTENT_CALLBACK:
                    Integer result = intent.getIntExtra(ItemSearchTask.RESPONSE, ItemSearchCriteria.FILTERED_NOT_FOUND);
                    switch (result) {
                        case ItemSearchCriteria.FILTERED_FOUND:
                            itemListFragment.updateList(false);
                            viewPager.setCurrentItem(TAB_RESULTS, true);
                            break;
                        case ItemSearchCriteria.FILTERED_NOT_FOUND:
                            itemListFragment.updateList(false);
                            showEmptySearchResult();
                            break;
                        default:
                            itemListFragment.updateList(true);
                            viewPager.setCurrentItem(TAB_RESULTS, true);
                    }


                    break;
                case SCAN_ACTION_FOR_INTENT_CALLBACK:
                    String code = intent.getStringExtra(ItemSearchTask.RESPONSE);
                    ItemSearchCriteria c = new ItemSearchCriteria();
                    c.setCode(code);
                    getContent(c);
                    break;
            }

        }

        private void showEmptySearchResult() {
            android.support.v7.app.AlertDialog ad = new AlertDialog.Builder(ScannerActivity.this).setPositiveButton(getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setMessage(getString(R.string.alertClientConnectionError)).show();
        }
    };


    private void showItem(String code) {
        /*Intent intentItem = new Intent(this, ItemActivity.class);
        intentItem.putExtra("itemCode", code);
        startActivity(intentItem);*/
    }


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(INIT_ACTION_FOR_INTENT_CALLBACK));
        registerReceiver(receiver, new IntentFilter(SEARCH_ACTION_FOR_INTENT_CALLBACK));
        registerReceiver(receiver, new IntentFilter(SCAN_ACTION_FOR_INTENT_CALLBACK));
        if (viewPager.getCurrentItem() == TAB_RESULTS) {
            itemListFragment.updateList(false);
        }
        if (!zFetchRunning) {
            PendingIntent pi;
            Intent intent;

            // Создаем PendingIntent для ZFetchService
            pi = createPendingResult(ZFETCH_TASK_CODE, new Intent(), 0);
            // Создаем Intent для вызова сервиса, кладем туда созданный PendingIntent
           /* intent = new Intent(this, DataFetchService.class).putExtra(ZFETCH_PARAM_PINTENT, pi);
            // стартуем сервис
            startService(intent);*/
        }
        // embeddedScanBar();
        DatabaseHandler.Settings settings = new DatabaseHandler.Settings(this);
        String isLoaded = settings.getValue("dataLoaded");
        boolean result = "true".equalsIgnoreCase(isLoaded);
        if (!result) {
            String fileName = "info";
            final InitTask task = new InitTask(this, INIT_ACTION_FOR_INTENT_CALLBACK);
            task.execute(fileName);

            progress = new ProgressDialog(ScannerActivity.this);
            progress.setIndeterminate(true);
            progress.setMessage(getString(R.string.authenticating));
            progress.setCancelable(false);
            progress.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    task.cancel(true);

                }
            });
            progress.show();
        } else {
            Intent intent = getIntent();
            String action = intent.getAction();
            if ("com.google.zxing.client.android.SCAN".equals(action)) {

                String str = settings.getValue("filtered");
                if (str != null && Integer.parseInt(str) > 0) {
                    viewPager.setCurrentItem(TAB_RESULTS, true);
                }
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);

    }

    @Override
    protected void onStop() {
        super.onStop();
        //unregisterReceiver(receiver);
    }

    @Override
    public void onItemListFragmentInteraction(String code) {
        showItem(code);
    }


}
