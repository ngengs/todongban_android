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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.ngengs.skripsi.todongban.utils.networks.APIUnSecure;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import timber.log.Timber;

public class SignupActivity extends AppCompatActivity implements
        SignupBasicFragment.OnFragmentSignupBasicInteractionListener,
        SignupTypeFragment.OnFragmentTypeInteractionListener,
        SignupGarageFragment.OnFragmentInteractionListener {

    private Toolbar mToolbarSignup;
    private FrameLayout mFrameLayoutSignUp;
    private FragmentManager mFragmentManager;
    private MaterialDialog mDialog;
    private SharedPreferences mSharedPreferences;


    //    private User mUser;
//    private Garage mGarage;
    private APIUnSecure mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();
        setSupportActionBar(mToolbarSignup);
        mApi = NetworkHelpers.provideAPIUnSecure(this);
        mSharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        mDialog = new MaterialDialog.Builder(this)
                .content("Memproses...")
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .build();
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }
        goToPageBasic();
//        goToPageGarage(null);
    }

    private void initView() {
        mToolbarSignup = findViewById(R.id.toolbarSignUp);
        mFrameLayoutSignUp = findViewById(R.id.frameLayoutSignUp);
    }

    public void setAppTitle(@StringRes int title) {
        Timber.d("setAppTitle() called with: title = [ %s ]", title);
        if (getSupportActionBar() != null) {
            Timber.d("setAppTitle: %s", "changing");
            setTitle(title);
            getSupportActionBar().setTitle(title);
            mToolbarSignup.setTitle(title);
        }
    }

    @Override
    public void onButtonBasicNextClicked(User userData) {
        Timber.d("onButtonBasicNextClicked() called with: userData = [ %s ]", userData);
        goToPageType(userData);
//        mUser = userData;
//        if (mUser.getType() != User.TYPE_PERSONAL && mUser.getType() != User.TYPE_GARAGE) {
//            goToPageType(userData);
//        } else {
//            if (mUser.getType() == User.TYPE_PERSONAL) submitNewAccountPersonal();
//            else if (mUser.getType() == User.TYPE_GARAGE) goToPageGarage();
//        }
    }

    @SuppressWarnings("unused")
    private void goToPageBasic() {
        Timber.d("goToPageBasic() called");
        mFragmentManager.beginTransaction()
                        .add(mFrameLayoutSignUp.getId(), SignupBasicFragment.newInstance(null))
                        .commit();
    }

    private void goToPageType(User user) {
        Timber.d("goToPageType() called with: user = [ %s ]", user);
        mFragmentManager.beginTransaction()
                        .replace(mFrameLayoutSignUp.getId(), SignupTypeFragment.newInstance(user))
                        .addToBackStack("SignupTypeFragment")
                        .commit();
    }

    private void goToPageGarage(User user) {
        Timber.d("goToPageGarage() called with: user = [ %s ]", user);
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
        if (mDialog.isShowing()) {
            return;
        }
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onButtonTypeClicked(User userData) {
        Timber.d("onButtonTypeClicked() called with: userData = [ %s ]", userData);
        if (userData.getType() == User.TYPE_PERSONAL) submitNewAccountPersonal(userData);
        else if (userData.getType() == User.TYPE_GARAGE) goToPageGarage(userData);
    }

    private void submitNewAccountPersonal(User user) {
        Timber.d("submitNewAccountPersonal() called with: user = [ %s ]", user);
        mFrameLayoutSignUp.setVisibility(View.GONE);
        mDialog.show();
        user = prepareUserToken(user);
        List<MultipartBody.Part> partImages = new ArrayList<>(
                prepareSignupImage(user.getAvatarUri(), user.getIdentityUri()));
//        mApi.signupPersonal(NetworkHelpers.prepareStringPart(user.getUsername()),
//                            NetworkHelpers.prepareStringPart(user.getEmail()),
//                            NetworkHelpers.prepareStringPart(user.getPasswordClean()),
//                            NetworkHelpers.prepareStringPart(user.getFullName()),
//                            NetworkHelpers.prepareStringPart(user.getPhone()),
//                            NetworkHelpers.prepareStringPart(user.getGender()),
//                            NetworkHelpers.prepareStringPart(user.getAddress()),
//                            NetworkHelpers.prepareStringPart(user.getIdentityNumber()),
//                            NetworkHelpers.prepareStringPart(user.getDeviceId()),
//                            NetworkHelpers.prepareStringPart(user.getType()), partImages)
        Map<String, RequestBody> signupData = new HashMap<>(
                NetworkHelpers.prepareMapPart(user));
        mApi.signup(signupData, partImages)
            .enqueue(new ApiResponse<>(this::signupSuccess, this::signupFailure));
    }

    private void submitNewAccountGarage(Garage garage) {
        Timber.d("submitNewAccountGarage() called with: garage = [ %s ]", garage);
        mFrameLayoutSignUp.setVisibility(View.GONE);
        mDialog.show();
        User user = prepareUserToken(garage.getUser());
        List<MultipartBody.Part> partImages = new ArrayList<>(
                prepareSignupImage(user.getAvatarUri(), user.getIdentityUri()));
//        mApi.signupGarage(NetworkHelpers.prepareStringPart(user.getUsername()),
//                          NetworkHelpers.prepareStringPart(user.getEmail()),
//                          NetworkHelpers.prepareStringPart(user.getPasswordClean()),
//                          NetworkHelpers.prepareStringPart(user.getFullName()),
//                          NetworkHelpers.prepareStringPart(user.getPhone()),
//                          NetworkHelpers.prepareStringPart(user.getGender()),
//                          NetworkHelpers.prepareStringPart(user.getAddress()),
//                          NetworkHelpers.prepareStringPart(user.getIdentityNumber()),
//                          NetworkHelpers.prepareStringPart(user.getDeviceId()),
//                          NetworkHelpers.prepareStringPart(user.getType()),
//                          NetworkHelpers.prepareStringPart(garage.getName()),
//                          NetworkHelpers.prepareStringPart(garage.getOpenHour()),
//                          NetworkHelpers.prepareStringPart(garage.getCloseHour()),
//                          NetworkHelpers.prepareStringPart(garage.getAddress()),
//                          NetworkHelpers.prepareStringPart(garage.getLatitude()),
//                          NetworkHelpers.prepareStringPart(garage.getLongitude()),
//                          partImages
//        )
        garage.setUser(null);
        Map<String, RequestBody> signupData = new HashMap<>(NetworkHelpers.prepareMapPart(garage));
        signupData.putAll(NetworkHelpers.prepareMapPart(user));
        mApi.signup(signupData, partImages)
            .enqueue(new ApiResponse<>(this::signupSuccess, this::signupFailure));
    }

    private User prepareUserToken(User user) {
        Timber.d("prepareUserToken() called with: user = [ %s ]", user);
        String token = FirebaseInstanceId.getInstance().getToken();
        Timber.d("prepareUserToken: Token: %s", token);
        user.setDeviceId(token);
        return user;
    }

    private List<MultipartBody.Part> prepareSignupImage(Uri avatarUri, Uri identityUri) {
        List<MultipartBody.Part> partImages = new ArrayList<>();
        partImages.add(NetworkHelpers.prepareImagePart("avatar", avatarUri));
        partImages.add(NetworkHelpers.prepareImagePart("identity_picture", identityUri));
        return partImages;
    }

    private void signupSuccess(Response<Signup> response) {
        Timber.d("signupSuccess() called with: response = [ %s ]", response);

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


    private void signupFailure(Throwable t) {
        Timber.e(t, "signupFailure: ");

        Snackbar.make(mFrameLayoutSignUp, t.getMessage(), Snackbar.LENGTH_SHORT).show();
        while (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStack();
        }
    }

    @Override
    public void onButtonGarageSubmitClicked(Garage garage) {
        Timber.d("onButtonGarageSubmitClicked() called with: garage = [ %s ]", garage);
//        Gson gson = new Gson();
//        String json = gson.toJson(garage);
//        Timber.d("onButtonGarageSubmitClicked: JSON: %s", json);
//        LinkedHashTreeMap map = gson.fromJson(json, LinkedHashTreeMap.class);
//        Timber.d("onButtonGarageSubmitClicked: MAP: %s", map);
//        Map<String, RequestBody> mapBody = NetworkHelpers.prepareMapPart(garage);
//        Timber.d("onButtonGarageSubmitClicked: MAP PART: %s", mapBody);
//        mGarage = garage;
        submitNewAccountGarage(garage);
    }
}
