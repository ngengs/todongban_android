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

import java.util.Date;

public class Garage implements Parcelable {

    public static final int FORCE_CLOSE_NOT = 0;
    public static final int FORCE_CLOSE_TRUE = 1;

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
    @SerializedName(value = "garage_id", alternate = "id")
    private String id;
    @SerializedName(value = "garage_user", alternate = "user")
    private User user;
    @SerializedName(value = "garage_name", alternate = "name")
    private String name;
    @SerializedName(value = "garage_open", alternate = "open")
    private Date openHour;
    @SerializedName(value = "garage_close", alternate = "close")
    private Date closeHour;
    @SerializedName(value = "garage_address", alternate = "address")
    private String address;
    @SerializedName(value = "garage_latitude", alternate = "latitude")
    private double latitude;
    @SerializedName(value = "garage_longitude", alternate = "longitude")
    private double longitude;
    @SerializedName(value = "garage_force_close", alternate = "force_close")
    private int forceClose;

    public Garage(User user) {
        this.user = user;
    }

    protected Garage(Parcel in) {
        id = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        name = in.readString();
        openHour = new Date(in.readLong());
        closeHour = new Date(in.readLong());
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        forceClose = in.readInt();
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

    public Date getOpenHour() {
        return openHour;
    }

    public void setOpenHour(Date openHour) {
        this.openHour = openHour;
    }

    public Date getCloseHour() {
        return closeHour;
    }

    public void setCloseHour(Date closeHour) {
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

    public int getForceClose() {
        return forceClose;
    }

    public void setForceClose(int forceClose) {
        this.forceClose = forceClose;
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
        dest.writeLong(openHour.getTime());
        dest.writeLong(closeHour.getTime());
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(forceClose);
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
               ", forceClose=" + forceClose +
               '}';
    }
}
