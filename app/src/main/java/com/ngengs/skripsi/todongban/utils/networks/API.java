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

import com.ngengs.skripsi.todongban.data.remote.CheckBadge;
import com.ngengs.skripsi.todongban.data.remote.CheckStatus;
import com.ngengs.skripsi.todongban.data.remote.GarageData;
import com.ngengs.skripsi.todongban.data.remote.HelpConfig;
import com.ngengs.skripsi.todongban.data.remote.HistoryData;
import com.ngengs.skripsi.todongban.data.remote.ResponseDetail;
import com.ngengs.skripsi.todongban.data.remote.ResponseSearch;
import com.ngengs.skripsi.todongban.data.remote.SingleStringData;
import com.ngengs.skripsi.todongban.data.remote.StatisticGarageList;

import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

public interface API {

    @GET("api/user/signout")
    Call<SingleStringData> signout();

    @GET("api/user/check_status")
    Call<CheckStatus> checkStatus();

    @FormUrlEncoded
    @POST("api/user/update_device_id")
    Call<SingleStringData> updateDeviceId(@Field("device_id") String deviceId);

    @FormUrlEncoded
    @POST("api/user/update_password")
    Call<SingleStringData> updatePassword(@Field("old_password") String passwordOld,
                                          @Field("new_password") String newdOld);

    @FormUrlEncoded
    @POST("api/help/request")
    Call<SingleStringData> requestHelp(@Field("latitude") double latitude,
                                       @Field("longitude") double longitude,
                                       @Field("help_type") String helpType,
                                       @Field("message") String message,
                                       @Field("location_name") String locationName);

    @FormUrlEncoded
    @POST("api/help/finish_request")
    Call<SingleStringData> finishHelp(@Field("id_request") String requestId,
                                      @Field("rating") int rating);

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

    @FormUrlEncoded
    @POST("api/help/response_search_detail")
    Call<ResponseSearch> responseSearchDetail(@Field("id_response") String idResponse);

    @FormUrlEncoded
    @POST("api/help/help_response_accept")
    Call<ResponseSearch> responseSearchAccept(@Field("id_response") String idResponse);

    @FormUrlEncoded
    @POST("api/help/help_response_reject")
    Call<ResponseSearch> responseSearchReject(@Field("id_response") String idResponse);

    @FormUrlEncoded
    @POST("api/help/select_response")
    Call<SingleStringData> responseSelect(@Field("id_response") String idResponse);

    @FormUrlEncoded
    @POST("api/help/detail_response")
    Call<ResponseDetail> responseDetail(@Field("id_request") String idRequest);

    @GET("api/garage")
    Call<GarageData> getGarage();

    @Multipart
    @POST("api/garage")
    Call<SingleStringData> updateGarage(@PartMap() Map<String, RequestBody> garageData);

    @FormUrlEncoded
    @POST("api/garage/statistic")
    Call<StatisticGarageList> statisticGarage(@Field("date_start") Date startDate,
                                              @Field("date_end") Date endDate);

    @GET("api/help/history_request")
    Call<HistoryData> historyRequest();

    @GET("api/help/history_response")
    Call<HistoryData> historyResponse();

    @GET("api/badge")
    Call<CheckBadge> checkBadge();

}
