package me.linjw.handyhttpd.tempfile;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * TempFileManager.
 * Created by linjw on 18-4-19.
 */

public class TempFileManager {
    private File mTempFileDir;
    private List<TempFile> mTempFiles = new LinkedList<>();

    public TempFileManager(String tempFileDir) {
        mTempFileDir = new File(tempFileDir);
        if (!mTempFileDir.exists()) {
            mTempFileDir.mkdirs();
        }
    }


    /**
     * create temp file.
     *
     * @return TempFile
     * @throws IOException
     */
    public TempFile createTempFile() throws IOException {
        TempFile tempFile = new TempFile(mTempFileDir);
        mTempFiles.add(tempFile);
        return tempFile;
    }


    /**
     * clear all temp files.
     */
    public void clear() {
        for (TempFile tempFile : mTempFiles) {
            tempFile.delete();
        }
        mTempFiles.clear();
    }
}
