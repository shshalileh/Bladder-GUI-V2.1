package com.flomio.ndef.helper.utils;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;


public class NfcUtils {

	public static boolean isNFCAvailable(Context context){
		NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
		if (manager.getDefaultAdapter() != null)
			return true;

		return false;
	}	
	
	public static boolean isNFCEnabled(Context context){
		if (isNFCAvailable(context)) {
			return NfcAdapter.getDefaultAdapter(context).isEnabled();
		}else
			return false;
	}
	
	public static boolean isNFCAvailableOrEnableInThisDevice(Context context) {
		if (NfcUtils.isNFCEnabled(context))  	
			return true;
		
		return false;
	}
}
