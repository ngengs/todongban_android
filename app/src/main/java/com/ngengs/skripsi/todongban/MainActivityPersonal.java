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
import timber.log.Timber;

public class MainActivityPersonal extends AppCompatActivity
        implements PersonalRequestHelpFragment.OnFragmentInteractionListener,
        PersonalProcessHelpFragment.OnFragmentInteractionListener {
    public static final String ARGS_USER = "USER";
    public static final String ARGS_BROADCAST_FILTER
            = "com.ngengs.skripsi.todongban.UPDATE_HELPER_LIST";
    public static final String ARGS_BROADCAST_DATA = "PEOPLE_HELP";

    private User mUser;
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
        initView();

        if (getIntent().getParcelableExtra(ARGS_USER) != null) {
            mUser = getIntent().getParcelableExtra(ARGS_USER);
        }
        mApi = NetworkHelpers.provideAPI(this);
        mBroadcastReceiver = new FoundHelpersBroadcastReceiver();
        mIntentFilter = new IntentFilter(ARGS_BROADCAST_FILTER);

        mSharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        mHelpProcess = mSharedPreferences.getBoolean(Values.SHARED_PREFERENCES_KEY_IN_HELP_PROCESS,
                                                     false);
        if (mUser == null) {
            mApi.checkStatus()
                .enqueue(new ApiResponse<>(this::getUserSuccess, this::getUserFailure));
        } else {
            initFragment();
//            initPlayServices();
//            initMaps();
        }
    }

    private void initView() {
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_config_help:
                    HelpSettingActivity.runSetting(this);
                    break;
                case R.id.menu_signout:
                    SignoutActivity.runSignout(this);
                    break;
            }
            return true;
        });

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
        Timber.d("goToPageRequestHelp() called");
        mFragment = PersonalRequestHelpFragment.newInstance();
        changePage();
    }

    private void goToPageProcessHelp(@Nullable RequestHelp requestHelp) {
        Timber.d("goToPageProcessHelp() called with: requestHelp = [ %s ]", requestHelp);
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
        Timber.d("changePage() called");
        if (mFragment != null) {
            Timber.d("changePage: %s", "Fragment not null");
            Fragment fragment = getSupportFragmentManager().findFragmentById(
                    R.id.frame_personal_main);
            FragmentTransaction fragmentTransaction
                    = getSupportFragmentManager().beginTransaction();

            if (fragment == null) {
                Timber.d("changePage: %s", "Create new Fragment");
                fragmentTransaction.add(R.id.frame_personal_main, mFragment);
            } else {
                Timber.d("changePage: %s", "Replace exist Fragment");
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
        Timber.d("onHelpRequested() called with: requestHelp = [ %s ]", requestHelp);
        removeDrawerListener();
        mHelpProcess = true;
        registerReceiver(mBroadcastReceiver, mIntentFilter);

        mSharedPreferences.edit()
                          .putBoolean(Values.SHARED_PREFERENCES_KEY_IN_HELP_PROCESS, mHelpProcess)
                          .putString(Values.SHARED_PREFERENCES_KEY_TYPE_HELP_PROCESS,
                                     requestHelp.getSelectedHelpType())
                          .apply();
        goToPageProcessHelp(requestHelp);
        mApi.requestHelp(requestHelp.getLocationLatitude(), requestHelp.getLocationLongitude(),
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
        Timber.d("onProcessCancel() called with: requestId = [ %s ]", requestId);
        mHelpProcess = false;
        mHelpProcessId = null;
        unregisterReceiver(mBroadcastReceiver);
        mSharedPreferences.edit()
                          .putBoolean(Values.SHARED_PREFERENCES_KEY_IN_HELP_PROCESS, mHelpProcess)
                          .putString(Values.SHARED_PREFERENCES_KEY_ID_HELP_PROCESS, null)
                          .putString(Values.SHARED_PREFERENCES_KEY_TYPE_HELP_PROCESS, null)
                          .putString(Values.SHARED_PREFERENCES_KEY_PEOPLE_HELP, null)
                          .apply();
        mApi.requestHelpCancel(requestId)
            .enqueue(new ApiResponse<>(this::helpCancelSuccess, this::helpCancelFailure));
        goToPageRequestHelp();
    }

    private void getUserSuccess(Response<CheckStatus> response) {
        Timber.d("getUserSuccess() called with: response = [ %s ]", response);
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
        Timber.e(t, "getUserFailure: ");
    }

    private void helpRequestSuccess(Response<SingleStringData> response) {
        Timber.d("helpRequestSuccess() called with: response = [ %s ]", response);
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
        Timber.e(t, "helpRequestFailure: ");
    }

    private void helpCancelSuccess(Response<SingleStringData> response) {
        Timber.d("helpCancelSuccess() called with: response = [ %s ]", response);
        goToPageRequestHelp();
    }

    private void helpCancelFailure(Throwable t) {
        Timber.e(t, "helpCancelFailure: ");
    }

    private class FoundHelpersBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            PeopleHelp peopleHelp = intent.getParcelableExtra(ARGS_BROADCAST_DATA);
            Timber.d("onReceive: %s", peopleHelp);
            if (mFragment instanceof PersonalProcessHelpFragment) {
                ((PersonalProcessHelpFragment) mFragment).addData(peopleHelp);
            }
        }
    }
}
