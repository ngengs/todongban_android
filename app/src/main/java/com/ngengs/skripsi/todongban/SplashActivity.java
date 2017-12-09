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

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ngengs.skripsi.todongban.data.enumerations.Values;
import com.ngengs.skripsi.todongban.data.local.User;
import com.ngengs.skripsi.todongban.data.remote.CheckStatus;
import com.ngengs.skripsi.todongban.services.LocationChangedService;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;
import com.ngengs.skripsi.todongban.utils.playservices.PlayServicesAvailableResponse;
import com.ngengs.skripsi.todongban.utils.playservices.PlayServicesUtils;

import retrofit2.Response;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity implements PlayServicesAvailableResponse {

    // Permission Variable
    private static final String[] PERMISSION_ALL = {Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                                    Manifest.permission.CAMERA,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final int REQUEST_CODE_PLAY_SERVICES = 20;

    // View Binding
    private TextView mTextProcess;

    // Connection
    @SuppressWarnings("FieldCanBeLocal")
    private API mApi;

    private User mUser;
    private SharedPreferences mSharedPreferences;
    private GoogleApiAvailability mGoogleApiAvailability;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        mGoogleApiAvailability = GoogleApiAvailability.getInstance();
        runApp(savedInstanceState, true);
    }

    private void initView() {
        mTextProcess = findViewById(R.id.textProcess);
    }

    @SuppressWarnings("UnusedParameters")
    private void runApp(Bundle savedInstanceState, boolean needCheckPermission) {
        // Check permission if user use phone with SDK >= 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && needCheckPermission) {
            Timber.i("onCreate: check permission");
            mTextProcess.setText(R.string.splashCheckPermission);
            checkAppPermission();
        }

        // Check if user has logged in
        Timber.d("onCreate: check user data");
        mTextProcess.setText(R.string.splashCheckUser);
        mSharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        mToken = mSharedPreferences.getString(Values.SHARED_PREFERENCES_KEY_TOKEN, null);
        Timber.d("runApp: token: %s", mToken);
        // TODO Handle main screen from SplashScreen
        if (TextUtils.isEmpty(mToken)) {
            Timber.d("mToken: run sign in / sign up");
            runPageWithoutToken(false);
        } else {
            Timber.d("onCreate: token not empty");
            checkToken();
        }
    }

    private void runPageWithoutToken(boolean clearToken) {
        Timber.d("runPageWithoutToken() called with: clearToken = [ %s ]", clearToken);
        if (clearToken) {
            mSharedPreferences.edit().putString(Values.SHARED_PREFERENCES_KEY_TOKEN, null).apply();
        }
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void runPageWithStatusUser(User user) {
        Timber.d("runPageWithStatusUser() called with: user = [ %s ]", user);
        Intent intent = null;
        if (user.getStatus() == User.STATUS_DEACTIVE) {
            intent = new Intent(this, WaitVerificationActivity.class);

        } else if (user.getStatus() == User.STATUS_ACTIVE) {
            mSharedPreferences.edit()
                              .putInt(Values.SHARED_PREFERENCES_KEY_USER_TYPE, user.getType())
                              .apply();
            // Show Main Screen
            if (user.getType() == User.TYPE_PERSONAL) {
                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(
                        new GooglePlayDriver(this));
                dispatcher.cancelAll();
                Bundle jobBundle = new Bundle();
                jobBundle.putString(LocationChangedService.PARAM_TOKEN, mToken);
                jobBundle.putInt(LocationChangedService.PARAM_TYPE, user.getType());
                Job job = dispatcher
                        .newJobBuilder()
                        .setService(LocationChangedService.class)
                        .setTag("job_update_location")
                        .setConstraints(Constraint.ON_ANY_NETWORK)
                        .setLifetime(Lifetime.FOREVER)
                        .setRecurring(true)
                        .setReplaceCurrent(true)
                        .setTrigger(Trigger.executionWindow(10 * 60, 20 * 60))
                        .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                        .setExtras(jobBundle)
                        .build();
                dispatcher.mustSchedule(job);
                intent = new Intent(this, MainActivityPersonal.class);
                intent.putExtra(MainActivityPersonal.ARGS_USER, user);
            } else {
                intent = new Intent(this, MainActivityGarage.class);
            }

        } else {
//            mSharedPreferences.edit().putString("token", null).apply();
            // Show Login Page
//            intent = new Intent(this, SignupActivity.class);
            runPageWithoutToken(true);
        }
        if (intent != null) {
            startActivity(intent);
            finish();
        }
    }

    private void checkToken() {
        Timber.d("checkToken() called");
        mApi = NetworkHelpers.provideAPI(this);
        mApi.checkStatus(NetworkHelpers.authorizationHeader(mToken))
            .enqueue(new ApiResponse<>(this::checkUserSuccess, this::checkUserFailure));
    }

    /**
     * TODO JavaDoc
     */
    private void checkAppPermission() {
        Timber.d("checkAppPermission() called");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : PERMISSION_ALL) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    requestAppPermissionPrompt();
                    break;
                }
            }
        }
    }

    /**
     * TODO Javadoc
     */
    @SuppressWarnings("JavaDoc")
    private void requestAppPermissionPrompt() {
        Timber.d("requestAppPermissionPrompt() called");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if user need explanation
            boolean needPermissionExplanation = false;
            for (String permissionCheck : PERMISSION_ALL) {
                if (shouldShowRequestPermissionRationale(permissionCheck)) {
                    needPermissionExplanation = true;
                    break;
                }
            }
            if (needPermissionExplanation) {
                // Show the explanation
                String explanationTitle = getString(R.string.explanationPermissionTitle);
                String explanationDetail = getString(R.string.explanationPermissionDetail);

                new MaterialDialog.Builder(this)
                        .title(explanationTitle)
                        .content(explanationDetail)
                        .positiveText(R.string.ok)
                        .canceledOnTouchOutside(false)
                        .onPositive((dialog, wich) -> requestPermissions(PERMISSION_ALL,
                                                                         REQUEST_CODE_PERMISSION))
                        .show();
            } else {
                // Request permission without explanation
                requestPermissions(PERMISSION_ALL, REQUEST_CODE_PERMISSION);
            }
        }

    }

    private void checkPlayService() {
        int playServicesAvailable = mGoogleApiAvailability.isGooglePlayServicesAvailable(this);
        PlayServicesUtils.checkStatus(playServicesAvailable, this);
    }

    @Override
    public void onSuccess() {
        if (mUser != null) {
            runPageWithStatusUser(mUser);
        }
    }

    @Override
    public void onError(int resultCode) {
        Timber.d("onError() called with: resultCode = [ %s ]", resultCode);
        mTextProcess.setText("Gagal menggunakan Google Play Services");
        if (mGoogleApiAvailability.isUserResolvableError(resultCode)) {
            mGoogleApiAvailability.getErrorDialog(this, resultCode, REQUEST_CODE_PLAY_SERVICES)
                                  .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PLAY_SERVICES && resultCode == RESULT_OK) {
            checkPlayService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Timber.d(
                "onRequestPermissionsResult() called with: requestCode = [ %s ], permissions = [ %s ], grantResults = [ %s ]",
                requestCode, permissions, grantResults);
        boolean shouldRequestAgain = false;
        // Check if permission granted
        if (grantResults.length > 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    shouldRequestAgain = true;
                    break;
                }
            }
        }
        // Request permission again when user denied
        if (shouldRequestAgain) {
            checkAppPermission();
        } else {
            runApp(null, false);
        }
    }

    private void checkUserSuccess(Response<CheckStatus> response) {
        Timber.d("checkUserSuccess() called with: response = [ %s ]", response);
        CheckStatus checkStatus = response.body();
        if (checkStatus != null) {
//            runPageWithStatusUser(checkStatus.getData());
            mUser = checkStatus.getData();
            checkPlayService();
        } else {
            checkUserFailure(new Throwable("Data empty"));
            mTextProcess.setText("Data login telah kadaluarsa");
            runPageWithoutToken(true);
        }
    }

    private void checkUserFailure(Throwable t) {
        Timber.e(t, "onFailure: ");
        mTextProcess.setText("Terjadi kesalahan pada server");
    }
}
