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

import com.google.gson.annotations.SerializedName;
import com.ngengs.skripsi.todongban.R;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class HelpType implements Parcelable {

    public static final int VEHICLE_MOTORCYCLE = 1;
    public static final int VEHICLE_CAR = 2;

    public static final String TYPE_MOTORCYCLE_FLAT_TIRE = "18e135f0-6406-11e7-bfcc-68f7286287bc";
    public static final String TYPE_MOTORCYCLE_NO_FUEL = "22b36853-6406-11e7-bfcc-68f7286287bc";
    public static final String TYPE_MOTORCYCLE_BROKEN = "25df3334-6406-11e7-bfcc-68f7286287bc";
    public static final String TYPE_CAR_FLAT_TIRE = "2b095992-6406-11e7-bfcc-68f7286287bc";
    public static final String TYPE_CAR_NO_FUEL = "2e3905d8-6406-11e7-bfcc-68f7286287bc";
    public static final String TYPE_CAR_BROKEN = "317b5fd5-6406-11e7-bfcc-68f7286287bc";

    @SerializedName("id_help_type")
    private String id;
    @SerializedName("vehicle")
    private int vehicle;
    @SerializedName("status")
    private int status;

    protected HelpType(Parcel in) {
        id = in.readString();
        vehicle = in.readInt();
        status = in.readInt();
    }

    public static final Creator<HelpType> CREATOR = new Creator<HelpType>() {
        @Override
        public HelpType createFromParcel(Parcel in) {
            return new HelpType(in);
        }

        @Override
        public HelpType[] newArray(int size) {
            return new HelpType[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVehicle() {
        return vehicle;
    }

    public void setVehicle(int vehicle) {
        this.vehicle = vehicle;
    }

    public int getStatus() {
        return status;
    }

    public boolean getStatusBoolean() {
        return (status == 1);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatusBoolean(boolean status) {
        this.status = (status) ? 1 : 0;
    }

    public static List<String> getHelpTypeList(int vehicle) {
        List<String> helpType = new ArrayList<>();
        if (vehicle == HelpType.VEHICLE_CAR) {
            helpType.add(HelpType.TYPE_CAR_FLAT_TIRE);
            helpType.add(HelpType.TYPE_CAR_NO_FUEL);
            helpType.add(HelpType.TYPE_CAR_BROKEN);
        } else if (vehicle == HelpType.VEHICLE_MOTORCYCLE) {
            helpType.add(HelpType.TYPE_MOTORCYCLE_FLAT_TIRE);
            helpType.add(HelpType.TYPE_MOTORCYCLE_NO_FUEL);
            helpType.add(HelpType.TYPE_MOTORCYCLE_BROKEN);
        }

        return helpType;
    }

    public static int getVehicleFromHelpType(String helpType) {
        if (helpType != null) {
            if (helpType.equalsIgnoreCase(TYPE_CAR_BROKEN) ||
                helpType.equalsIgnoreCase(TYPE_CAR_FLAT_TIRE) ||
                helpType.equalsIgnoreCase(TYPE_CAR_NO_FUEL)) {
                return VEHICLE_CAR;
            } else if (helpType.equalsIgnoreCase(TYPE_MOTORCYCLE_BROKEN) ||
                       helpType.equalsIgnoreCase(TYPE_MOTORCYCLE_FLAT_TIRE) ||
                       helpType.equalsIgnoreCase(TYPE_MOTORCYCLE_NO_FUEL)) {
                return VEHICLE_MOTORCYCLE;
            }
        }
        return 0;
    }

    @StringRes
    public static int getVehicleNameFromHelpType(String helpType) {
        switch (HelpType.getVehicleFromHelpType(helpType)) {
            case HelpType.VEHICLE_CAR:
                return R.string.help_vehicle_car;
            case HelpType.VEHICLE_MOTORCYCLE:
                return R.string.help_vehicle_motorcycle;
            default:
                return 0;
        }
    }

    @DrawableRes
    public static int getVehicleIconFromHelpType(String helpType) {
        switch (HelpType.getVehicleFromHelpType(helpType)) {
            case HelpType.VEHICLE_CAR:
                return R.drawable.ic_vehicle_car;
            case HelpType.VEHICLE_MOTORCYCLE:
                return R.drawable.ic_vehicle_motorcycle;
            default:
                return 0;
        }
    }

    @DrawableRes
    public static int getIconFromHelpType(String helpType) {
        if (helpType.equalsIgnoreCase(TYPE_CAR_BROKEN)) {
            return R.drawable.ic_help_broken_car;
        } else if (helpType.equalsIgnoreCase(TYPE_CAR_FLAT_TIRE)) {
            return R.drawable.ic_help_flat_tire;
        } else if (helpType.equalsIgnoreCase(TYPE_CAR_NO_FUEL)) {
            return R.drawable.ic_help_no_fuel;
        } else if (helpType.equalsIgnoreCase(TYPE_MOTORCYCLE_BROKEN)) {
            return R.drawable.ic_help_broken_motorcycle;
        } else if (helpType.equalsIgnoreCase(TYPE_MOTORCYCLE_FLAT_TIRE)) {
            return R.drawable.ic_help_flat_tire;
        } else if (helpType.equalsIgnoreCase(TYPE_MOTORCYCLE_NO_FUEL)) {
            return R.drawable.ic_help_no_fuel;
        }
        return 0;
    }

    @StringRes
    public static int getNameFromHelpType(String helpType) {
        if (helpType.equalsIgnoreCase(TYPE_CAR_BROKEN)) {
            return R.string.help_type_broken;
        } else if (helpType.equalsIgnoreCase(TYPE_CAR_FLAT_TIRE)) {
            return R.string.help_type_flat_tire;
        } else if (helpType.equalsIgnoreCase(TYPE_CAR_NO_FUEL)) {
            return R.string.help_type_no_fuel;
        } else if (helpType.equalsIgnoreCase(TYPE_MOTORCYCLE_BROKEN)) {
            return R.string.help_type_broken;
        } else if (helpType.equalsIgnoreCase(TYPE_MOTORCYCLE_FLAT_TIRE)) {
            return R.string.help_type_flat_tire;
        } else if (helpType.equalsIgnoreCase(TYPE_MOTORCYCLE_NO_FUEL)) {
            return R.string.help_type_no_fuel;
        }
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeInt(vehicle);
        parcel.writeInt(status);
    }
}
