package com.example.doctorsj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyOTPBroadcastReceiver extends BroadcastReceiver {

    Getotplistener getotplistener;

    public MyOTPBroadcastReceiver() {

    }

    public void setotplistener(Getotplistener getotplistener) {

        this.getotplistener = getotplistener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equals(SmsRetriever.SMS_RETRIEVED_ACTION)) {

            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                Status status = (Status) bundle.get(SmsRetriever.EXTRA_STATUS);

                if (status != null) {

                    switch (status.getStatusCode()) {

                        case CommonStatusCodes
                                .SUCCESS:

                            String message = (String) bundle.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                            if (message != null) {

                                Pattern pattern = Pattern.compile("\\d{6}");
                                Matcher matcher = pattern.matcher(message);
                                if (matcher.find()) {

                                    String otp = matcher.group(0);
                                    if (this.getotplistener != null) {

                                        this.getotplistener.getotp(otp);
                                    }
                                }
                            }

                            break;

                        case CommonStatusCodes.TIMEOUT:

                            if (getotplistener != null) {

                                this.getotplistener.timeout();
                            }

                    }


                }

            }
        }
    }

    public interface Getotplistener{

        public void getotp(String otp);
        public void timeout();
    }
}
