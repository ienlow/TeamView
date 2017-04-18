package com.example.isaacenlow.my_application;

import android.app.AlertDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import static android.content.ContentValues.TAG;
import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Isaac Enlow on 11/22/2016.
 */

public class BackgroundWorker extends AsyncTask<String, Void, String> {
    Context context;
    String[] place;
    String[] address;
    BackgroundWorker (Context ctx) {
        context = ctx;
    }

    /**
     * Connects to database.
     * Retrieves device's ip address.
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String login_url = "http://" + getIPAddress(true) + "/login.php";
        Log.i("network", getIPAddress(true));
        if (type.equals("login")) {
            try {
                Scanner scan;
                String team = params[1];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("team", "UTF-8") + "=" + URLEncoder.encode(team, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                scan = new Scanner(result);
                int count = 0;
                place = new String[10];
                address = new String[10];
                while (scan.hasNext()) {
                    place[count] = scan.next();
                    if (scan.hasNext())
                        address[count] = scan.next();
                    count++;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                scan.close();
                return result;
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        {
        }
        return null;
    }

    public String[] getPlace() {
        return place;
    }

    public String[] getAddress() {
        return address;
    }

    /**
     * Retrieves device's ip address.
     * @param useIPv4
     * @return
     */
    public String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%');
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { }
        return "";
    }

    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
