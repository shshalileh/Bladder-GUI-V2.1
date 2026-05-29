package com.flomio.ndef.helper.utils;

public class Utils {

	public static boolean isStringEmpty(String str){
        return str == null || str.length() == 0;
    }
	
	public static boolean isStringBlank(String str){
        int strLen;

        if (str == null || (strLen = str.length()) == 0){
            return true;
        }

        if (str.equals("null")){
            return true;
        }

        for (int i = 0; i < strLen; i++){
            if ((Character.isWhitespace(str.charAt(i)) == false)){
                return false;
            }
        }
        return true;
    }
	
	public static String bytesToHex(byte[] bytes) {
	    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    
	    return new String(hexChars);
	}
	
}
