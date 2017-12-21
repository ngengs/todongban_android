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
import com.ngengs.skripsi.todongban.data.local.ResponseHelp;

public class ResponseSearch extends BaseData implements Parcelable {
    @SerializedName("data")
    private ResponseHelp data;

    protected ResponseSearch(Parcel in) {
        super(in);
        data = in.readParcelable(ResponseHelp.class.getClassLoader());
    }

    public static final Creator<ResponseSearch> CREATOR = new Creator<ResponseSearch>() {
        @Override
        public ResponseSearch createFromParcel(Parcel in) {
            return new ResponseSearch(in);
        }

        @Override
        public ResponseSearch[] newArray(int size) {
            return new ResponseSearch[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public ResponseHelp getData() {
        return data;
    }

    public void setData(ResponseHelp data) {
        this.data = data;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(data, i);
    }
}
