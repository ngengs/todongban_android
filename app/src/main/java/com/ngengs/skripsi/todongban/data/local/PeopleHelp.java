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

public class PeopleHelp implements Parcelable {
    public static final Creator<PeopleHelp> CREATOR = new Creator<PeopleHelp>() {
        @Override
        public PeopleHelp createFromParcel(Parcel in) {
            return new PeopleHelp(in);
        }

        @Override
        public PeopleHelp[] newArray(int size) {
            return new PeopleHelp[size];
        }
    };
    private String id;
    private String name;
    private int badgeType;
    private int userType;
    private double distance;

    public PeopleHelp(String id, String name, int badgeType, int userType, double distance) {
        this.id = id;
        this.name = name;
        this.badgeType = badgeType;
        this.userType = userType;
        this.distance = distance;
    }

    protected PeopleHelp(Parcel in) {
        id = in.readString();
        name = in.readString();
        badgeType = in.readInt();
        userType = in.readInt();
        distance = in.readDouble();
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

    public int getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(int badgeType) {
        this.badgeType = badgeType;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(badgeType);
        dest.writeInt(userType);
        dest.writeDouble(distance);
    }

    @Override
    public String toString() {
        return "PeopleHelp{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", badgeType=" + badgeType +
               ", userType=" + userType +
               ", distance=" + distance +
               '}';
    }
}
