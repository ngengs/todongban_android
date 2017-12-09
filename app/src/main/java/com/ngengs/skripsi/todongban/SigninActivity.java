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
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ngengs.skripsi.todongban.data.enumerations.Values;
import com.ngengs.skripsi.todongban.data.remote.SingleStringData;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import retrofit2.Response;
import timber.log.Timber;

public class SigninActivity extends AppCompatActivity {
    private final static String TAG = "SigninActivity";

    private Toolbar mToolbarSignIn;
    private AppBarLayout mAppBarSignIn;
    /** Username */
    private TextInputEditText mInputSigninUsername;
    private TextInputLayout mInputLayoutSigninUsername;
    /** Password */
    private TextInputEditText mInputSigninPassword;
    private TextInputLayout mInputLayoutSigninPassword;
    /** Signin */
    private Button mButtonSignin;
    private NestedScrollView mScrollSignin;
    private MaterialProgressBar mProgress;

    private API mApi;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        initView();
        setSupportActionBar(mToolbarSignIn);
        setTitle(R.string.title_signin);
        mApi = NetworkHelpers.provideAPI(this);
        mSharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signup_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_signup) {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        Timber.tag(TAG).d("initView() called");
        mToolbarSignIn = findViewById(R.id.toolbarSignIn);
        mAppBarSignIn = findViewById(R.id.appBarSignIn);
        mInputSigninUsername = findViewById(R.id.inputSigninUsername);
        mInputLayoutSigninUsername = findViewById(R.id.inputLayoutSigninUsername);
        mInputSigninPassword = findViewById(R.id.inputSigninPassword);
        mInputLayoutSigninPassword = findViewById(R.id.inputLayoutSigninPassword);
        mButtonSignin = findViewById(R.id.buttonSignin);
        mButtonSignin.setOnClickListener(v -> signIn());
        mScrollSignin = findViewById(R.id.scrollSignIn);
        mProgress = findViewById(R.id.progressSignin);
        mProgress.setVisibility(View.GONE);
    }

    private void signIn() {
        Timber.d("signIn() called");
        mInputLayoutSigninUsername.setErrorEnabled(false);
        mInputLayoutSigninPassword.setErrorEnabled(false);
        mProgress.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(mInputSigninUsername.getText())) {
            mInputLayoutSigninUsername.setErrorEnabled(true);
            mInputLayoutSigninUsername.setError("Username tidak dapat kosong");
            mProgress.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(mInputSigninPassword.getText())) {
            mInputLayoutSigninPassword.setErrorEnabled(true);
            mInputLayoutSigninPassword.setError("Password tidak dapat kosong");
            mProgress.setVisibility(View.GONE);
            return;
        }
        mInputSigninUsername.setEnabled(false);
        mInputSigninPassword.setEnabled(false);
        String deviceId = FirebaseInstanceId.getInstance().getToken();
        String username = mInputSigninUsername.getText().toString();
        String password = mInputSigninPassword.getText().toString();

        mApi.signin(username, password, deviceId)
            .enqueue(new ApiResponse<>(this::signinSuccess, this::signinFailure));

    }

    private void signinSuccess(Response<SingleStringData> response) {
        Timber.d("signinSuccess() called with: response = [ %s ]", response);

        SingleStringData data = response.body();
        if (data != null && !TextUtils.isEmpty(data.getData())) {
            mSharedPreferences.edit()
                              .putString(Values.SHARED_PREFERENCES_KEY_TOKEN,
                                         data.getData())
                              .apply();

            Intent intent = new Intent(getBaseContext(), SplashActivity.class);
            startActivity(intent);
            finish();
        } else {
            mProgress.setVisibility(View.GONE);
            mInputSigninUsername.setEnabled(true);
            mInputSigninPassword.setEnabled(true);
            Toast.makeText(getBaseContext(), "Username atau password tidak sesuai",
                           Toast.LENGTH_SHORT).show();
            mInputSigninPassword.setText(null);
            mInputSigninPassword.clearFocus();
        }
    }

    private void signinFailure(Throwable t) {
        Timber.e(t, "onFailure: ");
        Toast.makeText(getBaseContext(), "Terjadi kesalahan saat mengakses server",
                       Toast.LENGTH_SHORT).show();
    }

}
