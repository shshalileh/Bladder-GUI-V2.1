package com.ti.nfcdemo.activities.nfc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.ti.nfcdemo.R;

/**
 * Created with IntelliJ IDEA.
 * User: beej
 * Date: 6/18/13
 * Time: 12:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class NfcWriteTagBaseActivity extends NfcBaseActivity {
    protected static AlertDialog mScanDialog;
    protected static boolean mProcessNewTags;
   

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScanDialog = buildScanDialog();
        mProcessNewTags = false;
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nfc_write_operation_action_bar_menu, menu);
        enableScanningAndShowScanDialog();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if (id == R.id.write_tag) {
            enableScanningAndShowScanDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private AlertDialog buildScanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = getLayoutInflater().inflate(R.layout.write_dialog_layout,null);
        builder.setView(view);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                disableScanningAndDismissScanDialog();
            }
        });
        return builder.create();
    }

    public void enableScanningAndShowScanDialog() {
        mProcessNewTags = true;
       // enableForegroundDispatch(mNfcPendingIntent, mWriteTagFilters, null);
        mScanDialog.show();
    }

    public static void disableScanningAndDismissScanDialog() {
        mProcessNewTags = false;
        if (mScanDialog != null && mScanDialog.isShowing()) {
            mScanDialog.dismiss();
        }
    }
}
