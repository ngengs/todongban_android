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
import com.ngengs.skripsi.todongban.data.local.UserWithLocation;

public class ResponseDetail extends BaseData implements Parcelable {
    @SerializedName("data")
    private UserWithLocation data;

    protected ResponseDetail(Parcel in) {
        super(in);
        data = in.readParcelable(UserWithLocation.class.getClassLoader());
    }

    public static final Creator<ResponseDetail> CREATOR = new Creator<ResponseDetail>() {
        @Override
        public ResponseDetail createFromParcel(Parcel in) {
            return new ResponseDetail(in);
        }

        @Override
        public ResponseDetail[] newArray(int size) {
            return new ResponseDetail[size];
        }
    };

    public UserWithLocation getData() {
        return data;
    }

    public void setData(UserWithLocation data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(data, i);
    }

    @Override
    public String toString() {
        return super.toString() + ", ResponseDetail{" +
               "data=" + data +
               '}';
    }
}
