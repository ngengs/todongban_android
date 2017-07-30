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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    // Permission Variable
    private static final String[] PERMISSION_ALL = {Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.CAMERA,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE_PERMISSION = 1;

    // View Binding
    @BindView(R.id.textProcess)
    TextView mTextProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        runApp(savedInstanceState, true);
    }

    @SuppressWarnings("UnusedParameters")
    private void runApp(Bundle savedInstanceState, boolean needCheckPermission) {
        // Check permission if user use phone with SDK >= 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && needCheckPermission) {
            Log.i(TAG, "onCreate: check permission");
            mTextProcess.setText(R.string.splashCheckPermission);
            checkAppPermission();
        }

        // Check if user has logged in
        Log.d(TAG, "onCreate: check user data");
        mTextProcess.setText(R.string.splashCheckUser);
        SharedPreferences sharedPreferences = getSharedPreferences("TodongBanPreference",
                                                                   MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        // TODO Handle login or main screen from Splashscreen
        //noinspection StatementWithEmptyBody
        if (TextUtils.isEmpty(token)) {
            Log.d(TAG, "onCreate: run login");
            // Show Sign In / Sign Up Screen
        }
        else {
            Log.d(TAG, "onCreate: run home");
            // Show Main Screen
        }
    }

    /**
     * TODO JavaDoc
     */
    private void checkAppPermission() {
        Log.d(TAG, "checkAppPermission() called");
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
        Log.d(TAG, "requestAppPermissionPrompt() called");
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
            }
            else {
                // Request permission without explanation
                requestPermissions(PERMISSION_ALL, REQUEST_CODE_PERMISSION);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult() called with: requestCode = [" + requestCode
                   + "], permissions = [" + Arrays.toString(permissions) + "], grantResults = ["
                   + Arrays.toString(grantResults) + "]");
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
        }
        else {
            runApp(null, false);
        }
    }
}
