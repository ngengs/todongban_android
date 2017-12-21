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

package com.ngengs.skripsi.todongban.data.local;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ResponseHelp implements Parcelable {

    public static final int STATUS_NOT_SELECTED = 0;
    public static final int STATUS_SELECTED = 1;
    public static final int RESPONSE_NOT_YET = 0;
    public static final int RESPONSE_ACCEPT = 1;
    public static final int RESPONSE_REJECT = 2;

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("help_type")
    private String helpType;
    @SerializedName("distance")
    private float distance;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("message")
    private String message;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("address")
    private String address;

    public ResponseHelp() {
    }

    protected ResponseHelp(Parcel in) {
        id = in.readString();
        name = in.readString();
        helpType = in.readString();
        distance = in.readFloat();
        avatar = in.readString();
        message = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        address = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHelpType() {
        return helpType;
    }

    public void setHelpType(String helpType) {
        this.helpType = helpType;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static final Creator<ResponseHelp> CREATOR = new Creator<ResponseHelp>() {
        @Override
        public ResponseHelp createFromParcel(Parcel in) {
            return new ResponseHelp(in);
        }

        @Override
        public ResponseHelp[] newArray(int size) {
            return new ResponseHelp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(helpType);
        parcel.writeFloat(distance);
        parcel.writeString(avatar);
        parcel.writeString(message);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(address);
    }
}
