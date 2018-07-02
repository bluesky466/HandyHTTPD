package me.linjw.handyhttpd.httpcore.multipartbody;

import java.io.File;
import java.util.Map;

/**
 * Created by linjw on 18-7-1.
 */

public interface IProcessor {
    void process(byte data,
                 Map<String, String> outParams,
                 Map<String, File> outFiles,
                 String cacheDir);

    void process(byte[] datas,
                 int size,
                 Map<String, String> outParams,
                 Map<String, File> outFiles,
                 String cacheDir);

    void reset();
}
