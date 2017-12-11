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

package com.ngengs.skripsi.todongban.notification;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ngengs.skripsi.todongban.data.enumerations.Values;
import com.ngengs.skripsi.todongban.data.remote.SingleStringData;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;

import retrofit2.Response;
import timber.log.Timber;

public class NotificationInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "NotificationInstanceIDS";
    private SharedPreferences mSharedPreferences;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Timber.tag(TAG).d("onTokenRefresh: %s", refreshedToken);

        mSharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String savedToken = mSharedPreferences.getString(Values.SHARED_PREFERENCES_KEY_TOKEN, null);
        if (!TextUtils.isEmpty(savedToken)) {
            API mApi = NetworkHelpers.provideAPI(this, savedToken);
            mApi.updateDeviceId(refreshedToken)
                .enqueue(new ApiResponse<>(this::updateSuccess, this::updateFailure));
        }
    }

    public void updateSuccess(Response<SingleStringData> response) {
        Timber.tag(TAG).d("updateSuccess() called with: response = [ %s ]", response);
        SingleStringData data = response.body();
        if (data != null) {
            mSharedPreferences.edit()
                              .putString(Values.SHARED_PREFERENCES_KEY_TOKEN, data.getData())
                              .apply();
        }
    }

    public void updateFailure(@NonNull Throwable t) {
        Timber.tag(TAG).e(t, "onFailure: ");
        mSharedPreferences.edit()
                          .putString(Values.SHARED_PREFERENCES_KEY_TOKEN, null)
                          .apply();
    }
}
