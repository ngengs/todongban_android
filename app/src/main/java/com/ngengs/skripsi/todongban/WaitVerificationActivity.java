/*==============================================================================
 Copyright (c) 2017 Rizky Kharisma (@ngengs)


 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 =============================================================================*/

package com.ngengs.skripsi.todongban;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;

import com.ngengs.skripsi.todongban.data.enumerations.Values;

public class WaitVerificationActivity extends AppCompatActivity {
    public static final String ARGS_BROADCAST_FILTER
            = "com.ngengs.skripsi.todongban.UPDATE_SIGNUP_PROCESS";
    public static final String ARGS_BROADCAST_DATA = "SIGNUP_CODE";
    private SignupBroadcastReceiver mBroadcastReceiver;
    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_verification);
        mBroadcastReceiver = new SignupBroadcastReceiver();
        mIntentFilter = new IntentFilter(ARGS_BROADCAST_FILTER);
        registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void cleanNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(Values.NOTIFICATION_TAG_SIGNUP,
                                   Values.NOTIFICATION_ID_SIGNUP);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(1000);
        }
    }

    private void goToSplashScreen() {
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private class SignupBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int signupCode = intent.getIntExtra(ARGS_BROADCAST_DATA,
                                                Values.NOTIFICATION_CODE_SIGNUP_FAILED);
            if (signupCode == Values.NOTIFICATION_CODE_SIGNUP_SUCCESS) {
                cleanNotification();
                goToSplashScreen();
            }
        }
    }
}
