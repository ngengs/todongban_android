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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.ngengs.skripsi.todongban.data.enumerations.Values;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;

import timber.log.Timber;

public class ResponseActionActivity extends AppCompatActivity {
    public static String ACTION_REJECT = "reject";
    public static String ACTION_ACCEPT = "accept";
    public static String PARAMS_RESPONSE_ID = "id_response";

    private API mApi;
    private SharedPreferences mSharedPreferences;
    private String responseId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApi = NetworkHelpers.provideAPI(this);
        mSharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        if (getIntent().getExtras() != null) {
            responseId = getIntent().getStringExtra(PARAMS_RESPONSE_ID);
        }
        if (TextUtils.isEmpty(responseId)) {
            responseId = mSharedPreferences.getString(
                    Values.SHARED_PREFERENCES_KEY_ID_RESPONSE_SEARCH, null);
        }
        if (TextUtils.isEmpty(responseId)) {
            throw new RuntimeException("No Response Id");
        }
        cancelNotification(this);
        stopAutoCancelNotification(this);
        if (getIntent().getAction() != null) {
            Timber.d("onCreate: %s", "Check Action");
            if (getIntent().getAction().equals(ACTION_ACCEPT)) {
                acceptResponse();
                return;
            } else if (getIntent().getAction().equals(ACTION_REJECT)) {
                rejectResponse();
                return;
            }
        }
        finish();
    }

    public static void cancelNotification(Context context) {
        Timber.d("cancelNotification() called with: context = [ %s ]", context);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(Values.NOTIFICATION_TAG_REQUEST_SEARCH,
                                   Values.NOTIFICATION_ID_REQUEST_SEARCH);
    }

    public static void stopAutoCancelNotification(Context context) {
        Timber.d("stopAutoCancelNotification() called with: context = [ %s ]", context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.cancel(Values.NOTIFICATION_TAG_REQUEST_SEARCH);
    }

    private void rejectResponse() {
        Timber.d("rejectResponse() called");
        Toast.makeText(this, "Reject", Toast.LENGTH_SHORT).show();
        mApi.responseSearchReject(responseId).enqueue(new ApiResponse<>(response -> {
            setResult(RESULT_OK);
            finish();
        }, throwable -> {
            setResult(RESULT_CANCELED);
            finish();
        }));
    }

    private void acceptResponse() {
        Timber.d("acceptResponse() called");
        Toast.makeText(this, "Accept", Toast.LENGTH_SHORT).show();
        mApi.responseSearchAccept(responseId).enqueue(new ApiResponse<>(response -> {
            setResult(RESULT_OK);
            finish();
        }, throwable -> {
            setResult(RESULT_CANCELED);
            finish();
        }));
//        mApi.responseSearchAccept(responseId).enqueue();
//        mApi.responseSearchAccept(responseId).enqueue(new ApiResponse<>(response -> {
//            Timber.d("acceptResponse: %s", "Here");
//            setResult(RESULT_OK);
//            Timber.d("acceptResponse: %s", "Here 2");
//            finish();
//        }, throwable -> {
//            Timber.d("acceptResponse: %s", "Here Error");
//            setResult(RESULT_CANCELED);
//            Timber.d("acceptResponse: %s", "Here Error 2");
//            finish();
//        }));
    }
}
