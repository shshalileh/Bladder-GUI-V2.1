package com.ti.nfcdemo.activities.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import com.flomio.ndef.helper.utils.NfcDebuglog;
import com.flomio.ndef.helper.utils.ToastMaker;

/**
 * Created with IntelliJ IDEA.
 * User: beej
 * Date: 5/31/13
 * Time: 1:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class NfcBaseActivity extends Activity {
  //  protected final String LOG_TAG = NfcWriteTagWithTextActivity.class.getSimpleName();

    protected Activity mActivity;
    protected static Context mContext;
    protected static IntentFilter[] mWriteTagFilters;
    protected static PendingIntent mNfcPendingIntent;

    protected static NfcAdapter mNfcAdapter;
    protected static Tag mTag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mContext = this;
    }

    @Override
    public void onPause() {
        super.onPause();
        disableForegroundDispatch();
        mTag = null;
    }

    @Override
    public void onResume() {
        super.onResume();
//        enableForegroundDispatch(mNfcPendingIntent, mWriteTagFilters, null);
//        Intent myintent = getIntent();
//        if (myintent != null) {
//            mTag = myintent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    //    Log.d(LOG_TAG, "onNewIntent()");
        setIntent(intent);
    }

    protected void disableForegroundDispatch() {
        try {
            mNfcAdapter.disableForegroundDispatch(this);
        } catch (IllegalStateException e) {
     //       NfcDebuglog.e(LOG_TAG, "feature not supported or activity not in foreground");
        }
    }

//    protected void enableForegroundDispatch(PendingIntent intent,
//                                         IntentFilter[] filters, String[][] techLists) {
//        try {
//            String[][] techListsArray = new String[][]{new String[]{MifareUltralight.class.getName(),
//                                                                    Ndef.class.getName(),
//                                                                    NfcA.class.getName()},
//                                                       new String[]{MifareClassic.class.getName(),
//                                                                    Ndef.class.getName(),
//                                                                    NfcA.class.getName()}};
//            mNfcAdapter.enableForegroundDispatch(this, intent, filters, techListsArray);
//        } catch (IllegalStateException e) {
//     //       NfcDebuglog.e(LOG_TAG, "feature not supported or activity not in foreground");
//        }
//    }

    protected void initializeNfcAdapterAndIntentFilters() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mWriteTagFilters = new IntentFilter[]{ndefDetected, techDetected, tagDetected};
    }

    protected void initializeNfcAdapterAndIntentFilters(String[] intentFilters) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);

        mWriteTagFilters = new IntentFilter[intentFilters.length];
        int i = 0;
        for (String intentFilter : intentFilters)
        {
            mWriteTagFilters[i] =  new IntentFilter(intentFilter);
            i++;
        }
    }

    protected void indicateTagNotNdefFormatted() {
        ToastMaker.makeToastLong(getApplicationContext(),"Tag not NDEF Formatted",ToastMaker.STYLE_FAILURE);
    }

    protected void indicateTagTechNotSupported() {
        ToastMaker.makeToastLong(getApplicationContext(),"Tag Technology not Supported",ToastMaker.STYLE_FAILURE);
    }

    protected void indicateTagWriteStatus(boolean tagWriteSuccessful) {
        if (tagWriteSuccessful) {
            indicateTagWriteSuccessfull();
        }
        else {
            indicateTagWriteUnsuccessfull();
        }
    }

    protected void indicateTagWriteSuccessfull() {
        ToastMaker.makeToastShort(getApplicationContext(), "Tag write successful", ToastMaker.STYLE_SUCCESS);
    }

    protected void indicateTagWriteUnsuccessfull() {
        ToastMaker.makeToastShort(getApplicationContext(), "Tag write unsuccessful", ToastMaker.STYLE_SUCCESS);
    }
}
