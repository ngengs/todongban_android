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

public class SingleStringData extends BaseData implements Parcelable {
    public static final Creator<SingleStringData> CREATOR = new Creator<SingleStringData>() {
        @Override
        public SingleStringData createFromParcel(Parcel in) {
            return new SingleStringData(in);
        }

        @Override
        public SingleStringData[] newArray(int size) {
            return new SingleStringData[size];
        }
    };
    @SerializedName("data")
    private String data;

    public SingleStringData() {
    }

    protected SingleStringData(Parcel in) {
        data = in.readString();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {parcel.writeString(data);}
}
