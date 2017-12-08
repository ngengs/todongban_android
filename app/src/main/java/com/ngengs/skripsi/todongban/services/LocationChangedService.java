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
import android.util.Log;

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
        Log.d(TAG, "onStartJob() called with: job = [" + job + "]");
        givenParams = job;
        if (givenParams.getExtras() != null) {
            mToken = givenParams.getExtras().getString(PARAM_TOKEN, null);
            int userType = givenParams.getExtras().getInt(PARAM_TYPE, -1);
            if (!TextUtils.isEmpty(mToken) && userType == User.TYPE_PERSONAL) {
                Log.d(TAG, "onStartJob: running: token: " + mToken + ", type: " + userType);
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
        Log.d(TAG, "onStopJob() called with: job = [" + job + "]");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        return true;
    }

    private void pushUpdate(double latitude, double longitude) {
        Log.d(TAG, "pushUpdate() called with: token = [" + mToken + "], latitude = [" + latitude +
                   "], longitude = [" + longitude + "]");
        API mApi = NetworkHelpers.provideAPI(getBaseContext());
        mApi.updateLocation(NetworkHelpers.authorizationHeader(mToken), latitude, longitude)
            .enqueue(new ApiResponse<>(this::updateSuccess, this::updateFailure));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected() called with: bundle = [" + bundle + "]");
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended() called with: i = [" + i + "]");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,
              "onConnectionFailed() called with: connectionResult = [" + connectionResult + "]");
        jobFinished(givenParams, false);
    }


    @SuppressLint("MissingPermission")
    private void getLocation() {
        Log.d(TAG, "getLocation() called");
        Task<Location> resultLocation = LocationServices
                .getFusedLocationProviderClient(getBaseContext())
                .getLastLocation();
        resultLocation.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                pushUpdate(task.getResult().getLatitude(), task.getResult().getLongitude());
            } else {
                Log.e(TAG, "getLocation: kesalahan saat mendapatkan lokasi");
                jobFinished(givenParams, false);
            }
        });
    }

    public void updateSuccess(Response<SingleStringData> response) {
        Log.d(TAG, "updateSuccess() called with: response = [" + response + "]");
        jobFinished(givenParams, false);
    }

    public void updateFailure(Throwable t) {
        Log.e(TAG, "updateFailure: ", t);
        jobFinished(givenParams, false);
    }
}
