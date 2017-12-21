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

public class UserWithLocation extends User implements Parcelable {
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName(value = "garage_name", alternate = "name")
    private String garageName;

    public UserWithLocation() {
        super();
    }

    protected UserWithLocation(Parcel in) {
        super(in);
        latitude = in.readDouble();
        longitude = in.readDouble();
        garageName = in.readString();
    }

    public static final Creator<UserWithLocation> CREATOR = new Creator<UserWithLocation>() {
        @Override
        public UserWithLocation createFromParcel(Parcel in) {
            return new UserWithLocation(in);
        }

        @Override
        public UserWithLocation[] newArray(int size) {
            return new UserWithLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
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

    @Override
    public String toString() {
        return super.toString() + ", UserWithLocation{" +
               "latitude=" + latitude +
               ", longitude=" + longitude +
               ", garageName='" + garageName + '\'' +
               '}';
    }

    public String getGarageName() {
        return garageName;
    }

    public void setGarageName(String garageName) {
        this.garageName = garageName;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(garageName);
    }
}
