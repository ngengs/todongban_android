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

import com.ngengs.skripsi.todongban.data.remote.Signup;
import com.ngengs.skripsi.todongban.data.remote.SingleStringData;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface APIUnSecure {

    @Multipart
    @POST("api/user/signup")
    Call<Signup> signup(@PartMap() Map<String, RequestBody> signupData,
                        @Part List<MultipartBody.Part> files);

    @FormUrlEncoded
    @POST("api/user/signin")
    Call<SingleStringData> signin(@Field("username") String username,
                                  @Field("password") String password,
                                  @Field("device_id") String deviceId);

}
