package com.flomio.ndef.helper.utils.iso15693;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import java.nio.ByteBuffer;

import com.flomio.ndef.helper.utils.FlomioNdefHelper;
import com.flomio.ndef.helper.utils.ToastMaker;
import com.ti.nfcdemo.activities.iso15693.Iso15693WriteTagActivity;

/**
 * Created by Udayan on 6/7/13.
 */
public class Iso15693WriteMultipleBlocks implements OnCommandExecutedCallBack {

	private static final String LOG_TAG = Iso15693WriteMultipleBlocks.class.getSimpleName();
    protected OnCommandExecutedCallBack mOnCommandExecutedCallBack;
    protected byte[] mWriteData = null;
    protected int mWriteDatalength = 0; 
    protected byte mWriteBlockId;
    protected int mCurrentState = 0;
    protected Context mContext;
    protected Tag mTag;
    protected byte[] mResponse;
    protected byte[] systemInfo = null;
    protected int numOfBlocksToWrite = 0;
    protected int mIndex = 0;
	protected int mblockSize = 0;
	protected int mnumberOfBlocks = 0; 
    protected int mBlockCounter;
    private ByteBuffer mRawBlock8Data = null;

    protected static int WRITE_SINGLE_BLOCK_COMMAND_ISSUED = 0;
    protected static int GET_SYSTEM_INFO_COMMAND_ISSUED = 1;
    protected static int INVENTORY_COMMAND_ISSUED = 2;
    protected static int READ_SINGLE_BLOCK_BLOCK8_COMMAND_ISSUED = 3;
    

    public Iso15693WriteMultipleBlocks(Context context, Tag tag) {
        mContext = context;
        mTag = tag;
    }

    public void WriteMultipleBlock(byte BlockId, byte[] data, OnCommandExecutedCallBack onCommandExecutedCallBack) {

        mResponse = null;
        Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
        mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_GET_SYSTEM_INFO});
      //  mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_WRITE_SINGLE_BLOCK});
        mOnCommandExecutedCallBack = onCommandExecutedCallBack;

        mWriteData = data;
        mWriteBlockId = BlockId;
        mBlockCounter = (int) mWriteBlockId;
        mCurrentState = GET_SYSTEM_INFO_COMMAND_ISSUED;
        
    }

    @Override
    public void onCommandExecuted(byte[] data) {
        onWriteCommandExecuted(data);
    }

    @Override
    public void onWriteCommandExecuted(byte[] response) {

        if (mCurrentState == GET_SYSTEM_INFO_COMMAND_ISSUED) {
            postProcessSystemInfo(response);
        } else if (mCurrentState == READ_SINGLE_BLOCK_BLOCK8_COMMAND_ISSUED) {
            //postProcessInventoryCommand(response);
        	postProcessSingleBlockBlock8ReadReponse(response);
        } else /* Write Single Block Command issued*/ {
            postProcessSingleBlockWriteReponse(response);
        }
    }

    public void postProcessSystemInfo(byte[] response) {

        systemInfo = response;

        if (systemInfo != null) {
        	
            //int blockSize = systemInfo[13] + 1;
            //int numOfBlocks = systemInfo[12] + 1;
        	mblockSize = FlomioNdefHelper.mByteToInt(Iso15693.getBlockSize(systemInfo));
        	mnumberOfBlocks = FlomioNdefHelper.mByteToInt(Iso15693.getNumberOfBlocks(systemInfo));

            int memorySize = mblockSize * mnumberOfBlocks;

            if ((mWriteData != null)) {
            	mWriteDatalength = mWriteData.length;
            	
                if (mWriteDatalength > memorySize) {
                    mOnCommandExecutedCallBack.onCommandExecuted(new byte[]{0x02});
                } else {
                	
                    numOfBlocksToWrite = (mWriteDatalength / mblockSize) + 1; 
                    ByteBuffer bytedatabuffer = ByteBuffer.allocate(mblockSize);                	
                    for (int j = 0; j < mblockSize; j++) {
                	//numOfBlocksToWrite = (mWriteData.length / 8) + 1;                   
                	//ByteBuffer bytedatabuffer = ByteBuffer.allocate(8);                                    
                	//for (int j = 0; j < 8; j++) {  
                    //Ricky - 3
                    	//The purpose of this if is if in case the data being written
                    	//to the tag does are not multiple of mblockSize. 
                        if ((mIndex + j) < mWriteDatalength)
                            bytedatabuffer.put(mWriteData[mIndex + j]);
                    }

                    Iso15693WriteSingleBlock iso15693WriteSingleBlock = new Iso15693WriteSingleBlock(mContext, mTag, 1, 1);
                //    Iso15693WriteSingleBlock iso15693WriteSingleBlock = new Iso15693WriteSingleBlock(mContext, mTag, mblockSize, mnumberOfBlocks);
                    iso15693WriteSingleBlock.WriteSingleBlock((byte) mWriteBlockId, bytedatabuffer.array(), this);
                    //mBlockCounter++;
                  //  numOfBlocksToWrite--;
                }
            }
            mCurrentState = WRITE_SINGLE_BLOCK_COMMAND_ISSUED;
            try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if(mWriteBlockId == 0)
            {
            	postProcessSingleBlockWriteReponse(response);
            }

        } else {
                /*
                	Inventory command is issued when GetSystemInfo command fails.
                	GetSystemInfo command is not supported by TI's Pro/Standard tags
                	Issue a Inventory single slot command
                */
            Iso15693 iso15693 = new Iso15693(mContext, mTag);
            iso15693.IssueInventoryCommandSingleSlot(this);
            mCurrentState = INVENTORY_COMMAND_ISSUED;
        }
    }
/*
    public void postProcessInventoryCommand(byte[] response) {

        if ((mWriteData != null)) {

            int blockSize;
            int numOfBlocks;
            int memorySize = 0;

            if (response != null) {
                if (response[7] == 0x00) {
                    blockSize = systemInfo[13] + 1;
                    numOfBlocks = systemInfo[12] + 1;
                    memorySize = blockSize * numOfBlocks;

                    if (mWriteData.length > memorySize) {
                        mOnCommandExecutedCallBack.onCommandExecuted(new byte[]{0x02});
                    } else {
                        numOfBlocksToWrite = (mWriteData.length / 4) + 1;
                        ByteBuffer bytedatabuffer = ByteBuffer.allocate(4);
                        for (int j = 0; j < 4; j++) {
                            if ((mIndex + j) < mWriteData.length)
                                bytedatabuffer.put(mWriteData[mIndex + j]);
                        }
                        Iso15693WriteSingleBlock iso15693WriteSingleBlock = new Iso15693WriteSingleBlock(mContext, mTag, mblockSize, mnumberOfBlocks);
                        iso15693WriteSingleBlock.WriteSingleBlock((byte) mBlockCounter++, bytedatabuffer.array(), this);
                        numOfBlocksToWrite--;
                    }
                    mCurrentState = WRITE_SINGLE_BLOCK_COMMAND_ISSUED;

                } else if ((response[7] == (byte) 0xC0) || (response[7] == (byte) 0xC5)) {
                    memorySize = 32;

                    if (mWriteData.length > memorySize) {
                        mOnCommandExecutedCallBack.onCommandExecuted(new byte[]{0x02});
                    } else {
                        numOfBlocksToWrite = (mWriteData.length / 4);
                        if (numOfBlocksToWrite * 4 < mWriteData.length) {
                            numOfBlocksToWrite++;
                        }

                        ByteBuffer bytedatabuffer = ByteBuffer.allocate(4);
                        for (int j = 0; j < 4; j++) {
                            if ((mIndex + j) < mWriteData.length)
                                bytedatabuffer.put(mWriteData[mIndex + j]);
                        }
                        Iso15693WriteSingleBlock iso15693WriteSingleBlock = new Iso15693WriteSingleBlock(mContext, mTag, mblockSize, mnumberOfBlocks);
                        iso15693WriteSingleBlock.WriteSingleBlock((byte) mBlockCounter++, bytedatabuffer.array(), this);
                        numOfBlocksToWrite--;
                    }
                }
                mCurrentState = WRITE_SINGLE_BLOCK_COMMAND_ISSUED;

            } else {
                mOnCommandExecutedCallBack.onWriteCommandExecuted(new byte[]{(byte) 0x03});
            }
        }
    }
*/
    
    public void postProcessSingleBlockBlock8ReadReponse(byte[] response) {
        if (response != null) {  
        	mRawBlock8Data = ByteBuffer.allocate(response.length);
            for (int j = 0; j < response.length; j++) {
            	mRawBlock8Data.put(response[j]);
            }
            
            byte[] c = new byte[response.length + 1];                        
          
            System.arraycopy(response,          0, c, 1, response.length);
            System.arraycopy(new byte[] {0x01}, 0, c, 0, 1);
            
//            String dataFromTag = FlomioNdefHelper.mBytesToHexString(mRawBlock8Data.array());
            String dataFromTag = FlomioNdefHelper.mBytesToHexString(response);   
            String dataFromTag_ = FlomioNdefHelper.mBytesToHexString(c);   
            
            Log.d("DAMIAN", "Block 8 Contents = "+dataFromTag);
            //ToastMaker.makeToastShort(mContext, dataFromTag, ToastMaker.STYLE_SUCCESS);
                        
        	//mOnCommandExecutedCallBack.onWriteCommandExecuted(new byte[] { (byte)0x01});        	
            mOnCommandExecutedCallBack.onWriteCommandExecuted(c);        	            
            
        }
    	
    }
    
    public void postProcessSingleBlockWriteReponse(byte[] response) {
      //  if (numOfBlocksToWrite == 0) {
    	if (mWriteBlockId == 0) {
            //mOnCommandExecutedCallBack.onWriteCommandExecuted(response);
            Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
            mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_READ_SINGLE_BLOCK_COMMAND}, new byte[]{0x0F});
        	mCurrentState = READ_SINGLE_BLOCK_BLOCK8_COMMAND_ISSUED;
        } else {

            if (response[0] == 0x01) {
                mIndex = mIndex + mblockSize;
            	//mIndex = mIndex + 8;                
            	ByteBuffer bytedatabuffer = ByteBuffer.allocate(mblockSize);
            	//ByteBuffer bytedatabuffer = ByteBuffer.allocate(8);
                for (int j = 0; j < mblockSize; j++) {
            	//for (int j = 0; j < 8; j++) {
                    if ((mIndex + j) < mWriteDatalength)
                        bytedatabuffer.put(mWriteData[mIndex + j]);
                }

                Iso15693WriteSingleBlock newIso15693WriteObject = new Iso15693WriteSingleBlock(mContext, mTag, mblockSize, mnumberOfBlocks);
                newIso15693WriteObject.WriteSingleBlock((byte) 0, bytedatabuffer.array(), this);
               // numOfBlocksToWrite--;
            } else {
                mOnCommandExecutedCallBack.onWriteCommandExecuted(response);
            }
        }
    }


}
