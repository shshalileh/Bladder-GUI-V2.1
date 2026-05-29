package com.ti.nfcdemo.activities;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ti.nfcdemo.R;
import com.ti.nfcdemo.activities.iso15693.Iso15693WriteTagActivity;
import com.ti.nfcdemo.activities.nfc.NfcBaseActivity;


public class MainActivity extends NfcBaseActivity {
    private EditText initialResistanceEditText;
    private EditText slopeEditText;
    private EditText calibrationConstantEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout_grid);
        initializeNfcAdapterAndIntentFilters();
        initialResistanceEditText = (EditText) findViewById(R.id.edit_initial_resistance);
        slopeEditText = (EditText) findViewById(R.id.edit_slope);
        calibrationConstantEditText = (EditText) findViewById(R.id.edit_calibration_constant);
        loadCalibrationFields();

        ImageButton bladderButton = (ImageButton) findViewById(R.id.button_bladder_volume);
        if (bladderButton != null) {
            bladderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveCalibrationFields();
                    Intent newintent = new Intent(getApplicationContext(), Iso15693WriteTagActivity.class);
                    newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(newintent);
                }
            });
        }
    }

    private void loadCalibrationFields() {
        SharedPreferences preferences = getSharedPreferences(Iso15693WriteTagActivity.PREFS_NAME, MODE_PRIVATE);
        double initialResistance = Double.longBitsToDouble(preferences.getLong(
                Iso15693WriteTagActivity.KEY_INITIAL_RESISTANCE,
                Double.doubleToLongBits(Iso15693WriteTagActivity.DEFAULT_INITIAL_RESISTANCE)));
        double slope = Double.longBitsToDouble(preferences.getLong(
                Iso15693WriteTagActivity.KEY_SLOPE,
                Double.doubleToLongBits(Iso15693WriteTagActivity.DEFAULT_SLOPE)));
        double calibrationConstant = Double.longBitsToDouble(preferences.getLong(
                Iso15693WriteTagActivity.KEY_CALIBRATION_CONSTANT,
                Double.doubleToLongBits(Iso15693WriteTagActivity.DEFAULT_CALIBRATION_CONSTANT)));
        if (initialResistanceEditText != null) {
            initialResistanceEditText.setText(String.valueOf(initialResistance));
        }
        if (slopeEditText != null) {
            slopeEditText.setText(String.valueOf(slope));
        }
        if (calibrationConstantEditText != null) {
            calibrationConstantEditText.setText(String.valueOf(calibrationConstant));
        }
    }

    private void saveCalibrationFields() {
        double initialResistance = parseInitialResistance();
        double slope = parseSlope();
        double calibrationConstant = parseCalibrationConstant();
        SharedPreferences.Editor editor = getSharedPreferences(
                Iso15693WriteTagActivity.PREFS_NAME, MODE_PRIVATE).edit();
        editor.putLong(Iso15693WriteTagActivity.KEY_INITIAL_RESISTANCE, Double.doubleToLongBits(initialResistance));
        editor.putLong(Iso15693WriteTagActivity.KEY_SLOPE, Double.doubleToLongBits(slope));
        editor.putLong(Iso15693WriteTagActivity.KEY_CALIBRATION_CONSTANT, Double.doubleToLongBits(calibrationConstant));
        editor.apply();
        if (initialResistanceEditText != null) {
            initialResistanceEditText.setText(String.valueOf(initialResistance));
        }
        if (slopeEditText != null) {
            slopeEditText.setText(String.valueOf(slope));
        }
        if (calibrationConstantEditText != null) {
            calibrationConstantEditText.setText(String.valueOf(calibrationConstant));
        }
    }

    private double parseInitialResistance() {
        if (initialResistanceEditText != null) {
            try {
                double value = Double.parseDouble(initialResistanceEditText.getText().toString());
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return Iso15693WriteTagActivity.DEFAULT_INITIAL_RESISTANCE;
    }

    private double parseSlope() {
        if (slopeEditText != null) {
            try {
                return Double.parseDouble(slopeEditText.getText().toString());
            } catch (NumberFormatException ignored) {
            }
        }
        return Iso15693WriteTagActivity.DEFAULT_SLOPE;
    }

    private double parseCalibrationConstant() {
        if (calibrationConstantEditText != null) {
            try {
                return Double.parseDouble(calibrationConstantEditText.getText().toString());
            } catch (NumberFormatException ignored) {
            }
        }
        return Iso15693WriteTagActivity.DEFAULT_CALIBRATION_CONSTANT;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nfc_write_operation_action_bar_menu, menu);
        inflater.inflate(R.menu.main_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if (id == R.id.about_app) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(mContext);
            View view = getLayoutInflater().inflate(R.layout.about_app_layout, null);
            myBuilder.setView(view);
            myBuilder.create().show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    public void onResume() {
        super.onResume();

        Intent myintent = getIntent();
        if (myintent != null) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(myintent.getAction())
                    || NfcAdapter.ACTION_TECH_DISCOVERED.equals(myintent.getAction())
                    || NfcAdapter.ACTION_TAG_DISCOVERED.equals(myintent.getAction())) {
                saveCalibrationFields();
                setIntent(null);
                myintent.setClass(getApplicationContext(), Iso15693WriteTagActivity.class);
                myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(myintent);
            }
        }
    }
}
