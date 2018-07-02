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

    private int mConut;
    private byte mPartFirstData;
    private boolean mIsFinished;

    private MultipartBodyPartProcessor mPartProcessor = new MultipartBodyPartProcessor();

    public void setBoundary(String boundary) {
        mBoundary = ("--" + boundary).getBytes();
    }

    @Override
    public void process(byte data,
                        Map<String, String> outParams,
                        Map<String, File> outFiles,
                        String cacheDir) {

        if (mIsFinished
                || (mConut == 1 && mPartFirstData == '-' && data == '-')) {
            mIsFinished = true;
            return;
        }

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
                mPartProcessor.process(data, outParams, outFiles, cacheDir);
            }
            mSkipLineBreak = true;
            mPartFirstData = data;
            mConut = 1;
        } else if (mBodyBegin && mIndex == 0) {
            mPartProcessor.process(data, outParams, outFiles, cacheDir);
            mConut++;
        } else if (mBodyBegin) {
            mPartProcessor.process(mBoundary, mIndex, outParams, outFiles, cacheDir);
            mPartProcessor.process(data, outParams, outFiles, cacheDir);
            mConut += mIndex + 1;
        }

        mIndex = 0;
    }

    @Override
    public void process(byte[] datas,
                        int size,
                        Map<String, String> outParams,
                        Map<String, File> outFiles,
                        String cacheDir) {
        for (int i = 0; i < size; i++) {
            process(datas[i], outParams, outFiles, cacheDir);
        }
    }

    @Override
    public void reset() {
        mIsFinished = false;
        mBodyBegin = false;
        mIndex = 0;
        mConut = 0;
    }
}
