package com.a20170208.tranvanhay.appat;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Hay Tran on 22/04/2017.
 */
public class NetworkPing {

    private static final String TAG = "NetworkPing";

    public static String pingError = null;

    /**
     * Ping a host and return an int value of 0 or 1 or 2 0=success, 1=fail, 2=error
     *
     * Does not work in Android emulator and also delay by '1' second if host not pingable
     * In the Android emulator only ping to 127.0.0.1 works
     *
     * @param String host in dotted IP address format
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static int pingHost(String host) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("ping -c 3 " + host);
        proc.waitFor();
        int exit = proc.exitValue();
        return exit;
    }

    public static String ping(String host) throws IOException, InterruptedException {
        StringBuilder echo = new StringBuilder();
        Runtime runtime = Runtime.getRuntime();
        Log.d(TAG, "About to ping using runtime.exec");
        Process proc = runtime.exec("ping -c 1 " + host);
        proc.waitFor();
        int exit = proc.exitValue();
        if (exit == 0) {
            InputStreamReader reader = new InputStreamReader(proc.getInputStream());
            BufferedReader buffer = new BufferedReader(reader);
            String line = "";
            String ignoreLine = "";
            ignoreLine = buffer.readLine();
            line = buffer.readLine();
            buffer.close();
            reader.close();
            return line;
        } else if (exit == 1) {
            pingError = "1";
        } else {
            pingError = "2";
        }
        return pingError;
    }

    /**
     * getPingStats interprets the text result of a Linux ping command
     *
     * Set pingError on error and return null
     *
     * http://en.wikipedia.org/wiki/Ping
     *
     * PING 127.0.0.1 (127.0.0.1) 56(84) bytes of data.
     * 64 bytes from 127.0.0.1: icmp_seq=1 ttl=64 time=0.251 ms
     * 64 bytes from 127.0.0.1: icmp_seq=2 ttl=64 time=0.294 ms
     * 64 bytes from 127.0.0.1: icmp_seq=3 ttl=64 time=0.295 ms
     * 64 bytes from 127.0.0.1: icmp_seq=4 ttl=64 time=0.300 ms
     *
     * --- 127.0.0.1 ping statistics ---
     * 4 packets transmitted, 4 received, 0% packet loss, time 0ms
     * rtt min/avg/max/mdev = 0.251/0.285/0.300/0.019 ms
     *
     * PING 192.168.0.2 (192.168.0.2) 56(84) bytes of data.
     *
     * --- 192.168.0.2 ping statistics ---
     * 1 packets transmitted, 0 received, 100% packet loss, time 0ms
     *
     * # ping 321321.
     * ping: unknown host 321321.
     *
     * 1. Check if output contains 0% packet loss : Branch to success -> Get stats
     * 2. Check if output contains 100% packet loss : Branch to fail -> No stats
     * 3. Check if output contains 25% packet loss : Branch to partial success -> Get stats
     * 4. Check if output contains "unknown host"
     *
     * @param s
     */
    public static String getPingStats(String s) {
        if (s.contains("0% packet loss")) {
            int start = s.indexOf("/mdev = ");
            int end = s.indexOf(" ms\n", start);
            s = s.substring(start + 8, end);
            String stats[] = s.split("/");
            return stats[2];
        } else if (s.contains("100% packet loss")) {
            pingError = "100% packet loss";
            return null;
        } else if (s.contains("% packet loss")) {
            pingError = "partial packet loss";
            return null;
        } else if (s.contains("unknown host")) {
            pingError = "unknown host";
            return null;
        } else {
            pingError = "unknown error in getPingStats";
            return null;
        }
    }
}