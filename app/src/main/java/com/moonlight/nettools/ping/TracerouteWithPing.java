package com.moonlight.nettools.ping;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.moonlight.nettools.MoonApp;
import com.moonlight.nettools.R;
import com.moonlight.nettools.utils.NetUtils;
import com.moonlight.nettools.utils.ThreadUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 * This class contain everything needed to launch a traceroute using the ping command
 *
 * @author Olivier Goutay
 */
public class TracerouteWithPing implements IPing{
    private static final String TAG = "TracerouteWithPing";
    private static final String PING = "PING";
    private static final String FROM_PING = "From";
    private static final String SMALL_FROM_PING = "from";
    private static final String PARENTHESE_OPEN_PING = "(";
    private static final String PARENTHESE_CLOSE_PING = ")";
    private static final String TIME_PING = "time=";
    private static final String EXCEED_PING = "exceed";
    private static final String UNREACHABLE_PING = "100%";

    private TracerouteContainer latestTrace;
    private int ttl;
    private int finishedTasks;
    private String urlToPing;
    private String ipToPing;
    private float elapsedTime;
    private final Context mContext;

    // timeout handling
    private static final int TIMEOUT = 30000;
    private Handler handlerTimeout;
    private static Runnable runnableTimeout;

    private TracerouteWithPing() {
        mContext = MoonApp.getApp();
    }

    /**
     * Launches the Traceroute
     *
     * @param url    The url to trace
     * @param maxTtl The max time to live to set (ping param)
     */
    @Override
    public void ping(String url, int maxTtl) {
        this.ttl = 1;
        this.finishedTasks = 0;
        this.urlToPing = url;

        new ExecutePingAsyncTask(maxTtl).execute();
    }

    private static final class SInstanceHolder {
        static final IPing sInstance = new TracerouteWithPing();
    }

    public static IPing getInstance() {
        return SInstanceHolder.sInstance;
    }


    /**
     * Allows to timeout the ping if TIMEOUT exceeds. (-w and -W are not always supported on Android)
     */
    private class TimeOutAsyncTask extends AsyncTask<Void, Void, Void> {

        private ExecutePingAsyncTask task;
        private int ttlTask;

        public TimeOutAsyncTask(ExecutePingAsyncTask task, int ttlTask) {
            this.task = task;
            this.ttlTask = ttlTask;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (handlerTimeout == null) {
                handlerTimeout = new Handler();
            }

            // stop old timeout
            if (runnableTimeout != null) {
                handlerTimeout.removeCallbacks(runnableTimeout);
            }
            // define timeout
            runnableTimeout = new Runnable() {
                @Override
                public void run() {
                    if (task != null) {
                        Log.e(TAG, ttlTask + " task.isFinished()" + finishedTasks + " " + (ttlTask == finishedTasks));
                        if (ttlTask == finishedTasks) {
                            Toast.makeText(mContext, mContext.getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                            task.setCancelled(true);
                            task.cancel(true);
//                            mContext.stopProgressBar();
                        }
                    }
                }
            };
            // launch timeout after a delay
            handlerTimeout.postDelayed(runnableTimeout, TIMEOUT);
            super.onPostExecute(result);
        }
    }

    /**
     * The task that ping an ip, with increasing time to live (ttl) value
     */
    private class ExecutePingAsyncTask extends AsyncTask<Void, Void, String> {

        private boolean isCancelled;
        private int maxTtl;

        public ExecutePingAsyncTask(int maxTtl) {
            this.maxTtl = maxTtl;
        }

        /**
         * Launches the ping, launches InetAddress to retrieve url if there is one, store trace
         */
        @Override
        protected String doInBackground(Void... params) {
            if (NetUtils.isNetworkAvailable(mContext)) {
                try {
                    String res = launchPing(urlToPing);

                    TracerouteContainer trace;
                    String ip = parseIpFromPing(res);

                    if (res.contains(UNREACHABLE_PING) && !res.contains(EXCEED_PING)) {
                        // Create the TracerouteContainer object when ping
                        // failed
                        trace = new TracerouteContainer("", ip, elapsedTime, false);
                    } else {
                        // Create the TracerouteContainer object when succeed
                        trace = new TracerouteContainer("", ip, ttl == maxTtl ? Float.parseFloat(parseTimeFromPing(res)) : elapsedTime, true);
                    }

                    // Get the host name from ip (unix ping do not support
                    // hostname resolving)
                    InetAddress inetAddr = InetAddress.getByName(trace.getIp());
                    String hostname = inetAddr.getHostName();
                    String canonicalHostname = inetAddr.getCanonicalHostName();
                    trace.setHostname(hostname);
                    latestTrace = trace;
                    Log.d(TAG, "hostname : " + hostname);
                    Log.d(TAG, "canonicalHostname : " + canonicalHostname);

                    // Store the TracerouteContainer object
                    Log.d(TAG, trace.toString());

                    // Not refresh list if this ip is the final ip but the ttl is not maxTtl
                    // this row will be inserted later
                    if (!ip.equals(ipToPing) || ttl == maxTtl) {
//                        mContext.refreshList(trace);
                    }

                    return res;
                } catch (final Exception e) {
                    ThreadUtil.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            onException(e);
                        }
                    });
                }
            } else {
                return mContext.getString(R.string.no_connectivity);
            }
            return "";
        }

        /**
         * Launches ping command
         *
         * @param url The url to ping
         * @return The ping string
         */
        @SuppressLint("NewApi")
        private String launchPing(String url) throws Exception {
            // Build ping command with parameters
            Process p;
            String command = "";

            String format = "ping -c 1 -t %d ";
            command = String.format(format, ttl);

            Log.d(TAG, "Will launch : " + command + url);

            long startTime = System.nanoTime();
            elapsedTime = 0;
            // timeout task
            new TimeOutAsyncTask(this, ttl).execute();
            // Launch command
            p = Runtime.getRuntime().exec(command + url);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            // Construct the response from ping
            String s;
            StringBuilder res = new StringBuilder();
            while ((s = stdInput.readLine()) != null) {
                res.append(s).append("\n");
                if (s.contains(FROM_PING) || s.contains(SMALL_FROM_PING)) {
                    // We store the elapsedTime when the line from ping comes
                    elapsedTime = (System.nanoTime() - startTime) / 1000000.0f;
                }
            }

            p.destroy();

            if (res.toString().equals("")) {
                throw new IllegalArgumentException();
            }

            // Store the wanted ip adress to compare with ping result
            if (ttl == 1) {
                ipToPing = parseIpToPingFromPing(res.toString());
            }

            return res.toString();
        }

        /**
         * Treat the previous ping (launches a ttl+1 if it is not the final ip, refresh the list on view etc...)
         */
        @Override
        protected void onPostExecute(String result) {
            if (!isCancelled) {
                try {
                    if (!"".equals(result)) {
                        if (mContext.getString(R.string.no_connectivity).equals(result)) {
                            Toast.makeText(mContext, mContext.getString(R.string.no_connectivity), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, result);

                            if (latestTrace != null && latestTrace.getIp().equals(ipToPing)) {
                                if (ttl < maxTtl) {
                                    ttl = maxTtl;
                                    new ExecutePingAsyncTask(maxTtl).execute();
                                } else {
//                                    mContext.stopProgressBar();
                                }
                            } else {
                                if (ttl < maxTtl) {
                                    ttl++;
                                    new ExecutePingAsyncTask(maxTtl).execute();
                                }
                            }
                        }
                    }
                    finishedTasks++;
                } catch (final Exception e) {
                    ThreadUtil.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            onException(e);
                        }
                    });
                }
            }

            super.onPostExecute(result);
        }

        /**
         * Handles exception on ping
         *
         * @param e The exception thrown
         */
        private void onException(Exception e) {
            Log.e(TAG, e.toString());
            if (e instanceof IllegalArgumentException) {
                Toast.makeText(mContext, mContext.getString(R.string.no_ping), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
//            mContext.stopProgressBar();
            finishedTasks++;
        }

        public void setCancelled(boolean isCancelled) {
            this.isCancelled = isCancelled;
        }

    }

    /**
     * Gets the ip from the string returned by a ping
     *
     * @param ping The string returned by a ping command
     * @return The ip contained in the ping
     */
    private String parseIpFromPing(String ping) {
        String ip = "";
        if (ping.contains(FROM_PING)) {
            // Get ip when ttl exceeded
            int index = ping.indexOf(FROM_PING);

            ip = ping.substring(index + 5);
            if (ip.contains(PARENTHESE_OPEN_PING)) {
                // Get ip when in parenthese
                int indexOpen = ip.indexOf(PARENTHESE_OPEN_PING);
                int indexClose = ip.indexOf(PARENTHESE_CLOSE_PING);

                ip = ip.substring(indexOpen + 1, indexClose);
            } else {
                // Get ip when after from
                ip = ip.substring(0, ip.indexOf("\n"));
                if (ip.contains(":")) {
                    index = ip.indexOf(":");
                } else {
                    index = ip.indexOf(" ");
                }

                ip = ip.substring(0, index);
            }
        } else {
            // Get ip when ping succeeded
            int indexOpen = ping.indexOf(PARENTHESE_OPEN_PING);
            int indexClose = ping.indexOf(PARENTHESE_CLOSE_PING);

            ip = ping.substring(indexOpen + 1, indexClose);
        }

        return ip;
    }

    /**
     * Gets the final ip we want to ping (example: if user fullfilled google.fr, final ip could be 8.8.8.8)
     *
     * @param ping The string returned by a ping command
     * @return The ip contained in the ping
     */
    private String parseIpToPingFromPing(String ping) {
        String ip = "";
        if (ping.contains(PING)) {
            // Get ip when ping succeeded
            int indexOpen = ping.indexOf(PARENTHESE_OPEN_PING);
            int indexClose = ping.indexOf(PARENTHESE_CLOSE_PING);

            ip = ping.substring(indexOpen + 1, indexClose);
        }

        return ip;
    }

    /**
     * Gets the time from ping command (if there is)
     *
     * @param ping The string returned by a ping command
     * @return The time contained in the ping
     */
    private String parseTimeFromPing(String ping) {
        String time = "";
        if (ping.contains(TIME_PING)) {
            int index = ping.indexOf(TIME_PING);

            time = ping.substring(index + 5);
            index = time.indexOf(" ");
            time = time.substring(0, index);
        }

        return time;
    }
}