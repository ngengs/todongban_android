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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.ngengs.skripsi.todongban.data.enumerations.Values;
import com.ngengs.skripsi.todongban.data.local.PeopleHelp;
import com.ngengs.skripsi.todongban.data.local.RequestHelp;
import com.ngengs.skripsi.todongban.data.local.User;
import com.ngengs.skripsi.todongban.data.remote.CheckStatus;
import com.ngengs.skripsi.todongban.data.remote.SingleStringData;
import com.ngengs.skripsi.todongban.fragments.PersonalProcessHelpFragment;
import com.ngengs.skripsi.todongban.fragments.PersonalRequestHelpFragment;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;

import retrofit2.Response;

public class MainActivityPersonal extends AppCompatActivity
        implements PersonalRequestHelpFragment.OnFragmentInteractionListener,
        PersonalProcessHelpFragment.OnFragmentInteractionListener {
    public static final String ARGS_USER = "USER";
    public static final String ARGS_BROADCAST_FILTER
            = "com.ngengs.skripsi.todongban.UPDATE_HELPER_LIST";
    public static final String ARGS_BROADCAST_DATA = "PEOPLE_HELP";
    private static final String TAG = "MainActivityPersonal";
    private User mUser;
    private String mToken;
    private API mApi;
    private Fragment mFragment;
    private DrawerLayout mDrawer;
    @SuppressWarnings("FieldCanBeLocal")
    private NavigationView mNavigationView;
    private boolean mHelpProcess;
    private String mHelpProcessId;
    private DrawerLayout.DrawerListener mDrawerListener;
    private SharedPreferences mSharedPreferences;
    private FoundHelpersBroadcastReceiver mBroadcastReceiver;
    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_personal);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_signout:
                    mSharedPreferences.edit()
                                      .putString(Values.SHARED_PREFERENCES_KEY_TOKEN, null)
                                      .apply();
                    mApi.signout(NetworkHelpers.authorizationHeader(mToken))
                        .enqueue(new ApiResponse<>(this::signoutSuccess, this::signoutFailure));
                    break;
            }
            return true;
        });


        if (getIntent().getParcelableExtra(ARGS_USER) != null) {
            mUser = getIntent().getParcelableExtra(ARGS_USER);
        }
        mApi = NetworkHelpers.provideAPI(this);
        mBroadcastReceiver = new FoundHelpersBroadcastReceiver();
        mIntentFilter = new IntentFilter(ARGS_BROADCAST_FILTER);

        mSharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        mToken = mSharedPreferences.getString(Values.SHARED_PREFERENCES_KEY_TOKEN, null);
        mHelpProcess = mSharedPreferences.getBoolean(Values.SHARED_PREFERENCES_KEY_IN_HELP_PROCESS,
                                                     false);
        if (mToken == null) {
            Log.e(TAG, "onCreate: Empty token data");
            throw new RuntimeException("Empty token data");
        }
        if (mUser == null) {
            mApi.checkStatus(NetworkHelpers.authorizationHeader(mToken))
                .enqueue(new ApiResponse<>(this::getUserSuccess, this::getUserFailure));
        } else {
            initFragment();
//            initPlayServices();
//            initMaps();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelpProcess) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    private void initFragment() {
        if (!mHelpProcess) {
            goToPageRequestHelp();
        } else {
            goToPageProcessHelp(null);
        }
    }

    private void goToPageRequestHelp() {
        Log.d(TAG, "goToPageRequestHelp() called");
        mFragment = PersonalRequestHelpFragment.newInstance();
        changePage();
    }

    private void goToPageProcessHelp(@Nullable RequestHelp requestHelp) {
        Log.d(TAG, "goToPageProcessHelp() called with: requestHelp = [" + requestHelp + "]");
        if (requestHelp == null) {
            String helpType = mSharedPreferences.getString(
                    Values.SHARED_PREFERENCES_KEY_TYPE_HELP_PROCESS, null);
            requestHelp = new RequestHelp(helpType);
            mHelpProcessId = mSharedPreferences.getString(
                    Values.SHARED_PREFERENCES_KEY_ID_HELP_PROCESS, null);
        }
        mFragment = PersonalProcessHelpFragment.newInstance(requestHelp, mHelpProcessId);
        registerReceiver(mBroadcastReceiver, mIntentFilter);
        changePage();
    }

    private void changePage() {
        Log.d(TAG, "changePage() called");
        if (mFragment != null) {
            Log.d(TAG, "changePage: Fragment not null");
            Fragment fragment = getSupportFragmentManager().findFragmentById(
                    R.id.frame_personal_main);
            FragmentTransaction fragmentTransaction
                    = getSupportFragmentManager().beginTransaction();

            if (fragment == null) {
                Log.d(TAG, "changePage: create new fragment");
                fragmentTransaction.add(R.id.frame_personal_main, mFragment);
            } else {
                Log.d(TAG, "changePage: replace fragment");
                fragmentTransaction.replace(R.id.frame_personal_main, mFragment);
            }
            fragmentTransaction.commit();
        }
    }

    private void setDrawerListener(DrawerLayout.DrawerListener drawerListener) {
        this.mDrawerListener = drawerListener;
        mDrawer.addDrawerListener(mDrawerListener);
    }

    private void removeDrawerListener() {
        mDrawer.removeDrawerListener(mDrawerListener);
        mDrawerListener = null;
    }

    @Override
    public void onBackPressed() {
        if (mFragment != null) {
            if (mFragment instanceof PersonalRequestHelpFragment) {
                PersonalRequestHelpFragment castedFragment
                        = (PersonalRequestHelpFragment) mFragment;
                if (castedFragment.isEditLocation()) {
                    castedFragment.cancelEditLocation();
                    return;
                } else if (castedFragment.isBottomSheetNotCollapsed()) {
                    castedFragment.setBottomSheetCollapsed();
                    return;
                }
            }
        }
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
            return;
        }

        super.onBackPressed();
    }


    @Override
    public void onHelpRequested(RequestHelp requestHelp) {
        Log.d(TAG, "onHelpRequested() called with: requestHelp = [" + requestHelp + "]");
        removeDrawerListener();
        mHelpProcess = true;
        registerReceiver(mBroadcastReceiver, mIntentFilter);

        mSharedPreferences.edit()
                          .putBoolean(Values.SHARED_PREFERENCES_KEY_IN_HELP_PROCESS, mHelpProcess)
                          .putString(Values.SHARED_PREFERENCES_KEY_TYPE_HELP_PROCESS,
                                     requestHelp.getSelectedHelpType())
                          .apply();
        goToPageProcessHelp(requestHelp);
        mApi.requestHelp(NetworkHelpers.authorizationHeader(mToken),
                         requestHelp.getLocationLatitude(), requestHelp.getLocationLongitude(),
                         requestHelp.getSelectedHelpType(), requestHelp.getMessage(),
                         requestHelp.getLocationName())
            .enqueue(new ApiResponse<>(this::helpRequestSuccess, this::helpRequestFailure));
    }

    @Override
    public DrawerLayout prepareDrawerLayoutForHelpRequest() {
        return mDrawer;
    }

    @Override
    public void syncDrawerLayoutForHelpRequest(ActionBarDrawerToggle toggle) {
        setDrawerListener(toggle);
    }

    @Override
    public void onProcessCancel(String requestId) {
        Log.d(TAG, "onProcessCancel() called with: requestId = [" + requestId + "]");
        mHelpProcess = false;
        mHelpProcessId = null;
        unregisterReceiver(mBroadcastReceiver);
        mSharedPreferences.edit()
                          .putBoolean(Values.SHARED_PREFERENCES_KEY_IN_HELP_PROCESS, mHelpProcess)
                          .putString(Values.SHARED_PREFERENCES_KEY_ID_HELP_PROCESS, null)
                          .putString(Values.SHARED_PREFERENCES_KEY_TYPE_HELP_PROCESS, null)
                          .putString(Values.SHARED_PREFERENCES_KEY_PEOPLE_HELP, null)
                          .apply();
        mApi.requestHelpCancel(NetworkHelpers.authorizationHeader(mToken), requestId)
            .enqueue(new ApiResponse<>(this::helpCancelSuccess, this::helpCancelFailure));
        goToPageRequestHelp();
    }

    private void getUserSuccess(Response<CheckStatus> response) {
        Log.d(TAG, "getUserSuccess() called with: response = [" + response + "]");
        CheckStatus responseBody = response.body();
        if (responseBody != null) {
            mUser = responseBody.getData();
            initFragment();
//            initMaps();
        } else {
            getUserFailure(new Throwable("Empty Response Data"));
        }
    }

    private void getUserFailure(Throwable t) {
        Log.e(TAG, "getUserFailure: ", t);
    }

    private void helpRequestSuccess(Response<SingleStringData> response) {
        Log.d(TAG, "helpRequestSuccess() called with: response = [" + response + "]");
        SingleStringData body = response.body();
        if (body != null) {
            if (mFragment instanceof PersonalProcessHelpFragment) {
                mHelpProcessId = body.getData();

                mSharedPreferences.edit()
                                  .putString(Values.SHARED_PREFERENCES_KEY_ID_HELP_PROCESS,
                                             mHelpProcessId)
                                  .apply();
                ((PersonalProcessHelpFragment) mFragment).setHelpRequestId(mHelpProcessId);
            }
        }
    }

    private void helpRequestFailure(Throwable t) {
        Log.e(TAG, "helpRequestFailure: ", t);
    }

    private void helpCancelSuccess(Response<SingleStringData> response) {
        Log.d(TAG, "helpCancelSuccess() called with: response = [" + response + "]");
        goToPageRequestHelp();
    }

    private void helpCancelFailure(Throwable t) {
        Log.e(TAG, "helpCancelFailure: ", t);
    }

    private void signoutSuccess(Response<SingleStringData> response) {
        Log.d(TAG, "signoutSuccess() called with: response = [" + response + "]");
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(
                new GooglePlayDriver(getBaseContext()));
        dispatcher.cancelAll();
        Intent intent = new Intent(getApplicationContext(),
                                   SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private void signoutFailure(Throwable t) {
        Log.e(TAG, "signoutFailure: ", t);
    }

    private class FoundHelpersBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            PeopleHelp peopleHelp = intent.getParcelableExtra(ARGS_BROADCAST_DATA);
            Log.d(TAG, "onReceive: " + peopleHelp);
            if (mFragment instanceof PersonalProcessHelpFragment) {
                ((PersonalProcessHelpFragment) mFragment).addData(peopleHelp);
            }
        }
    }
}
