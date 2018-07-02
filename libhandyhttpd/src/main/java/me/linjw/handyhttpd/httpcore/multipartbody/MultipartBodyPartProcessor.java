package me.linjw.handyhttpd.httpcore.multipartbody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import me.linjw.handyhttpd.HandyHttpd;

/**
 * Created by linjw on 18-7-1.
 */

public class MultipartBodyPartProcessor implements IProcessor {
    public static final int BUFFER_SIZE = 128;

    private boolean mIsData;

    private int mCount;
    private byte[] mBuffer = new byte[BUFFER_SIZE];
    private String mLine = "";

    private DataProcessor mDataProcessor;
    private Map<String, String> mOutParams;
    private Map<String, File> mOutFiles;

    private StringDataProcessor mStringDataProcessor = new StringDataProcessor();
    private FileDataProcessor mFileDataProcessor = new FileDataProcessor();

    @Override
    public void process(byte data,
                        Map<String, String> outParams,
                        Map<String, File> outFiles,
                        String cacheDir) {
        mOutParams = outParams;
        mOutFiles = outFiles;
        if (mIsData) {
            if (mDataProcessor != null) {
                mDataProcessor.write(data);
            }
        } else if (data == '\n') {
            if (mCount == 0) {
                mIsData = true;
                return;
            }

            if (mBuffer[mCount - 1] == '\r') {
                mLine += new String(mBuffer, 0, mCount - 1);
            } else {
                mLine += new String(mBuffer, 0, mCount);
            }

            if (!mLine.isEmpty()) {
                parseHeaderLine(mLine, cacheDir);
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
        mIsData = false;
        mLine = "";
        mCount = 0;

        if (mDataProcessor != null) {
            mDataProcessor.flush(mOutParams, mOutFiles);
        }
    }

    private void parseHeaderLine(String line, String cacheDir) {
        int index = findStartSubStringEndIgnoreCase(line, "content-disposition", 0);
        if (index > 0) {
            int indexName = line.indexOf(" name");
            if (indexName < 0) {
                return;
            }

            String name = getName(line, indexName + " name".length());

            String filename = null;
            int indexFilename = line.indexOf(" filename");
            if (indexFilename >= 0) {
                filename = getName(line, indexFilename + " filename".length());
            }
            if (name != null && filename == null) {
                mStringDataProcessor.setName(name);
                mDataProcessor = mStringDataProcessor;
            } else if (name != null) {
                mFileDataProcessor.setFile(name, cacheDir, filename);
                mDataProcessor = mFileDataProcessor;
            }
        }
    }

    private String getName(String line, int begin) {
        StringBuilder builder = null;

        for (int i = begin; i < line.length(); i++) {
            if (line.charAt(i) == '\"') {
                if (builder == null) {
                    builder = new StringBuilder();
                } else {
                    return builder.toString();
                }
            } else if (builder != null) {
                builder.append(line.charAt(i));
            }
        }
        return null;
    }

    private int findStartSubStringEndIgnoreCase(String str, String sub, int offset) {
        for (int i = offset; i < str.length() - sub.length(); i++) {
            if (str.charAt(i) == ' '
                    || str.charAt(i) == '\t') {
                continue;
            }

            for (int j = 0; j < sub.length(); j++) {
                if (Character.toLowerCase(str.charAt(i + j))
                        != Character.toLowerCase(sub.charAt(j))) {
                    return -1;
                }

                if (j + 1 == sub.length()) {
                    return i + sub.length();
                }
            }
            return -1;
        }
        return -1;
    }

    private interface DataProcessor {
        void write(byte data);

        void flush(Map<String, String> outParams, Map<String, File> outFiles);
    }

    private static class StringDataProcessor implements DataProcessor {
        private String mName;
        private StringBuilder mBuilder = new StringBuilder();

        public void setName(String name) {
            mName = name;
        }

        @Override
        public void write(byte data) {
            mBuilder.append((char) data);
        }

        @Override
        public void flush(Map<String, String> outParams, Map<String, File> outFiles) {
            // remove linebreak
            if (mBuilder.charAt(mBuilder.length() - 1) == '\n') {
                mBuilder.delete(mBuilder.length() - 1, mBuilder.length());
                if (mBuilder.charAt(mBuilder.length() - 1) == '\r') {
                    mBuilder.delete(mBuilder.length() - 1, mBuilder.length());
                }
            }
            if (outParams != null) {
                outParams.put(mName, mBuilder.toString());
            }
            mBuilder.delete(0, mBuilder.length());
        }
    }

    private static class FileDataProcessor implements DataProcessor {
        private int mCount = 0;
        private byte[] mBuffer = new byte[2];

        private FileOutputStream mFileOutputStream;
        private File mFile;
        private String mName;

        void setFile(String name, String cacheDir, String filename) {
            if (cacheDir == null) {
                return;
            }

            mName = name;
            mFile = new File(cacheDir, filename);
            try {
                mFileOutputStream = new FileOutputStream(mFile);
            } catch (IOException e) {
                HandyHttpd.Log.log(e);
            }
        }

        @Override
        public void write(byte data) {
            if (mCount < mBuffer.length) {
                mBuffer[mCount] = data;
                mCount++;
                return;
            }
            if (mFileOutputStream != null) {
                try {
                    mFileOutputStream.write(mBuffer[0]);
                } catch (IOException e) {
                    HandyHttpd.Log.log(e);
                }
                mBuffer[0] = mBuffer[1];
                mBuffer[1] = data;
            }
        }

        @Override
        public void flush(Map<String, String> outParams, Map<String, File> outFiles) {
            if (mFileOutputStream != null) {
                try {
                    flushBufferToFile();
                    mFileOutputStream.close();
                    if (outFiles != null && mName != null && mFile != null) {
                        outFiles.put(mName, mFile);
                    }
                } catch (IOException e) {
                    HandyHttpd.Log.log(e);
                }

                mFile = null;
                mName = null;
                mCount = 0;
            }
        }

        private void flushBufferToFile() {
            try {
                if (mCount == 1 && mBuffer[0] != '\n') {
                    mFileOutputStream.write(mBuffer[0]);
                } else if (mCount > 1 && mBuffer[0] != '\r') {
                    mFileOutputStream.write(mBuffer[0]);
                }

                if (mCount > 1 && mBuffer[1] != '\n') {
                    mFileOutputStream.write(mBuffer[1]);
                }
            } catch (IOException e) {
                HandyHttpd.Log.log(e);
            }
        }
    }
}
