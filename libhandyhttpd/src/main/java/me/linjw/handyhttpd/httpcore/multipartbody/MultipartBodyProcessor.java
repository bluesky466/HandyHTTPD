package me.linjw.handyhttpd.httpcore.multipartbody;

import java.io.File;
import java.util.Map;

/**
 * Created by linjw on 18-7-1.
 */

public class MultipartBodyProcessor implements IProcessor {
    private boolean mSkipLineBreak = false;
    private boolean mBodyBegin;
    private int mIndex;
    private byte[] mBoundary;

    private MultipartBodyPartProcessor mPartProcessor = new MultipartBodyPartProcessor();

    public void setBoundary(String boundary) {
        mBoundary = ("--" + boundary).getBytes();
    }

    @Override
    public void process(byte data, Map<String, String> outParams, Map<String, File> outFiles) {
        //skip linebreak on boundary line
        if (mSkipLineBreak && data == '\n') {
            return;
        }
        mSkipLineBreak = false;

        if (mIndex < mBoundary.length && data == mBoundary[mIndex]) {
            mIndex++;
            return;
        }

        if (mIndex == mBoundary.length) {
            mBodyBegin = true;
            mPartProcessor.reset();
            if (data != '\r' && data != '\n') {
                mPartProcessor.process(data, outParams, outFiles);
            }
            mSkipLineBreak = true;
        } else if (mBodyBegin && mIndex == 0) {
            mPartProcessor.process(data, outParams, outFiles);
        } else if (mBodyBegin) {
            mPartProcessor.process(mBoundary, mIndex, outParams, outFiles);
        }

        mIndex = 0;
    }

    @Override
    public void process(byte[] datas, int size, Map<String, String> outParams, Map<String, File> outFiles) {
        for (int i = 0; i < size; i++) {
            process(datas[i], outParams, outFiles);
        }
    }

    @Override
    public void reset() {
        mBodyBegin = false;
        mIndex = 0;
    }
}
