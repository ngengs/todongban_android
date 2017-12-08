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
import com.ngengs.skripsi.todongban.data.local.User;

public class CheckStatus extends BaseData implements Parcelable{
    @SerializedName("data")
    private User data;

    public CheckStatus() {
    }

    protected CheckStatus(Parcel in) {
        data = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<CheckStatus> CREATOR = new Creator<CheckStatus>() {
        @Override
        public CheckStatus createFromParcel(Parcel in) {
            return new CheckStatus(in);
        }

        @Override
        public CheckStatus[] newArray(int size) {
            return new CheckStatus[size];
        }
    };

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {parcel.writeParcelable(data, i);}
}
