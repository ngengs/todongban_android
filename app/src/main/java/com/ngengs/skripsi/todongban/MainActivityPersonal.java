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
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ngengs.skripsi.todongban.data.enumerations.Values;
import com.ngengs.skripsi.todongban.data.local.PeopleHelp;
import com.ngengs.skripsi.todongban.data.local.RequestHelp;
import com.ngengs.skripsi.todongban.data.local.User;
import com.ngengs.skripsi.todongban.data.remote.CheckStatus;
import com.ngengs.skripsi.todongban.data.remote.SingleStringData;
import com.ngengs.skripsi.todongban.fragments.PersonalProcessHelpFragment;
import com.ngengs.skripsi.todongban.fragments.PersonalRequestHelpFragment;
import com.ngengs.skripsi.todongban.fragments.PersonalResponseHelpFragment;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivityPersonal extends AppCompatActivity
        implements PersonalRequestHelpFragment.OnFragmentInteractionListener,
        PersonalProcessHelpFragment.OnFragmentInteractionListener,
        PersonalResponseHelpFragment.OnFragmentInteractionListener {
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
    private boolean mHelpResponse;
    private String mHelpProcessId;
    private DrawerLayout.DrawerListener mDrawerListener;
    private SharedPreferences mSharedPreferences;
    private FoundHelpersBroadcastReceiver mBroadcastReceiver;
    private IntentFilter mIntentFilter;
    private MaterialDialog mDialog;
    private CircleImageView mNavHeaderAvatar;
    private TextView mNavHeaderName;
    private TextView mNavHeaderType;

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
        mHelpResponse = mSharedPreferences.getBoolean(
                Values.SHARED_PREFERENCES_KEY_SELECTED_RESPONE, false);
        mDialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .title("Memproses")
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .build();
        if (mUser == null) {
            mDialog.show();
            mApi.checkStatus()
                .enqueue(new ApiResponse<>(this::getUserSuccess, this::getUserFailure));
        } else {
            initNavHeader();
            initFragment();
//            initPlayServices();
//            initMaps();
        }
    }

    private void initView() {
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mNavHeaderAvatar = mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_avatar);
        mNavHeaderName = mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        mNavHeaderType = mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_type);
        mNavigationView.setNavigationItemSelectedListener(this::handleNavigation);
        mNavigationView.setCheckedItem(R.id.menu_home);
    }

    private boolean handleNavigation(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                break;
            case R.id.menu_badge:
                BadgeActivity.runBadge(this);
                break;
            case R.id.menu_history_request_help:
                HistoryActivity.runHistoryRequest(this);
                break;
            case R.id.menu_history_response_help:
                HistoryActivity.runHistoryResponse(this);
                break;
            case R.id.menu_config_help:
                HelpSettingActivity.runSetting(this);
                break;
            case R.id.menu_config_profile:
                EditProfileActivity.runSetting(this);
                break;
            case R.id.menu_signout:
                SignoutActivity.runSignout(this);
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initNavHeader() {
        mNavHeaderName.setText(mUser.getFullName());
        mNavHeaderType.setText((mUser.getType() == User.TYPE_PERSONAL) ? "Personal" : "Bengkel");
        Picasso.with(this)
               .load(mUser.getAvatar())
               .resize(50, 50)
               .centerInside()
               .into(mNavHeaderAvatar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelpProcess) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    private void initFragment() {
        if (!mHelpProcess && !mHelpResponse) {
            goToPageRequestHelp();
        } else if (mHelpProcess) {
            goToPageProcessHelp(null);
        } else {
            goToPageDetailResponseHelp(null);
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

    private void goToPageDetailResponseHelp(@Nullable String helpProcessId) {
        Timber.d("goToPageDetailResponseHelp() called");
        if (helpProcessId == null) {
            mHelpProcessId = mSharedPreferences.getString(
                    Values.SHARED_PREFERENCES_KEY_ID_HELP_PROCESS, null);
        } else {
            mHelpProcessId = helpProcessId;
        }
        mFragment = PersonalResponseHelpFragment.newInstance(mHelpProcessId);
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
    public void pushUpdateLocation(double latitude, double longitude) {
        Timber.d("pushUpdateLocation() called with: latitude = [ %s ], longitude = [ %s ]",
                 latitude, longitude);
        mApi.updateLocation(latitude, longitude)
            .enqueue(new ApiResponse<>());
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
                          .remove(Values.SHARED_PREFERENCES_KEY_PEOPLE_HELP)
                          .apply();
        mApi.requestHelpCancel(requestId)
            .enqueue(new ApiResponse<>(this::helpCancelSuccess, this::helpCancelFailure));
        goToPageRequestHelp();
    }

    @Override
    public void onSelectedHelper(String responseId) {
        Timber.d("onSelectedHelper() called with: responseId = [ %s ]", responseId);
        mDialog.show();
        mApi.responseSelect(responseId)
            .enqueue(new ApiResponse<>(this::responseSelectSuccess, this::responseSelectFailed));
    }

    private void responseSelectSuccess(Response<SingleStringData> response) {
        Timber.d("responseSelectSuccess() called with: response = [ %s ]", response);
        mDialog.dismiss();
        mHelpProcess = false;
        mHelpResponse = true;
        mSharedPreferences.edit()
                          .remove(Values.SHARED_PREFERENCES_KEY_PEOPLE_HELP)
                          .putBoolean(Values.SHARED_PREFERENCES_KEY_IN_HELP_PROCESS, mHelpProcess)
                          .putBoolean(Values.SHARED_PREFERENCES_KEY_SELECTED_RESPONE, mHelpResponse)
                          .apply();
        goToPageDetailResponseHelp(mHelpProcessId);
    }

    private void responseSelectFailed(Throwable t) {
        Timber.e(t, "responseSelectFailed: ");
        mDialog.dismiss();
        Toast.makeText(this, "Terjadi Kesalahan, silahkan ulangi", Toast.LENGTH_SHORT).show();
    }

    private void getUserSuccess(Response<CheckStatus> response) {
        Timber.d("getUserSuccess() called with: response = [ %s ]", response);
        mDialog.dismiss();
        CheckStatus responseBody = response.body();
        if (responseBody != null) {
            mUser = responseBody.getData();
            initNavHeader();
            initFragment();
        } else {
            getUserFailure(new Throwable("Empty Response Data"));
        }
    }

    private void getUserFailure(Throwable t) {
        Timber.e(t, "getUserFailure: ");
        mDialog.dismiss();
        Toast.makeText(this, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
        mHelpProcess = false;
        mHelpProcessId = null;
        unregisterReceiver(mBroadcastReceiver);
        mSharedPreferences.edit()
                          .putBoolean(Values.SHARED_PREFERENCES_KEY_IN_HELP_PROCESS, mHelpProcess)
                          .putString(Values.SHARED_PREFERENCES_KEY_ID_HELP_PROCESS, null)
                          .putString(Values.SHARED_PREFERENCES_KEY_TYPE_HELP_PROCESS, null)
                          .remove(Values.SHARED_PREFERENCES_KEY_PEOPLE_HELP)
                          .apply();
        goToPageRequestHelp();
    }

    private void helpCancelSuccess(Response<SingleStringData> response) {
        Timber.d("helpCancelSuccess() called with: response = [ %s ]", response);
        goToPageRequestHelp();
    }

    private void helpCancelFailure(Throwable t) {
        Timber.e(t, "helpCancelFailure: ");
        Toast.makeText(this, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinishHelpProcess(String requestId, int rating) {
        Timber.d("onFinishHelpProcess() called with: requestId = [ %s ], rating = [ %s ]",
                 requestId, rating);
        mHelpProcessId = requestId;
        mDialog.show();
        mApi.finishHelp(mHelpProcessId, rating)
            .enqueue(new ApiResponse<>(this::finishHelpSuccess, this::finishHelpFailure));
    }

    private void finishHelpSuccess(Response<SingleStringData> response) {
        Timber.d("finishHelpSuccess() called with: response = [ %s ]", response);
        Toast.makeText(this, "Pencarian bantuan telah selesai", Toast.LENGTH_SHORT).show();
        mHelpProcessId = null;
        mHelpProcess = false;
        mHelpResponse = false;
        mSharedPreferences.edit()
                          .putString(Values.SHARED_PREFERENCES_KEY_ID_HELP_PROCESS, mHelpProcessId)
                          .putBoolean(Values.SHARED_PREFERENCES_KEY_IN_HELP_PROCESS, mHelpProcess)
                          .putBoolean(Values.SHARED_PREFERENCES_KEY_SELECTED_RESPONE, mHelpResponse)
                          .apply();
        mDialog.dismiss();
        initFragment();
    }

    private void finishHelpFailure(Throwable t) {
        Timber.e(t, "finishHelpFailure: ");
        Toast.makeText(this, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
        mDialog.dismiss();
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
