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

public class Garage implements Parcelable {
    public static final Creator<Garage> CREATOR = new Creator<Garage>() {
        @Override
        public Garage createFromParcel(Parcel in) {
            return new Garage(in);
        }

        @Override
        public Garage[] newArray(int size) {
            return new Garage[size];
        }
    };
    private String id;
    private User user;
    private String name;
    private long openHour;
    private long closeHour;
    private String address;
    private double latitude;
    private double longitude;

    public Garage(User user) {
        this.user = user;
    }

    protected Garage(Parcel in) {
        id = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        name = in.readString();
        openHour = in.readLong();
        closeHour = in.readLong();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOpenHour() {
        return openHour;
    }

    public void setOpenHour(long openHour) {
        this.openHour = openHour;
    }

    public long getCloseHour() {
        return closeHour;
    }

    public void setCloseHour(long closeHour) {
        this.closeHour = closeHour;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(user, flags);
        dest.writeString(name);
        dest.writeLong(openHour);
        dest.writeLong(closeHour);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @Override
    public String toString() {
        return "Garage{" +
               "id='" + id + '\'' +
               ", user=" + user +
               ", name='" + name + '\'' +
               ", openHour=" + openHour +
               ", closeHour=" + closeHour +
               ", address='" + address + '\'' +
               ", latitude=" + latitude +
               ", longitude=" + longitude +
               '}';
    }
}
