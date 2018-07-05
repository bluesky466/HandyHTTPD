package me.linjw.handyhttpd.samples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import me.linjw.handyhttpd.HandyHttpd;
import me.linjw.handyhttpd.annotation.Path;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PORT = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            new HandyHttpd.ServerBuilder()
                    .setTempFileDir(getCacheDir().getAbsolutePath())
                    .create()
                    .loadService(this)
                    .start(PORT);

        } catch (Exception e) {
            Log.e(TAG, "create HandyHttpdServer err", e);
        }
    }

    @Path("/test")
    public String test(String arg) {
        Log.d(TAG, "arg = " + arg);
        return "";
    }
}
