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

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ngengs.skripsi.todongban.BuildConfig;
import com.ngengs.skripsi.todongban.utils.GsonUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@SuppressWarnings("WeakerAccess")
public class NetworkHelpers {
    private static final String BASE_URL = BuildConfig.SERVER_ADDRESS;

    private static final long CACHE_SIZE = 10 * 1024 * 1024;    // 10 MB
    private static final int CONNECT_TIMEOUT = 30;
    private static final int WRITE_TIMEOUT = 300;
    private static final int TIMEOUT = 300;

    @NonNull
    public static OkHttpClient provideOkHttp(@NonNull Context context) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Cache cache = new Cache(context.getCacheDir(), CACHE_SIZE);

        return new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .cache(cache)
                .build();
    }

    @NonNull
    public static Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(NetworkHelpers.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.provideGson()))
                .client(okHttpClient)
                .build();
    }

    @NonNull
    public static API provideAPI(@NonNull Context context) {
        return provideRetrofit(provideOkHttp(context)).create(API.class);
    }

    @NonNull
    public static RequestBody prepareStringPart(@NonNull Object value) {
        Timber.d("prepareStringPart() called with: value = [ %s ]", value);
        return RequestBody.create(MultipartBody.FORM, String.valueOf(value));
    }

    @NonNull
    public static Map<String, RequestBody> prepareMapPart(@NonNull Object value) {
        Timber.d("prepareMapPart() called with: value = [ %s ]", value);
        Map<String, RequestBody> map = new HashMap<>();
        Gson gson = GsonUtils.provideGson();
        String jsonString = gson.toJson(value);
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            map.put(entry.getKey(),
                    NetworkHelpers.prepareStringPart(entry.getValue().getAsString()));
        }
        return map;
    }

    @NonNull
    public static String authorizationHeader(@NonNull String token) {
        return "Bearer " + token;
    }

    @NonNull
    public static MultipartBody.Part prepareImagePart(@NonNull String partName,
                                                      @NonNull Uri fileUri) {
        Timber.d("prepareImagePart() called with: partName = [ %s ], fileUri = [ %s ]", partName,
                 fileUri);
        File file = new File(fileUri.getPath());

        // handle RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
}
