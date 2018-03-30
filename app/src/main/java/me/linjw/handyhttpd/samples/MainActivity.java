package me.linjw.handyhttpd.samples;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import me.linjw.handyhttpd.HandyHttpdServer;

public class MainActivity extends AppCompatActivity {
    private static final int PORT = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new HandyHttpdServer(PORT).start();
    }
}
