package com.flomio.ndef.helper.utils.iso15693;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.flomio.ndef.helper.utils.FlomioNdefHelper;
import com.flomio.ndef.helper.utils.ToastMaker;
import com.ti.nfcdemo.R;
import com.ti.nfcdemo.activities.nfc.NfcBaseActivity;

/**
 * Created by Udayan on 6/11/13.
 */
public class Iso15693TestCommandsActivity extends NfcBaseActivity implements OnCommandExecutedCallBack {

    Button mButton;
    OnCommandExecutedCallBack mOnCommandExecutedCallBack;
    Context mContext;
    Spinner mSpinner;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test15693);
        mOnCommandExecutedCallBack = this;
        mContext = this;
        mSpinner = (Spinner) findViewById(R.id.spinner_15693_commands);

        mButton = (Button) findViewById(R.id.button_execute_command);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iso15693 iso15693 = new Iso15693(mContext, mTag);

                if("ISO15693_READ_SINGLE_BLOCK_COMMAND".equals(mSpinner.getSelectedItem().toString())) {
                    iso15693.IssueInventoryCommandSingleSlot(mOnCommandExecutedCallBack);
                }
                else if("ISO15693_WRITE_SINGLE_BLOCK".equals(mSpinner.getSelectedItem().toString())) {
                    iso15693.IssueInventoryCommandSingleSlot(mOnCommandExecutedCallBack);
                }
                else if("ISO15693_READ_MULTIPLE_BLOCKS_COMMAND".equals(mSpinner.getSelectedItem().toString())) {
                    iso15693.IssueInventoryCommandSingleSlot(mOnCommandExecutedCallBack);
                }
                else if("ISO15693_GET_SYSTEM_INFO_COMMAND".equals(mSpinner.getSelectedItem().toString())) {
                    iso15693.GetSystemInfo(mOnCommandExecutedCallBack);
                }
                else if("ISO15693_GET_SECURITY_STATUS".equals(mSpinner.getSelectedItem().toString())) {
                    iso15693.GetSecurityStatus(mOnCommandExecutedCallBack);
                }

            }
        });

        initializeNfcAdapterAndIntentFilters();
    }

    @Override
    public void onCommandExecuted(byte[] data) {
        if (data != null)
            ToastMaker.makeToastShort(mContext, "Response: " + FlomioNdefHelper.mBytesToHexString(data), ToastMaker.STYLE_SUCCESS);

        else
            ToastMaker.makeToastShort(mContext, "Response: null", ToastMaker.STYLE_FAILURE);

    }

    @Override
    public void onWriteCommandExecuted(byte[] result) {

    }

}