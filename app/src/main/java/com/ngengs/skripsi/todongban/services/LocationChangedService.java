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

package com.ngengs.skripsi.todongban.services;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.ngengs.skripsi.todongban.data.local.User;
import com.ngengs.skripsi.todongban.data.remote.SingleStringData;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;

import retrofit2.Response;
import timber.log.Timber;

public class LocationChangedService extends JobService
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public final static String TAG = "LocationChangedService";

    public final static String PARAM_TOKEN = "token";
    public final static String PARAM_TYPE = "type";


    private JobParameters givenParams;
    private GoogleApiClient mGoogleApiClient;
    private String mToken;


    @Override
    public boolean onStartJob(JobParameters job) {
        Timber.tag(TAG).d("onStartJob() called with: job = [ %s ]", job);
        givenParams = job;
        if (givenParams.getExtras() != null) {
            mToken = givenParams.getExtras().getString(PARAM_TOKEN, null);
            int userType = givenParams.getExtras().getInt(PARAM_TYPE, -1);
            if (!TextUtils.isEmpty(mToken) && userType == User.TYPE_PERSONAL) {
                Timber.tag(TAG).d("onStartJob: running: token: %s, type: %s", mToken, userType);
//                jobFinished(givenParams, false);
                mGoogleApiClient = new GoogleApiClient
                        .Builder(getBaseContext(), this, this)
                        .addApi(LocationServices.API)
                        .build();
                mGoogleApiClient.connect();
            }
        }
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters job) {
        Timber.tag(TAG).d("onStopJob() called with: job = [ %s ]", job);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        return true;
    }

    private void pushUpdate(double latitude, double longitude) {
        Timber.tag(TAG)
              .d("pushUpdate() called with: latitude = [ %s ], longitude = [ %s ]", latitude,
                 longitude);
        API mApi = NetworkHelpers.provideAPI(getBaseContext());
        mApi.updateLocation(NetworkHelpers.authorizationHeader(mToken), latitude, longitude)
            .enqueue(new ApiResponse<>(this::updateSuccess, this::updateFailure));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Timber.tag(TAG).d("onConnected() called with: bundle = [ %s ]", bundle);
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.tag(TAG).d("onConnectionSuspended() called with: i = [ %s ]", i);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.tag(TAG)
              .d("onConnectionFailed() called with: connectionResult = [ %s ]", connectionResult);
        jobFinished(givenParams, false);
    }


    @SuppressLint("MissingPermission")
    private void getLocation() {
        Timber.tag(TAG).d("getLocation() called");
        Task<Location> resultLocation = LocationServices
                .getFusedLocationProviderClient(getBaseContext())
                .getLastLocation();
        resultLocation.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                pushUpdate(task.getResult().getLatitude(), task.getResult().getLongitude());
            } else {
                Timber.tag(TAG).e("getLocation: Gagal mendapatkan lokasi");
                jobFinished(givenParams, false);
            }
        });
    }

    public void updateSuccess(Response<SingleStringData> response) {
        Timber.tag(TAG).d("updateSuccess() called with: response = [ %s ]", response);
        jobFinished(givenParams, false);
    }

    public void updateFailure(Throwable t) {
        Timber.tag(TAG).e(t, "updateFailure: ");
        jobFinished(givenParams, false);
    }
}
