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
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.ngengs.skripsi.todongban.data.local.StatisticGarage;

import java.util.ArrayList;
import java.util.List;

public class StatisticGarageList extends BaseData implements Parcelable {
    @SerializedName("data")
    private List<StatisticGarage> data;

    protected StatisticGarageList(Parcel in) {
        super(in);
        data = in.createTypedArrayList(StatisticGarage.CREATOR);
    }

    public static final Creator<StatisticGarageList> CREATOR = new Creator<StatisticGarageList>() {
        @Override
        public StatisticGarageList createFromParcel(Parcel in) {
            return new StatisticGarageList(in);
        }

        @Override
        public StatisticGarageList[] newArray(int size) {
            return new StatisticGarageList[size];
        }
    };

    public List<StatisticGarage> getData() {
        return data;
    }

    public void setData(List<StatisticGarage> data) {
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

    @Override
    public String toString() {
        return super.toString() + ", StatisticGarageList{" +
               "data=" + TextUtils.join(", ", new ArrayList<>(data)) +
               '}';
    }
}
