package me.linjw.handyhttpd.tempfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import me.linjw.handyhttpd.HandyHttpd;

/**
 * TempFile.
 * Created by linjw on 18-4-19.
 */

public class TempFile {
    private File mTempFile;
    private OutputStream mOutputStream;

    TempFile(File tempFileDir) throws IOException {
        mTempFile = File.createTempFile("HandyHttpd-", ".tmp", tempFileDir);
        mOutputStream = new FileOutputStream(mTempFile);
    }

    /**
     * get absolute path.
     *
     * @return absolute path
     */
    public String getAbsolutePath() {
        return mTempFile.getAbsolutePath();
    }

    /**
     * open OutputStream.
     *
     * @return OutputStream
     */
    public OutputStream open() {
        return mOutputStream;
    }

    /**
     * delete temp file.
     */
    public void delete() {
        HandyHttpd.safeClose(mOutputStream);
        mTempFile.delete();
    }
}
