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
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class RequestHelp implements Parcelable {

    public static final Creator<RequestHelp> CREATOR = new Creator<RequestHelp>() {
        @Override
        public RequestHelp createFromParcel(Parcel in) {
            return new RequestHelp(in);
        }

        @Override
        public RequestHelp[] newArray(int size) {
            return new RequestHelp[size];
        }
    };
    private double locationLatitude;
    private double locationLongitude;
    private String selectedHelpType;
    private String message;
    private String locationName;

    public RequestHelp(String selectedHelpType) {
        this.selectedHelpType = selectedHelpType;
        this.locationLatitude = 0;
        this.locationLongitude = 0;
        this.message = null;
        this.locationName = null;
    }

    public RequestHelp(double locationLatitude, double locationLongitude,
                       String selectedHelpType, String message, String locationName) {
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.selectedHelpType = selectedHelpType;
        this.message = message;
        this.locationName = null;
    }

    @SuppressWarnings("WeakerAccess")
    protected RequestHelp(Parcel in) {
        locationLatitude = in.readDouble();
        locationLongitude = in.readDouble();
        selectedHelpType = in.readString();
        message = in.readString();
        locationName = in.readString();
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getSelectedHelpType() {
        return selectedHelpType;
    }

    public void setSelectedHelpType(String selectedHelpType) {
        this.selectedHelpType = selectedHelpType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getVehicleType() {
        return HelpType.getVehicleFromHelpType(selectedHelpType);
    }

    @StringRes
    public int getVehicleName() {
        return HelpType.getVehicleNameFromHelpType(selectedHelpType);
    }

    @DrawableRes
    public int getVehicleIcon() {
        return HelpType.getVehicleIconFromHelpType(selectedHelpType);
    }

    @DrawableRes
    public int getHelpTypeIcon() {
        return HelpType.getIconFromHelpType(selectedHelpType);
    }

    @StringRes
    public int getHelpTypeName() {
        return HelpType.getNameFromHelpType(selectedHelpType);
    }

    @Override
    public String toString() {
        return "RequestHelp{" +
               "locationLatitude=" + locationLatitude +
               ", locationLongitude=" + locationLongitude +
               ", selectedHelpType='" + selectedHelpType + '\'' +
               ", message='" + message + '\'' +
               ", locationName='" + locationName + '\'' +
               '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(locationLatitude);
        dest.writeDouble(locationLongitude);
        dest.writeString(selectedHelpType);
        dest.writeString(message);
        dest.writeString(locationName);
    }
}
