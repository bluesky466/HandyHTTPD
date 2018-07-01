package me.linjw.handyhttpd.httpcore.multipartbody;

import java.io.File;
import java.util.Map;

/**
 * Created by linjw on 18-7-1.
 */

public class MultipartBodyPartProcessor implements IProcessor {
    public static final int BUFFER_SIZE = 128;

    private boolean mIsData;

    private int mCount;
    private byte[] mBuffer = new byte[BUFFER_SIZE];
    private String mLine = "";

    @Override
    public void process(byte data, Map<String, String> outParams, Map<String, File> outFiles) {
        if (mIsData) {
        } else if (data == '\n') {
            if (mCount == 0) {
                return;
            }

            if (mBuffer[mCount - 1] == '\r') {
                mLine += new String(mBuffer, 0, mCount - 1);
            } else {
                mLine += new String(mBuffer, 0, mCount);
            }

            if (!mLine.isEmpty()) {
                if (!mLine.equals("--")) {
                    System.out.println(mLine);
                }
            } else {
                mIsData = true;
            }

            mCount = 0;
            mLine = "";
        } else if (mCount == mBuffer.length) {
            if (mBuffer[mCount - 1] == '\r') {
                mLine += new String(mBuffer, 0, mCount - 1);
                mBuffer[0] = '\r';
                mCount = 1;
            } else {
                mLine += new String(mBuffer);
                mCount = 0;
            }
        } else {
            mBuffer[mCount] = data;
            mCount++;
        }
    }

    @Override
    public void process(byte[] datas, int size, Map<String, String> outParams, Map<String, File> outFiles) {
        for (int i = 0; i < size; i++) {
            process(datas[i], outParams, outFiles);
        }
    }

    @Override
    public void reset() {
        mIsData = false;
        mLine = "";
        mCount = 0;
    }
}
