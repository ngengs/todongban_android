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

import com.ngengs.skripsi.todongban.data.remote.CheckStatus;
import com.ngengs.skripsi.todongban.data.remote.Signup;
import com.ngengs.skripsi.todongban.data.remote.SingleStringData;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface API {

    @Multipart
    @POST("api/user/signup")
    Call<Signup> signupPersonal(@Part("username") RequestBody username,
                                @Part("email") RequestBody email,
                                @Part("password") RequestBody password,
                                @Part("full_name") RequestBody fullName,
                                @Part("phone") RequestBody phone,
                                @Part("gender") RequestBody gender,
                                @Part("address") RequestBody address,
                                @Part("identity_number") RequestBody identityNumber,
                                @Part("device_id") RequestBody deviceId,
                                @Part("type") RequestBody type,
                                @Part List<MultipartBody.Part> files);

    @Multipart
    @POST("api/user/signup")
    Call<Signup> signupGarage();

    @FormUrlEncoded
    @POST("api/user/signin")
    Call<SingleStringData> signin(@Field("username") String username,
                                  @Field("password") String password,
                                  @Field("device_id") String deviceId);
//    @FormUrlEncoded
//    @POST("api/user/signin")
//    Call<SingleStringData> signin(@Field("username") String username,
//                                  @Field("password") String password,
//                                  @Field("device_id") String deviceId);

    @GET("api/user/signout")
    Call<SingleStringData> signout(@Header("Authorization") String authHeader);

    @GET("api/user/check_status")
    Call<CheckStatus> checkStatus(@Header("Authorization") String authHeader);

    @POST("api/user/update_device_id")
    Call<SingleStringData> updateDeviceId(@Header("Authorization") String authHeader,
                                          @Field("device_id") String deviceId);

    @FormUrlEncoded
    @POST("api/help/request")
    Call<SingleStringData> requestHelp(@Header("Authorization") String authHeader,
                                       @Field("latitude") double latitude,
                                       @Field("longitude") double longitude,
                                       @Field("help_type") String helpType,
                                       @Field("message") String message,
                                       @Field("location_name") String locationName);

    @FormUrlEncoded
    @POST("api/help/request_cancel")
    Call<SingleStringData> requestHelpCancel(@Header("Authorization") String authHeader,
                                             @Field("id_request") String requestId);

    @FormUrlEncoded
    @POST("api/location")
    Call<SingleStringData> updateLocation(@Header("Authorization") String authHeader,
                                          @Field("latitude") double latitude,
                                          @Field("longitude") double longitude);

}
