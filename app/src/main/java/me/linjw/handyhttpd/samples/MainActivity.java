package me.linjw.handyhttpd.samples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.linjw.handyhttpd.HandyHttpdServer;

public class MainActivity extends AppCompatActivity {
    private static final int PORT = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new HandyHttpdServer.Builder(PORT)
                .build()
                .start();
    }
}
