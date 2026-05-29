package com.flomio.ndef.helper.utils.iso15693;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.flomio.ndef.helper.utils.FlomioNdefHelper;
import com.flomio.ndef.helper.utils.ToastMaker;
import com.ti.nfcdemo.activities.iso15693.Iso15693WriteTagActivity;

/**
 * Created by Udayan on 6/7/13.
 */
public class Iso15693WriteSingleBlock implements OnCommandExecutedCallBack {


    protected byte[] mWriteData = null;
    protected byte mWriteBlockId;
    protected OnCommandExecutedCallBack mOnCommandExecutedCallBack;
    protected boolean written = false;
    protected int mIterate = 0;
    protected boolean mReadBlock15 = false;    
    protected boolean mSuccess = false;
    protected Context mContext;
    protected Tag mTag;
    protected boolean mCommandExecuted = false;
    public static byte WRITE_SUCCEEDED = 0x01;
    public static byte WRITE_FAILED = 0x00;
    protected byte mCommandIssued = (byte) 0xFF;
    public static int mblockSize = 0;
    public static int mnumberOfBlocks = 0;

    protected static int WRITE_SINGLE_BLOCK_COMMAND_ISSUED = 0x00;
    public static final byte ISO15693_READ_SINGLE_BLOCK_COMMAND = 0x02;

    public Iso15693WriteSingleBlock(Context context, Tag tag, int blockSize, int numberOfBlocks ) {
        mContext = context;
        mTag = tag;
        mblockSize = blockSize;
        mnumberOfBlocks = numberOfBlocks;
    }


    public void WriteSingleBlock(byte BlockId, byte[] data, OnCommandExecutedCallBack onCommandExecutedCallBack) {

        mOnCommandExecutedCallBack = onCommandExecutedCallBack;
        mWriteData = data;
        mWriteBlockId = BlockId;


        
   
	        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
	        for (int j = 0; j < data.length; j++) {
	            byteBuffer.put(data[j]);
	        }
	
	
	        mWriteData = byteBuffer.array();
	        Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
	        mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_WRITE_SINGLE_BLOCK}, new byte[]{BlockId}, mWriteData);
	        written = true;
	        mCommandIssued = (byte) WRITE_SINGLE_BLOCK_COMMAND_ISSUED;
        
    }

boolean read = false;
    @Override
    public void onWriteCommandExecuted(byte[] response) {

    	if((mWriteBlockId != 0) && (mWriteBlockId != 9))
    	{
    		
    	}
    	if((mWriteBlockId == 0))
    	{
           if (mCommandIssued == ISO15693_READ_SINGLE_BLOCK_COMMAND) {
                //postProcessInventoryCommand(response);
            	postProcessSingleBlockBlock8ReadReponse(response);
            	//mCommandIssued = (byte)WRITE_SINGLE_BLOCK_COMMAND_ISSUED;
            } else if (mCommandIssued == WRITE_SINGLE_BLOCK_COMMAND_ISSUED) /* Write Single Block Command issued*/ {
              Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
              mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_READ_SINGLE_BLOCK_COMMAND}, new byte[]{mWriteBlockId});
              postProcessSingleBlockBlock8ReadReponse(response);
              mCommandIssued = ISO15693_READ_SINGLE_BLOCK_COMMAND;
            }

    	}
    	if((mWriteBlockId == 9))
    	{
    		 postProcessSingleBlockBlock8ReadReponse(response);
    		 read = true;
    	}
//        if ((mIterate <= 50) && (mSuccess == false)) {
//
//            if ((response != null) && (mCommandIssued == ISO15693_READ_SINGLE_BLOCK_COMMAND)) {
//                ByteBuffer buffer = ByteBuffer.allocate(mWriteData.length); 
//                buffer.put(new byte[] {0x01});                
//                for (int i = 1; i < mWriteData.length-1; i++) {
//                    buffer.put(response[i + 1]);
//                }
//                byte[] byteArray = buffer.array();
//                
//                if (Arrays.equals(byteArray, mWriteData)) {
//                    mSuccess = true;
//                }
//                if (!mSuccess) {
//                    //Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
//                    //mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_WRITE_SINGLE_BLOCK}, new byte[]{mWriteBlockId}, mWriteData);
//                    //mCommandIssued = (byte) WRITE_SINGLE_BLOCK_COMMAND_ISSUED;
//                    
//                    Iso15693ConstructAndSendCommands msIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
//                    msIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_READ_SINGLE_BLOCK_COMMAND}, new byte[]{mWriteBlockId});
//                    mCommandIssued = ISO15693_READ_SINGLE_BLOCK_COMMAND;
//                    written = false;
//                    
//                    mIterate++;                    
//                    //written = true;
//                } else {
//
//                    response[0] = (mSuccess ? (byte) 0x01 : 0x00);
//                    if (!mCommandExecuted)
//                        mOnCommandExecutedCallBack.onCommandExecuted(response);
//                    //ToastMaker.makeToastShort(mContext, String.valueOf(mIterate), ToastMaker.STYLE_SUCCESS);
//                    mIterate = 0;
//                    mSuccess = false;
//                    mCommandExecuted = true;
//                }
//
//            } else if ((response != null) && (mCommandIssued == WRITE_SINGLE_BLOCK_COMMAND_ISSUED)) {
////                Iso15693 newIso15693Object = new Iso15693(mContext, mTag);
////                newIso15693Object.ReadSingleBlock(mWriteBlockId, this);
//            	//try {
//            	//    Thread.sleep(2000);
//            	//} catch(InterruptedException ex) {
//            	//    Thread.currentThread().interrupt();
//            	//}
//            	
//                Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
//                mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_READ_SINGLE_BLOCK_COMMAND}, new byte[]{mWriteBlockId});
//                mCommandIssued = ISO15693_READ_SINGLE_BLOCK_COMMAND;
//                written = false;
//            } else {
//                if (!mSuccess && !written && !mCommandExecuted) {
//                    Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
//                    mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_WRITE_SINGLE_BLOCK}, new byte[]{mWriteBlockId}, mWriteData);
//                    mCommandIssued = (byte) WRITE_SINGLE_BLOCK_COMMAND_ISSUED;
//                    mIterate++;
//                    written = true;
//                } else if (!mSuccess && written && !mCommandExecuted) {
//
//                    Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
//                    mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_READ_SINGLE_BLOCK_COMMAND}, new byte[]{mWriteBlockId});
//                    mCommandIssued = ISO15693_READ_SINGLE_BLOCK_COMMAND;
//                    written = false;
//                }
//            }
//        } else {
//            if (response != null)
//                response[0] = (mSuccess ? WRITE_SUCCEEDED : WRITE_FAILED);
//
//            else {
//                response = new byte[1];
//                response[0] = (mSuccess ? WRITE_SUCCEEDED : WRITE_FAILED);
//            }
//
//            if (!mCommandExecuted)
//                mOnCommandExecutedCallBack.onCommandExecuted(response);
//            mIterate = 0;
//            mSuccess = false;
//            mCommandExecuted = true;
//        }
    }
    private ByteBuffer mRawBlock8Data = null;
    public void postProcessSingleBlockBlock8ReadReponse(byte[] response) {
        if (response != null) {
        	
        	if(mWriteBlockId == 0)
        	{
        		//if(response.length >= 3)
        		try
        		{
	        	if(response[2]== 0x02)
	        	{
	        		mWriteBlockId = 9;
	        		read = false;
	        		response = null;
//	                Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
//	                mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_READ_SINGLE_BLOCK_COMMAND}, new byte[]{mWriteBlockId});
//	           //     postProcessSingleBlockBlock8ReadReponse(response);
//	                mCommandIssued = ISO15693_READ_SINGLE_BLOCK_COMMAND;
	        	}
	        	else if(response[2]== 0x03)
	        	{
	
	        	}
	        	else
	        	{
	        		mWriteBlockId = 0;
	                Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
	                mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_READ_SINGLE_BLOCK_COMMAND}, new byte[]{mWriteBlockId});
	             //   postProcessSingleBlockBlock8ReadReponse(response);
	                mCommandIssued = ISO15693_READ_SINGLE_BLOCK_COMMAND;
	        	}
        		}
        		catch(Exception e){};
        	}
        	else if(mWriteBlockId == 9)
        	{
        		try
        		{
        		if(read == false)
        		{
        		Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
                mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_READ_SINGLE_BLOCK_COMMAND}, new byte[]{mWriteBlockId});
           //     postProcessSingleBlockBlock8ReadReponse(response);
                mCommandIssued = ISO15693_READ_SINGLE_BLOCK_COMMAND;
        		}
        		else
        		{

              		Iso15693WriteTagActivity.processData(response);
              		
        		}
        		}
        		catch(Exception e){};
        	}
//        	mRawBlock8Data = ByteBuffer.allocate(response.length);
//            for (int j = 0; j < response.length; j++) {
//            	mRawBlock8Data.put(response[j]);
//            }
//            
//            byte[] c = new byte[response.length + 1];                        
//          
//            System.arraycopy(response,          0, c, 1, response.length);
//            System.arraycopy(new byte[] {0x01}, 0, c, 0, 1);
//            
////            String dataFromTag = FlomioNdefHelper.mBytesToHexString(mRawBlock8Data.array());
//            String dataFromTag = FlomioNdefHelper.mBytesToHexString(response);   
//            String dataFromTag_ = FlomioNdefHelper.mBytesToHexString(c);   
//            
//            Log.d("DAMIAN", "Block 8 Contents = "+dataFromTag);
//            //ToastMaker.makeToastShort(mContext, dataFromTag, ToastMaker.STYLE_SUCCESS);
//                        
//        	//mOnCommandExecutedCallBack.onWriteCommandExecuted(new byte[] { (byte)0x01});        	
//            mOnCommandExecutedCallBack.onWriteCommandExecuted(c);        	            
            
        }
        else
        {
        	Iso15693WriteTagActivity.myintent.setAction(null);
        }

    	
    }
    
    
    
    @Override
    public void onCommandExecuted(byte[] data) {
        onWriteCommandExecuted(data);
    }
}
