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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.ngengs.skripsi.todongban.data.enumerations.Values;
import com.ngengs.skripsi.todongban.data.remote.SingleStringData;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import retrofit2.Response;
import timber.log.Timber;

public class SignoutActivity extends AppCompatActivity {

    private String mToken;
    private API mApi;
    private SharedPreferences mSharedPreferences;
    private MaterialProgressBar mProgressProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signout);
        initView();
        mSharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES_NAME,
                                                  MODE_PRIVATE);
        mToken = mSharedPreferences.getString(Values.SHARED_PREFERENCES_KEY_TOKEN, null);
        mApi = NetworkHelpers.provideAPI(this);
        signoutUser();
    }

    private void signoutUser() {
        mProgressProcess.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(mToken)) {
            mApi.signout(NetworkHelpers.authorizationHeader(mToken))
                .enqueue(new ApiResponse<>(
                        this::signoutSuccess,
                        this::signoutFailed)
                );
        } else {
            signoutFailed(new Exception("Tidak terdapat token untuk keluar"));
        }
    }

    public static void runSignout(AppCompatActivity activity) {
        Intent intent = new Intent(activity, SignoutActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    private void signoutSuccess(Response<SingleStringData> response) {
        Timber.d("signoutSuccess() called with: response = [ %s ]", response);
        mSharedPreferences.edit()
                          .putString(Values.SHARED_PREFERENCES_KEY_TOKEN, null)
                          .apply();

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(
                new GooglePlayDriver(getBaseContext()));
        dispatcher.cancelAll();
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private void signoutFailed(Throwable throwable) {
        Timber.e(throwable, "signoutFailed: ");
        mProgressProcess.setVisibility(View.GONE);
        Snackbar.make(mProgressProcess, R.string.message_failed_signout, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.try_again, view -> signoutUser())
                .show();
    }

    private void initView() {
        mProgressProcess = findViewById(R.id.progress_process);
    }
}
