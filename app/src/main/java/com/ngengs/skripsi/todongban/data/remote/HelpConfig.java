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
import com.ngengs.skripsi.todongban.data.local.HelpType;

import java.util.List;

public class HelpConfig extends BaseData implements Parcelable {
    @SerializedName("data")
    private List<HelpType> data;

    protected HelpConfig(Parcel in) {
        super(in);
        data = in.createTypedArrayList(HelpType.CREATOR);
    }

    public static final Creator<HelpConfig> CREATOR = new Creator<HelpConfig>() {
        @Override
        public HelpConfig createFromParcel(Parcel in) {
            return new HelpConfig(in);
        }

        @Override
        public HelpConfig[] newArray(int size) {
            return new HelpConfig[size];
        }
    };

    public List<HelpType> getData() {
        return data;
    }

    public void setData(List<HelpType> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeTypedList(data);
    }
}
