package com.flomio.ndef.helper.utils;

import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;

import java.nio.ByteBuffer;

import static java.lang.String.format;

public class NfcTagUtils {
	
	private static final String LOG_TAG = NfcTagUtils.class.getSimpleName();
	
	public static final String TAG_TYPE_MIFFARE_CLASSIC = "Mifare Classic";
	public static final String TAG_TYPE_NFC_FORUM_TYPE_1 = "NFC Forum Type 1 Tag";
	public static final String TAG_TYPE_NFC_FORUM_TYPE_2 = "NFC Forum Type 2 Tag";
	public static final String TAG_TYPE_NFC_FORUM_TYPE_3 = "NFC Forum Type 3 Tag";
	public static final String TAG_TYPE_NFC_FORUM_TYPE_4 = "NFC Forum Type 4 Tag";
	public static final String TAG_TYPE_UNKNOWN = "Unknown";
	
	public static boolean isTagReadOnly(Tag tag){
		Ndef ndef = Ndef.get(tag);
		
		if (ndef != null)
			return !ndef.isWritable();   
		else
			return false;
	}
	
	public static boolean isTagAbleToMakeItReadOnly(Tag tag) {
		Ndef ndef = Ndef.get(tag);
		
		if (ndef != null)
			return ndef.canMakeReadOnly();   
		else
			return false;		
	}
	
	public static boolean isEnoughSpaceToWrite(Tag tagToWrite, NdefMessage messageToWrite) {
		
		Ndef ndef = Ndef.get(tagToWrite);
		int size = messageToWrite.toByteArray().length;

        if ((ndef != null) && (ndef.getMaxSize() < size)) {
            return false;
        }
		
		return true;
	}
	
	public static String[] getTagSupportedTechnologies(Tag tag) {
		String[] technologies = tag.getTechList();
		
		if (NfcDebuglog.isNFCLoggingEnabled()) {
			for (String tech : technologies) {
				NfcDebuglog.d(LOG_TAG, "Tag Supported Technology ------------>>>> " + tech);	
			}
		}
		
		if (technologies != null)
			return technologies;
		else
			return new String [] {};
	}
	
	public static boolean tagSupportNdef(Tag tag) {
		
		String[] technologies = NfcTagUtils.getTagSupportedTechnologies(tag);
		
		if (technologies != null && technologies.length > 0) {
			for (String tech : technologies) {
				if (tech == Ndef.class.getName() || tech == NdefFormatable.class.getName())
					return true;
			}
		}
		
		return false;
	}
	
	public static boolean isTagNdefFormatted(Tag tag) {

		Ndef ndef = Ndef.get(tag);
		
		if (ndef == null)
			return false;
		
		return true;
	}
	
	public static boolean isTagNdefFormatable(Tag tag) {
		
		try {
			
			String ndefTagType = Ndef.get(tag).getType();
			
			if (ndefTagType.equals(Ndef.MIFARE_CLASSIC) || 
				ndefTagType.equals(Ndef.NFC_FORUM_TYPE_1) ||
				ndefTagType.equals(Ndef.NFC_FORUM_TYPE_2) ||
				ndefTagType.equals(Ndef.NFC_FORUM_TYPE_3) ||
				ndefTagType.equals(Ndef.NFC_FORUM_TYPE_4)) {
			
				return true;
			} else {
				return false;
			}
			
		}catch (Exception e) {
			NfcDebuglog.d(LOG_TAG, "There was an Exception when checkin for method: 'isTagNdefFormatable' " + "Exception --->" + e.getMessage() );
			return false;
		}
	}
	
	
	/**
	 * Android devices with NFC must only enumerate and implement this class for tags for which it can format to NDEF.
  	 * Unfortunately the procedures to convert unformated tags to NDEF formatted tags are not specified by NFC Forum, 
  	 * and are not generally well-known. So there is no mandatory set of tags for which all Android devices with NFC 
  	 * must support NdefFormatable.
  	 * 
	 * @param tag the tag to check if it's ndef formatable by this device
	 * @return true if the tag can be formated by this device.
	 */
	public static boolean isTagNdefFormatableByThisDevice(Tag tag) {
		
		if (NfcTagUtils.tagSupportNdef(tag)) {
			NdefFormatable format = NdefFormatable.get(tag);
			
	        if (format == null)
	        	if (NfcTagUtils.isTagNdefFormatable(tag)) {
	        		return true;
	        	} else {
	        		return false;
	        	}
	        else
	        	return true;
	        
		} else if (NfcTagUtils.isTagNdefFormatable(tag)) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public static String getTagType(Tag tag) {
		
		try {
			String ndefTagType = Ndef.get(tag).getType(); 

			if (ndefTagType == null || Utils.isStringEmpty(ndefTagType))
				return TAG_TYPE_UNKNOWN;
			else {
				if (ndefTagType.equals(Ndef.MIFARE_CLASSIC)) return NfcTagUtils.TAG_TYPE_MIFFARE_CLASSIC;
				if (ndefTagType.equals(Ndef.NFC_FORUM_TYPE_1)) return NfcTagUtils.TAG_TYPE_NFC_FORUM_TYPE_1+"(ISO/IEC 14443 Type A)";
				if (ndefTagType.equals(Ndef.NFC_FORUM_TYPE_2)) return NfcTagUtils.TAG_TYPE_NFC_FORUM_TYPE_2+"(ISO/IEC 14443 Type A)";
				if (ndefTagType.equals(Ndef.NFC_FORUM_TYPE_3)) return NfcTagUtils.TAG_TYPE_NFC_FORUM_TYPE_3;
				if (ndefTagType.equals(Ndef.NFC_FORUM_TYPE_4)) return NfcTagUtils.TAG_TYPE_NFC_FORUM_TYPE_4+"(ISO 14443A/B)";

                String[] tagTechList = tag.getTechList();

                for(String s:tagTechList){
                    if(s.equals("android.nfc.tech.NfcF")){
                        return NfcTagUtils.TAG_TYPE_NFC_FORUM_TYPE_3;
                    }
                }
			}
				
			return TAG_TYPE_UNKNOWN;
		
		}catch(Exception ex) {

            String[] tagTechList = tag.getTechList();

            for(String s:tagTechList){
                if(s.equals("android.nfc.tech.NfcF")){
                    return NfcTagUtils.TAG_TYPE_NFC_FORUM_TYPE_3;
                }
            }

			return TAG_TYPE_UNKNOWN;
		}
	}
	
	public static int getTagMaximumSizeInBytes(Tag tag) {

		Ndef ndef = Ndef.get(tag);
		int sizeInBytes = 0;
		
		if (ndef != null)
			sizeInBytes = ndef.getMaxSize();
		
		return sizeInBytes;
	}
	
	public static void printUsefullTagInformationInLog(Tag tag){
        
		NfcTagUtils.getTagSupportedTechnologies(tag);
        
        if (NfcTagUtils.tagSupportNdef(tag))
        	NfcDebuglog.d(LOG_TAG, "SSSSSSSSSSSSSSSSSSSSSSSSSSSS -------------->>>>>> TAG SUPPORT NDEF");
        else
        	NfcDebuglog.d(LOG_TAG, "SSSSSSSSSSSSSSSSSSSSSSSSSSSS -------------->>>>>> TAG NOT SUPPORT NDEF");
        
        if (NfcTagUtils.isTagNdefFormatted(tag))
        	NfcDebuglog.d(LOG_TAG, "SSSSSSSSSSSSSSSSSSSSSSSSSSSS -------------->>>>>> TAG IS NDEF FORMATTED");
        else
        	NfcDebuglog.d(LOG_TAG, "SSSSSSSSSSSSSSSSSSSSSSSSSSSS -------------->>>>>> TAG IS NOT NDEF FORMATTED");
        
        if (NfcTagUtils.isTagNdefFormatable(tag))
        	NfcDebuglog.d(LOG_TAG, "SSSSSSSSSSSSSSSSSSSSSSSSSSSS -------------->>>>>> TAG IS NDEF FORMATABLE");
        else
        	NfcDebuglog.d(LOG_TAG, "SSSSSSSSSSSSSSSSSSSSSSSSSSSS -------------->>>>>> TAG IS NOT NDEF FORMATABLE");
     
        if (NfcTagUtils.isTagNdefFormatableByThisDevice(tag))
        	NfcDebuglog.d(LOG_TAG, "SSSSSSSSSSSSSSSSSSSSSSSSSSSS -------------->>>>>> TAG IS NDEF FORMATABLE BY THIS DEVICE");
        else
        	NfcDebuglog.d(LOG_TAG, "SSSSSSSSSSSSSSSSSSSSSSSSSSSS -------------->>>>>> TAG IS NOT NDEF FORMATABLE BY THIS DEVICE");
        
        if (NfcTagUtils.isTagReadOnly(tag))
    		NfcDebuglog.d(LOG_TAG, "SSSSSSSSSSSSSSSSSSSSSSSSSSSS -------------->>>>>> TAG IS READ-ONLY");
        else
        	NfcDebuglog.d(LOG_TAG, "SSSSSSSSSSSSSSSSSSSSSSSSSSSS -------------->>>>>> TAG IS NOT READ-ONLY");
	}


    public static String getSakAtqa(Tag tag) {
        NfcA nfcA = NfcA.get(tag);
        String sakText;

        if (nfcA != null) {
            ByteBuffer buffer1 = ByteBuffer.allocate(2);
            buffer1.put(nfcA.getAtqa());
            String mAtqa = Utils.bytesToHex(buffer1.array());
            String mUpperByte = mAtqa.substring(0, 2);
            String mLowerByte = mAtqa.substring(2, 4);
            sakText = format("ATQA: %s %s", mLowerByte, mUpperByte) + "\n";
            ByteBuffer buffer = ByteBuffer.allocate(2);
            buffer.putShort(nfcA.getSak());
            sakText += format("SAK: %s", Utils.bytesToHex(buffer.array()).substring(2, 4)) + "\n";

            return sakText;
        } else {

            return null;
        }
    }
}
