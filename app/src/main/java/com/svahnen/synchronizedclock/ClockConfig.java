package com.svahnen.synchronizedclock;

import android.os.Handler;
import android.widget.TextView;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

public class ClockConfig  {

    private Handler hUpdate;
    private Runnable rUpdate;
    String time = "Loading...";

    public static final String TIME_SERVER = "time-a.nist.gov";

    public ClockConfig(TextView clock) throws IOException {

        hUpdate = new Handler();
        rUpdate = () -> clock.setText(time);
        clock.setText(time);

        Thread tUpdate = new Thread() {
            public void run() {
                while(true) {
                    hUpdate.post(rUpdate);
                    try {
                        time = getCurrentNetworkTime().toString();
                        sleep(10000);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        tUpdate.start();
    }


    public static Date getCurrentNetworkTime() throws IOException {
        NTPUDPClient timeClient = new NTPUDPClient();
        InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
        TimeInfo timeInfo = timeClient.getTime(inetAddress);
        long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
        timeClient.close();
        return new Date(returnTime);
    }
}
