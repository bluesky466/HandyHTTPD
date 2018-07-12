package me.linjw.handyhttpd.samples;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import me.linjw.handyhttpd.HandyHttpd;
import me.linjw.handyhttpd.annotation.Path;
import me.linjw.handyhttpd.httpcore.HttpResponse;
import me.linjw.handyhttpd.httpcore.MimeType;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PORT = 8888;

    private TextView mNickName;
    private ImageView mHeader;
    private HandyHttpd.Server mServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tip = (TextView) findViewById(R.id.tip);
        tip.setText("access http://" + getIpAddressString() + ":" + PORT + "/edit to edit user info");

        mNickName = (TextView) findViewById(R.id.nickname);
        mHeader = (ImageView) findViewById(R.id.header);

        try {
            mServer = new HandyHttpd.ServerBuilder()
                    .setTempFileDir(getCacheDir().getAbsolutePath())
                    .loadService(this)
                    .create();
            mServer.start(PORT);

        } catch (Exception e) {
            Log.e(TAG, "create HandyHttpdServer err", e);
        }
    }

    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, "get ip err", e);
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            mServer.stop();
        } catch (Exception e) {
            Log.e(TAG, "stop HandyHttpdServer err", e);
        }
    }

    @Path("/edit")
    public HttpResponse getEditUserInfoHtml() {
        try {
            return HandyHttpd.newResponse(
                    HttpResponse.Status.OK,
                    getResources().getAssets().open("edit.html"),
                    MimeType.TEXT_HTML);
        } catch (IOException e) {
            return HandyHttpd.newResponse(HttpResponse.Status.NOT_FOUND, "404 Not Found");
        }
    }

    @Path("/saveinfo")
    public String saveInfo(final String nickname, final File header) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNickName.setText(nickname);
                mHeader.setImageBitmap(BitmapFactory.decodeFile(header.getAbsolutePath()));
            }
        });
        return "OK";
    }
}

