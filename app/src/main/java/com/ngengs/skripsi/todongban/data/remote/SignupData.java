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

package com.ngengs.skripsi.todongban.data.remote;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("WeakerAccess")
public class SignupData implements Parcelable {
    public static final Creator<SignupData> CREATOR = new Creator<SignupData>() {
        @Override
        public SignupData createFromParcel(Parcel in) {
            return new SignupData(in);
        }

        @Override
        public SignupData[] newArray(int size) {
            return new SignupData[size];
        }
    };
    @SerializedName("message")
    private String message;
    @SerializedName("token")
    private String token;

    public SignupData() {
    }

    protected SignupData(Parcel in) {
        message = in.readString();
        token = in.readString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(message);
        parcel.writeString(token);
    }
}
