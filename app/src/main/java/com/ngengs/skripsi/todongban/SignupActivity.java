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
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ngengs.skripsi.todongban.data.enumerations.Values;
import com.ngengs.skripsi.todongban.data.local.Garage;
import com.ngengs.skripsi.todongban.data.local.User;
import com.ngengs.skripsi.todongban.data.remote.Signup;
import com.ngengs.skripsi.todongban.fragments.SignupBasicFragment;
import com.ngengs.skripsi.todongban.fragments.SignupGarageFragment;
import com.ngengs.skripsi.todongban.fragments.SignupTypeFragment;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements
        SignupBasicFragment.OnFragmentSignupBasicInteractionListener,
        SignupTypeFragment.OnFragmentTypeInteractionListener,
        SignupGarageFragment.OnFragmentInteractionListener {
    private static final String TAG = "SignupActivity";

    private Toolbar mToolbarSignup;
    private FrameLayout mFrameLayoutSignUp;
    private FragmentManager mFragmentManager;
    private MaterialDialog mDialog;
    private SharedPreferences mSharedPreferences;


    //    private User mUser;
//    private Garage mGarage;
    private API mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initLayout();
        setSupportActionBar(mToolbarSignup);
        mApi = NetworkHelpers.provideAPI(this);
        mSharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        mDialog = new MaterialDialog.Builder(this)
                .content("Memproses...")
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .build();
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }
//        goToPageBasic();
        goToPageGarage(null);
    }

    private void initLayout() {
        mToolbarSignup = findViewById(R.id.toolbarSignUp);
        mFrameLayoutSignUp = findViewById(R.id.frameLayoutSignUp);
    }

    public void setAppTitle(@StringRes int title) {
        Log.d(TAG, "setAppTitle() called with: title = [" + title + "]");
        if (getSupportActionBar() != null) {
            Log.d(TAG, "setAppTitle: changing");
            setTitle(title);
            getSupportActionBar().setTitle(title);
            mToolbarSignup.setTitle(title);
        }
    }

    @Override
    public void onButtonBasicNextClicked(User userData) {
        Log.d(TAG, "onButtonBasicNextClicked() called with: userData = [" + userData + "]");
        goToPageType(userData);
//        mUser = userData;
//        if (mUser.getType() != User.TYPE_PERSONAL && mUser.getType() != User.TYPE_GARAGE) {
//            goToPageType(userData);
//        } else {
//            if (mUser.getType() == User.TYPE_PERSONAL) submitNewAccountPersonal();
//            else if (mUser.getType() == User.TYPE_GARAGE) goToPageGarage();
//        }
    }

    private void goToPageBasic() {
        mFragmentManager.beginTransaction()
                        .add(mFrameLayoutSignUp.getId(), SignupBasicFragment.newInstance(null))
                        .commit();
    }

    private void goToPageType(User user) {
        mFragmentManager.beginTransaction()
                        .replace(mFrameLayoutSignUp.getId(), SignupTypeFragment.newInstance(user))
                        .addToBackStack("SignupTypeFragment")
                        .commit();
    }

    private void goToPageGarage(User user) {
        mFragmentManager.beginTransaction()
                        .replace(mFrameLayoutSignUp.getId(),
                                 SignupGarageFragment.newInstance(user))
                        .addToBackStack("SignupGarageFragment")
                        .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signin_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_signin) {
            Intent intent = new Intent(this, SigninActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onButtonTypeClicked(User userData) {
//        mUser.setType(userType);
        if (userData.getType() == User.TYPE_PERSONAL) submitNewAccountPersonal(userData);
        else if (userData.getType() == User.TYPE_GARAGE) goToPageGarage(userData);
    }

    private void submitNewAccountPersonal(User user) {
        mFrameLayoutSignUp.setVisibility(View.GONE);
        mDialog.show();
        List<MultipartBody.Part> partImages = new ArrayList<>();
        partImages.add(NetworkHelpers.prepareImagePart("avatar", user.getAvatarUri()));
        partImages.add(NetworkHelpers.prepareImagePart("identity_picture", user.getIdentityUri()));
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "submitNewAccountPersonal: Token:" + token);
        user.setDeviceId(token);
        mApi.signupPersonal(NetworkHelpers.prepareStringPart(user.getUsername()),
                            NetworkHelpers.prepareStringPart(user.getEmail()),
                            NetworkHelpers.prepareStringPart(user.getPasswordClean()),
                            NetworkHelpers.prepareStringPart(user.getFullName()),
                            NetworkHelpers.prepareStringPart(user.getPhone()),
                            NetworkHelpers.prepareStringPart(user.getGender()),
                            NetworkHelpers.prepareStringPart(user.getAddress()),
                            NetworkHelpers.prepareStringPart(user.getIdentityNumber()),
                            NetworkHelpers.prepareStringPart(user.getDeviceId()),
                            NetworkHelpers.prepareStringPart(user.getType()), partImages)
            .enqueue(new ApiResponse<>(this::signupPersonalSuccess, this::signupPersonalFailure));
    }

    private void submitNewAccountGarage() {
        mFrameLayoutSignUp.setVisibility(View.GONE);
        mDialog.show();
    }

    private void signupPersonalSuccess(Response<Signup> response) {
        Log.d(TAG, "signupPersonalSuccess() called with: response = [" + response + "]");

        Signup responseSignup = response.body();
        if (responseSignup != null) {
            mSharedPreferences.edit()
                              .putString(Values.SHARED_PREFERENCES_KEY_TOKEN,
                                         responseSignup.getData().getToken())
                              .apply();
            Intent intent = new Intent(getApplicationContext(), WaitVerificationActivity.class);
            startActivity(intent);
            finish();
        }
        mFrameLayoutSignUp.setVisibility(View.VISIBLE);
        mDialog.dismiss();
    }


    private void signupPersonalFailure(Throwable t) {
        Log.e(TAG, "signupPersonalFailure: ", t);

        Snackbar.make(mFrameLayoutSignUp, t.getMessage(), Snackbar.LENGTH_SHORT).show();
        while (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStack();
        }
    }

    @Override
    public void onButtonGarageSubmitClicked(Garage garage) {
        Log.d(TAG, "onButtonGarageSubmitClicked() called with: garage = [" + garage + "]");
//        mGarage = garage;
        submitNewAccountGarage();
    }
}
