package com.flomio.ndef.helper.utils.iso15693;

import android.nfc.Tag;

/**
 * Created with IntelliJ IDEA.
 * User: beej
 * Date: 6/19/13
 * Time: 5:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Iso15693Utils {

    public static final int ISO_15693_MANUFACTURER_UNKNOWN = 0;
    public static final int ISO_15693_TEXAS_INSTRUMENTS = 1;

    public static final int ISO_15693_TYPE_UNKNOWN = 0;
    public static final int ISO_15693_HF_I_STANDARD = 1;
    public static final int ISO_15693_HF_I_PLUS = 2;
    public static final int ISO_15693_HF_I_PRO = 3;
    public static final int ISO_15693_RF430 = 4;

    public static boolean isNdefFormatted(byte[] data) {
        if (data[0] == (byte) 0xE1) {
            if (data[4] == (byte) 0x03) {
                return true;
            } else
                return false;
        } else {
            return false;
        }
    }

    public static boolean isTagIso15693(Tag tag) {
        byte[] tagIDBytes = tag.getId();

        if ((tagIDBytes[tagIDBytes.length - 1] & 0xFF) == 0xE0) {
            return true;
        }
        return false;
    }

    public static boolean isTagIso15693(byte[] uid) {
        if (uid[uid.length - 1] == (byte) 0xE0) {
            return true;
        } else {
            return false;
        }
    }


    public static int getManufacturer(byte[] uid) {
        if (uid[uid.length - 2] == (byte) 0x07) {
            return ISO_15693_TEXAS_INSTRUMENTS;
        } else {
            return ISO_15693_MANUFACTURER_UNKNOWN;
        }
    }

    public static int getTagType(byte[] uid) {
        int tagTypeId = uid[uid.length - 3];

        if (tagTypeId == (byte) 0x00 || tagTypeId == (byte) 0x01) {
            return ISO_15693_HF_I_PLUS;
        } else if (tagTypeId == (byte) 0x80 || tagTypeId == (byte) 0x81) {
            return ISO_15693_HF_I_PLUS;
        } else if (tagTypeId == (byte) 0xC5 || tagTypeId == (byte) 0xC4) {
            return ISO_15693_HF_I_PRO;
        } 
        
          else if (tagTypeId == (byte) 0xA0 ){
            return ISO_15693_RF430;
        } 
          else if (tagTypeId == (byte) 0xC0 || tagTypeId == (byte) 0xC1) {
            return ISO_15693_HF_I_STANDARD;
        } else {
            return ISO_15693_TYPE_UNKNOWN;
        }
    }


}
