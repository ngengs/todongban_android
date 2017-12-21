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

package com.ngengs.skripsi.todongban.utils.networks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ngengs.skripsi.todongban.data.remote.BaseData;
import com.ngengs.skripsi.todongban.utils.networks.interfaces.ErrorResponse;
import com.ngengs.skripsi.todongban.utils.networks.interfaces.SuccessResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ApiResponse<T> implements Callback<T> {
    private SuccessResponse<T> successResponse;
    private ErrorResponse errorResponse;

    public ApiResponse() {
    }

    public ApiResponse(@NonNull SuccessResponse<T> successResponse,
                       @Nullable ErrorResponse errorResponse) {
        this.successResponse = successResponse;
        if (errorResponse != null) {
            this.errorResponse = errorResponse;
        }
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        Timber.d("onResponse() called with: call = [ %s ], response = [ %s ]", call, response);
        if (successResponse != null) {
            T responseBody = response.body();
            if (responseBody == null) {
                onFailure(call, new Exception("Something wrong"));
                return;
            }
            if (responseBody instanceof BaseData) {
                BaseData responseData = (BaseData) responseBody;
                if (responseData.getStatusCode() == 200) {
                    successResponse.onResponse(response);
                } else {
                    onFailure(call, new Throwable(
                            "Status code: " + responseData.getStatusCode() + ", Message: " +
                            responseData.getStatusMessage()));
                }
            } else {
                successResponse.onResponse(response);
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        Timber.e(t, "onFailure: call = [ %s ]", call);
        if (errorResponse != null) {
            errorResponse.onResponse(t);
        }
    }
}
