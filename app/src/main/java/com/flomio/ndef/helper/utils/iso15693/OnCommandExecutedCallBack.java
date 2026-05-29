package com.flomio.ndef.helper.utils.iso15693;

/**
 * Created by Udayan on 6/4/13.
 */
public interface OnCommandExecutedCallBack {

    void onCommandExecuted(byte[] data);
    void onWriteCommandExecuted(byte[] result);
}
