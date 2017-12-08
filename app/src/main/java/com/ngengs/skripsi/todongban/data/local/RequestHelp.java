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

import com.ngengs.skripsi.todongban.R;

import java.util.ArrayList;
import java.util.List;

public class RequestHelp implements Parcelable {
    public static final String HELP_TYPE_MOTORCYCLE_FLAT_TIRE
            = "18e135f0-6406-11e7-bfcc-68f7286287bc";
    public static final String HELP_TYPE_MOTORCYCLE_NO_FUEL
            = "22b36853-6406-11e7-bfcc-68f7286287bc";
    public static final String HELP_TYPE_MOTORCYCLE_BROKEN = "25df3334-6406-11e7-bfcc-68f7286287bc";
    public static final String HELP_TYPE_CAR_FLAT_TIRE = "2b095992-6406-11e7-bfcc-68f7286287bc";
    public static final String HELP_TYPE_CAR_NO_FUEL = "2e3905d8-6406-11e7-bfcc-68f7286287bc";
    public static final String HELP_TYPE_CAR_BROKEN = "317b5fd5-6406-11e7-bfcc-68f7286287bc";

    public static final int VEHICLE_TYPE_MOTORCYCLE = 1;
    public static final int VEHICLE_TYPE_CAR = 2;
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

    protected RequestHelp(Parcel in) {
        locationLatitude = in.readDouble();
        locationLongitude = in.readDouble();
        selectedHelpType = in.readString();
        message = in.readString();
        locationName = in.readString();
    }

    public static List<String> getHelpTypeList(int vehicle) {
        List<String> helpType = new ArrayList<>();
        if (vehicle == VEHICLE_TYPE_CAR) {
            helpType.add(RequestHelp.HELP_TYPE_CAR_FLAT_TIRE);
            helpType.add(RequestHelp.HELP_TYPE_CAR_NO_FUEL);
            helpType.add(RequestHelp.HELP_TYPE_CAR_BROKEN);
        } else if (vehicle == VEHICLE_TYPE_MOTORCYCLE) {
            helpType.add(RequestHelp.HELP_TYPE_MOTORCYCLE_FLAT_TIRE);
            helpType.add(RequestHelp.HELP_TYPE_MOTORCYCLE_NO_FUEL);
            helpType.add(RequestHelp.HELP_TYPE_MOTORCYCLE_BROKEN);
        }

        return helpType;
    }

    public static int getVehicleFromHelpType(String helpType) {
        if (helpType != null) {
            if (helpType.equalsIgnoreCase(HELP_TYPE_CAR_BROKEN) ||
                helpType.equalsIgnoreCase(HELP_TYPE_CAR_FLAT_TIRE) ||
                helpType.equalsIgnoreCase(HELP_TYPE_CAR_NO_FUEL)) {
                return VEHICLE_TYPE_CAR;
            } else if (helpType.equalsIgnoreCase(HELP_TYPE_MOTORCYCLE_BROKEN) ||
                       helpType.equalsIgnoreCase(HELP_TYPE_MOTORCYCLE_FLAT_TIRE) ||
                       helpType.equalsIgnoreCase(HELP_TYPE_MOTORCYCLE_NO_FUEL)) {
                return VEHICLE_TYPE_MOTORCYCLE;
            }
        }
        return 0;
    }

    public static int getVehicleNameFromHelpType(String helpType) {
        switch (RequestHelp.getVehicleFromHelpType(helpType)) {
            case VEHICLE_TYPE_CAR:
                return R.string.help_vehicle_car;
            case VEHICLE_TYPE_MOTORCYCLE:
                return R.string.help_vehicle_motorcycle;
            default:
                return 0;
        }
    }

    public static int getVehicleIconFromHelpType(String helpType) {
        switch (RequestHelp.getVehicleFromHelpType(helpType)) {
            case VEHICLE_TYPE_CAR:
                return R.drawable.image_icon_car;
            case VEHICLE_TYPE_MOTORCYCLE:
                return R.drawable.image_icon_motorcycle;
            default:
                return 0;
        }
    }

    public static int getIconFromHelpType(String helpType) {
        if (helpType.equalsIgnoreCase(HELP_TYPE_CAR_BROKEN)) {
            return R.drawable.image_icon_help_broken_car;
        } else if (helpType.equalsIgnoreCase(HELP_TYPE_CAR_FLAT_TIRE)) {
            return R.drawable.image_icon_help_flat_tire;
        } else if (helpType.equalsIgnoreCase(HELP_TYPE_CAR_NO_FUEL)) {
            return R.drawable.image_icon_help_no_fuel;
        } else if (helpType.equalsIgnoreCase(HELP_TYPE_MOTORCYCLE_BROKEN)) {
            return R.drawable.image_icon_help_broken_motorcycle;
        } else if (helpType.equalsIgnoreCase(HELP_TYPE_MOTORCYCLE_FLAT_TIRE)) {
            return R.drawable.image_icon_help_flat_tire;
        } else if (helpType.equalsIgnoreCase(HELP_TYPE_MOTORCYCLE_NO_FUEL)) {
            return R.drawable.image_icon_help_no_fuel;
        }
        return 0;
    }

    public static int getNameFromHelpType(String helpType) {
        if (helpType.equalsIgnoreCase(HELP_TYPE_CAR_BROKEN)) {
            return R.string.help_type_broken;
        } else if (helpType.equalsIgnoreCase(HELP_TYPE_CAR_FLAT_TIRE)) {
            return R.string.help_type_flat_tire;
        } else if (helpType.equalsIgnoreCase(HELP_TYPE_CAR_NO_FUEL)) {
            return R.string.help_type_no_fuel;
        } else if (helpType.equalsIgnoreCase(HELP_TYPE_MOTORCYCLE_BROKEN)) {
            return R.string.help_type_broken;
        } else if (helpType.equalsIgnoreCase(HELP_TYPE_MOTORCYCLE_FLAT_TIRE)) {
            return R.string.help_type_flat_tire;
        } else if (helpType.equalsIgnoreCase(HELP_TYPE_MOTORCYCLE_NO_FUEL)) {
            return R.string.help_type_no_fuel;
        }
        return 0;
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
        return RequestHelp.getVehicleFromHelpType(selectedHelpType);
    }

    public int getVehicleName() {
        return RequestHelp.getVehicleNameFromHelpType(selectedHelpType);
    }

    public int getVehicleIcon() {
        return RequestHelp.getVehicleIconFromHelpType(selectedHelpType);
    }

    public int getHelpTypeIcon() {
        return RequestHelp.getIconFromHelpType(selectedHelpType);
    }

    public int getHelpTypeName() {
        return RequestHelp.getNameFromHelpType(selectedHelpType);
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
