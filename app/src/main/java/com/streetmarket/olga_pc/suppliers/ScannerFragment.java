package com.streetmarket.olga_pc.suppliers;

/**
 * Created by Olga-PC on 5/12/2016.
 */

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.streetmarket.olga_pc.suppliers.autocomplete.SearchAdapter;
import com.streetmarket.olga_pc.suppliers.beans.KeyValue;
import com.streetmarket.olga_pc.suppliers.dao.DatabaseHandler;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScannerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ScannerFragment extends Fragment implements CompoundBarcodeView.TorchListener {

    private OnFragmentInteractionListener mListener;
    private static final String TAG = "avivinventory.ScannerFragment";

    private CaptureManager capture;
    private CompoundBarcodeView barcodeScannerView;
    private FloatingActionButton switchFlashlightButton;
    private Button btnSearch, btnClear;
    private EditText etSearchByName, etSearchByCode;

    private static final String ARG_PARAM_QR = "paramQR";
    private static final String ARG_PARAM_BAR = "paramBAR";
    private int paramQR;
    private int paramBAR;
    private boolean torch;
    private int supplier=-1;
    private AutoCompleteTextView searchView;


    public ScannerFragment() {
        // Required empty public constructor

    }

    public static ScannerFragment newInstance(int paramBar, int paramQr) {
        ScannerFragment fragment = new ScannerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_QR, paramQr);
        args.putInt(ARG_PARAM_BAR, paramBar);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paramQR = getArguments().getInt(ARG_PARAM_QR);
            paramBAR = getArguments().getInt(ARG_PARAM_BAR);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        btnClear = (Button) view.findViewById(R.id.btnClear);

        etSearchByCode = (EditText) view.findViewById(R.id.etSearchByCode);
        etSearchByName = (EditText) view.findViewById(R.id.etSearchByName);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(v);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v);
                    return true;
                }
                return false;
            }
        };

        etSearchByCode.setOnEditorActionListener(onEditorActionListener);
        etSearchByName.setOnEditorActionListener(onEditorActionListener);

       searchView =  (AutoCompleteTextView) view.findViewById(R.id.search_box);
        SearchAdapter searchAdapter=new SearchAdapter(getActivity(),R.id.search_box,new DatabaseHandler.Suppliers(getActivity()));
        searchView.setAdapter(searchAdapter);

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>=0) {
                    KeyValue kv= (KeyValue) parent.getItemAtPosition(position);
                    supplier=kv!=null?kv.getKey():-1;
                }else{
                    supplier=-1;
                }
            }
        });


        barcodeScannerView = (CompoundBarcodeView) view.findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);

        switchFlashlightButton = (FloatingActionButton) view.findViewById(R.id.switch_flashlight);
        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        } else {
            switchFlashlightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchFlashlight(v);
                }
            });
        }
        capture = new CaptureManager(getActivity(), barcodeScannerView);
        capture.initializeFromIntent(getActivity().getIntent(), savedInstanceState);
        capture.decode();
        return view;
    }


    /**
     * Check if the device's camera has a Flashlight.
     *
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getActivity().getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void switchFlashlight(View view) {
        torch = !torch;
        if (torch) {
            barcodeScannerView.setTorchOn();

        } else {
            barcodeScannerView.setTorchOff();
        }
    }

    @Override
    public void onTorchOn() {
       switchFlashlightButton.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{ContextCompat.getColor(getActivity(), R.color.torchOn)}));
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{ContextCompat.getColor(getActivity(), R.color.torchOff)}));
    }


    private void clear() {
        etSearchByCode.setText("");
        etSearchByName.setText("");
        searchView.setText("");
        supplier=-1;
    }


    private void performSearch(View v) {
        Utilities.hideKeyboard(v);
        capture.onPause();
        if (validate(etSearchByCode, 3) && validate(etSearchByName, 3)&&validate(searchView, 3)) {
            ItemSearchCriteria c = new ItemSearchCriteria();
            c.setCode(etSearchByCode.getText().toString());
            c.setName(etSearchByName.getText().toString());
            c.setSupplier(supplier);
            onSearchCodePressed(c);
        } else
            capture.onResume();
    }

    private boolean validate(EditText editText, int limit) {
        int l = editText.getText().toString().trim().length();
        boolean empty = l > 0 && l < limit;
        if (empty) {
            Snackbar.make(editText, getString(R.string.invalid_character_count), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            editText.requestFocus();
            editText.selectAll();
        }
        return !empty;
    }

    public void onSearchCodePressed(ItemSearchCriteria data) {
        // getContent(data);
        if (mListener != null) {
            mListener.onScanerFragmentInteraction(data);
        }
    }

    private void getContent(ItemSearchCriteria data) {

    }


    @Override
    public void onResume() {
        super.onResume();

        capture.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (capture != null) {
            capture.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (capture != null)
            capture.onDestroy();
    }

    public void resumeCapture() {
        if (capture != null) {
            capture.onResume();
        }
    }

    public void pauseCapture() {
        if (capture != null) {
            capture.onPause();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onScanerFragmentInteraction(ItemSearchCriteria criteria);
    }


}

