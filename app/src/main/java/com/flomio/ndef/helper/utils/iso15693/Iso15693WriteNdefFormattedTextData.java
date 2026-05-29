package com.flomio.ndef.helper.utils.iso15693;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;

import com.flomio.ndef.helper.utils.FlomioNdefHelper;

import java.nio.ByteBuffer;
import java.util.Locale;

/**
 * Created by Udayan on 6/12/13.
 */
public class Iso15693WriteNdefFormattedTextData implements OnCommandExecutedCallBack {

    protected Context mContext;
    protected Tag mTag;
    protected OnCommandExecutedCallBack mOnCommandExecutedCallBack;
    protected byte mCurrentCommand;
    protected byte[] mDataToBeWritten;


    public static final byte ISO15693_INVENTORY_SINGLE_SLOT_COMMAND = 0x05;
    public static final byte ISO15693_WRITE_MULTIPLE_BLOCKS_COMMAND = 0x07;

    public static final byte TAGTYPE_ISO15693TAG = (byte) 0xE1;
    public static final byte TAGTYPE_HFI_PLUS_TAG1 = (byte) 0x00;
    public static final byte TAGTYPE_HFI_PLUS_TAG2 = (byte) 0x01;
    public static final byte TAGTYPE_HFI_PRO_TAG1 = (byte) 0xC4;
    public static final byte TAGTYPE_HFI_PRO_TAG2 = (byte) 0xC5;
    public static final byte TAGTYPE_HFI_STANDARD_TAG1 = (byte) 0xC0;
    public static final byte TAGTYPE_HFI_STANDARD_TAG2 = (byte) 0xC1;


    public Iso15693WriteNdefFormattedTextData(Context context, Tag tag, byte[] dataToBeWritten, OnCommandExecutedCallBack onCommandExecutedCallBack) {
        mContext = context;
        mTag = tag;
        mDataToBeWritten = dataToBeWritten;
        mOnCommandExecutedCallBack = onCommandExecutedCallBack;
        mCurrentCommand = ISO15693_INVENTORY_SINGLE_SLOT_COMMAND;
        Iso15693 iso15693 = new Iso15693(mContext, mTag);
        iso15693.IssueInventoryCommandSingleSlot(this);
    }

    @Override
    public void onCommandExecuted(byte[] response) {

        int responseLength;

        if (response != null) {
            responseLength = response.length;
            if (mCurrentCommand == ISO15693_INVENTORY_SINGLE_SLOT_COMMAND) {
                if (response[responseLength - 1] == (byte) 0xE0) {
                    if (response[responseLength - 2] == 0x07) { // TI tags
                        if ((response[responseLength - 3] == 0x00) || (response[responseLength - 3] == 0x01)) { // HF-I Plus tags
                            Iso15693WriteMultipleBlocks iso15693WriteMultipleBlocks = new Iso15693WriteMultipleBlocks(mContext, mTag);
                            iso15693WriteMultipleBlocks.WriteMultipleBlock((byte) 0, getNdefFormattedData((byte) 0x00), this);
                            mCurrentCommand = ISO15693_WRITE_MULTIPLE_BLOCKS_COMMAND;
                        } else if ((response[responseLength - 3] == (byte) 0xC5) || (response[responseLength - 3] == (byte) 0xC4)) { // HF-I Pro tags
                            Iso15693WriteMultipleBlocks iso15693WriteMultipleBlocks = new Iso15693WriteMultipleBlocks(mContext, mTag);
                            iso15693WriteMultipleBlocks.WriteMultipleBlock((byte) 0, getNdefFormattedData((byte) 0xC5), this);
                            mCurrentCommand = ISO15693_WRITE_MULTIPLE_BLOCKS_COMMAND;
                        } else if ((response[responseLength - 3] == (byte) 0xC0) || (response[responseLength - 3] == (byte) 0xC1)) { // HF-I Standard tags
                            Iso15693WriteMultipleBlocks iso15693WriteMultipleBlocks = new Iso15693WriteMultipleBlocks(mContext, mTag);
                            iso15693WriteMultipleBlocks.WriteMultipleBlock((byte) 0, getNdefFormattedData((byte) 0xC0), this);
                            mCurrentCommand = ISO15693_WRITE_MULTIPLE_BLOCKS_COMMAND;
                        }
                    } else {
                        onWriteCommandExecuted(new byte[]{0x04});
                    }
                } else {
                    onWriteCommandExecuted(new byte[]{0x04});
                }
            } else if (mCurrentCommand == ISO15693_WRITE_MULTIPLE_BLOCKS_COMMAND) {
                onWriteCommandExecuted(response);
            }
        } else {
            onWriteCommandExecuted(new byte[]{0x00});
        }
    }

    public byte[] getNdefFormattedData(byte TagType) {
        NdefRecord textRecord = FlomioNdefHelper.createTextRecord(new String(mDataToBeWritten), Locale.ENGLISH, true);
        NdefMessage textMessage = new NdefMessage(
                new NdefRecord[]{textRecord});
        byte[] finalByteArray = textMessage.toByteArray();

        ByteBuffer capacityContainerBuf = ByteBuffer.allocate(4);
        ByteBuffer tlvBlock1Buf = ByteBuffer.allocate(3 + finalByteArray.length);

        byte[] ndefFormattedData = new byte[capacityContainerBuf.array().length + tlvBlock1Buf.array().length];

        switch (TagType) {
            case TAGTYPE_HFI_PLUS_TAG1: // HF-I Plus
            case TAGTYPE_HFI_PLUS_TAG2:
                capacityContainerBuf.put(TAGTYPE_ISO15693TAG);
                capacityContainerBuf.put((byte) 0x40);
                capacityContainerBuf.put((byte) 0x20);
                capacityContainerBuf.put((byte) 0x00);
                break;

            case TAGTYPE_HFI_STANDARD_TAG1:
            case TAGTYPE_HFI_STANDARD_TAG2:
            case TAGTYPE_HFI_PRO_TAG1:
            case TAGTYPE_HFI_PRO_TAG2:
                capacityContainerBuf.put(TAGTYPE_ISO15693TAG);
                capacityContainerBuf.put((byte) 0x40);
                capacityContainerBuf.put((byte) 0x04);
                capacityContainerBuf.put((byte) 0x00);
                break;
        }

        tlvBlock1Buf.put((byte) 0x03);
        tlvBlock1Buf.put((byte) finalByteArray.length); // put length

        for (int i = 0; i < finalByteArray.length; i++) {
            tlvBlock1Buf.put(finalByteArray[i]);
        }

        tlvBlock1Buf.put((byte) 0xFE);

        System.arraycopy(capacityContainerBuf.array(), 0, ndefFormattedData, 0, capacityContainerBuf.array().length);
        System.arraycopy(tlvBlock1Buf.array(), 0, ndefFormattedData, capacityContainerBuf.array().length, tlvBlock1Buf.array().length);

        return ndefFormattedData;
    }

    @Override
    public void onWriteCommandExecuted(byte[] result) {
        mOnCommandExecutedCallBack.onCommandExecuted(result);
    }
}
