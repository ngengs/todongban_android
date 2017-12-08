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

import com.ngengs.skripsi.todongban.utils.networks.interfaces.ErrorResponse;
import com.ngengs.skripsi.todongban.utils.networks.interfaces.LambdaCall;
import com.ngengs.skripsi.todongban.utils.networks.interfaces.SuccessResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public final class NgCallAdapterFactory<T> extends CallAdapter.Factory {

    private NgCallAdapterFactory() {

    }

    static NgCallAdapterFactory create() {
        return new NgCallAdapterFactory();
    }

    @Nullable
    @Override
    public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Annotation[] annotations,
                                 @NonNull Retrofit retrofit) {
        if (getRawType(returnType) != LambdaCall.class) {
            return null;
        }

        if (!(returnType instanceof ParameterizedType)) {
            //noinspection SpellCheckingInspection
            throw new IllegalArgumentException(
                    "Call return type must be parameterized as Call<Foo> or Call<? extends Foo>");
        }
        final Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
        return new CallAdapter<T, LambdaCall<T>>() {
            @Override
            public Type responseType() {
                return responseType;
            }

            @Override
            public LambdaCall<T> adapt(@NonNull Call<T> call) {
                return new LambdaCall<T>() {
                    @Override
                    public void enqueue() {
                        call.enqueue(new ApiResponse<>());
                    }

                    @Override
                    public void enqueue(@NonNull SuccessResponse<T> successResponse) {
                        call.enqueue(new ApiResponse<>(successResponse, null));
                    }

                    @Override
                    public void enqueue(@NonNull SuccessResponse<T> successResponse,
                                        ErrorResponse errorResponse) {
                        call.enqueue(new ApiResponse<>(successResponse, errorResponse));
                    }

                    @Override
                    public Response<T> execute() throws IOException {
                        return call.execute();
                    }

                    @Override
                    public void enqueue(@NonNull Callback<T> callback) {
                        call.enqueue(callback);
                    }

                    @Override
                    public boolean isExecuted() {
                        return call.isExecuted();
                    }

                    @Override
                    public void cancel() {
                        call.cancel();
                    }

                    @Override
                    public boolean isCanceled() {
                        return call.isCanceled();
                    }

                    @Override
                    public Request request() {
                        return call.request();
                    }

                    @Override
                    public Call<T> clone() {
                        return call.clone();
                    }
                };
            }
        };
    }

}
