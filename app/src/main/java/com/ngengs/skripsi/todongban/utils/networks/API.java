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
import com.ngengs.skripsi.todongban.data.remote.HelpConfig;
import com.ngengs.skripsi.todongban.data.remote.SingleStringData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface API {

    @GET("api/user/signout")
    Call<SingleStringData> signout();

    @GET("api/user/check_status")
    Call<CheckStatus> checkStatus();

    @POST("api/user/update_device_id")
    Call<SingleStringData> updateDeviceId(@Field("device_id") String deviceId);

    @FormUrlEncoded
    @POST("api/help/request")
    Call<SingleStringData> requestHelp(@Field("latitude") double latitude,
                                       @Field("longitude") double longitude,
                                       @Field("help_type") String helpType,
                                       @Field("message") String message,
                                       @Field("location_name") String locationName);

    @FormUrlEncoded
    @POST("api/help/request_cancel")
    Call<SingleStringData> requestHelpCancel(@Field("id_request") String requestId);

    @FormUrlEncoded
    @POST("api/location")
    Call<SingleStringData> updateLocation(@Field("latitude") double latitude,
                                          @Field("longitude") double longitude);

    @GET("api/config")
    Call<HelpConfig> getHelpConfig();

    @FormUrlEncoded
    @POST("api/config/update")
    Call<HelpConfig> updateHelpConfig(@Field("id_help_type[]") List<String> helpType,
                                      @Field("status[]") List<Integer> status);

}
