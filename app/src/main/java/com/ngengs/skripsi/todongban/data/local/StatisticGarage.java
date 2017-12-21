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
import android.support.annotation.StringRes;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class StatisticGarage implements Parcelable {
    @SerializedName("motor_1")
    private int problemMotor1;
    @SerializedName("motor_2")
    private int problemMotor2;
    @SerializedName("motor_3")
    private int problemMotor3;
    @SerializedName("car_1")
    private int problemCar1;
    @SerializedName("car_2")
    private int problemCar2;
    @SerializedName("car_3")
    private int problemCar3;
    @SerializedName("total")
    private int total;
    @SerializedName("date")
    private Date date;
    @SerializedName("motor_1_id")
    private String problemMotor1Id;
    @SerializedName("motor_2_id")
    private String problemMotor2Id;
    @SerializedName("motor_3_id")
    private String problemMotor3Id;
    @SerializedName("car_1_id")
    private String problemCar1Id;
    @SerializedName("car_2_id")
    private String problemCar2Id;
    @SerializedName("car_3_id")
    private String problemCar3Id;


    protected StatisticGarage(Parcel in) {
        problemMotor1 = in.readInt();
        problemMotor2 = in.readInt();
        problemMotor3 = in.readInt();
        problemCar1 = in.readInt();
        problemCar2 = in.readInt();
        problemCar3 = in.readInt();
        total = in.readInt();
        problemMotor1Id = in.readString();
        problemMotor2Id = in.readString();
        problemMotor3Id = in.readString();
        problemCar1Id = in.readString();
        problemCar2Id = in.readString();
        problemCar3Id = in.readString();
    }

    public static final Creator<StatisticGarage> CREATOR = new Creator<StatisticGarage>() {
        @Override
        public StatisticGarage createFromParcel(Parcel in) {
            return new StatisticGarage(in);
        }

        @Override
        public StatisticGarage[] newArray(int size) {
            return new StatisticGarage[size];
        }
    };

    public int getProblemMotor1() {
        return problemMotor1;
    }

    public void setProblemMotor1(int problemMotor1) {
        this.problemMotor1 = problemMotor1;
    }

    public int getProblemMotor2() {
        return problemMotor2;
    }

    public void setProblemMotor2(int problemMotor2) {
        this.problemMotor2 = problemMotor2;
    }

    public int getProblemMotor3() {
        return problemMotor3;
    }

    public void setProblemMotor3(int problemMotor3) {
        this.problemMotor3 = problemMotor3;
    }

    public int getProblemCar1() {
        return problemCar1;
    }

    public void setProblemCar1(int problemCar1) {
        this.problemCar1 = problemCar1;
    }

    public int getProblemCar2() {
        return problemCar2;
    }

    public void setProblemCar2(int problemCar2) {
        this.problemCar2 = problemCar2;
    }

    public int getProblemCar3() {
        return problemCar3;
    }

    public void setProblemCar3(int problemCar3) {
        this.problemCar3 = problemCar3;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getProblemMotor1Id() {
        return problemMotor1Id;
    }

    public void setProblemMotor1Id(String problemMotor1Id) {
        this.problemMotor1Id = problemMotor1Id;
    }

    public String getProblemMotor2Id() {
        return problemMotor2Id;
    }

    public void setProblemMotor2Id(String problemMotor2Id) {
        this.problemMotor2Id = problemMotor2Id;
    }

    public String getProblemMotor3Id() {
        return problemMotor3Id;
    }

    public void setProblemMotor3Id(String problemMotor3Id) {
        this.problemMotor3Id = problemMotor3Id;
    }

    public String getProblemCar1Id() {
        return problemCar1Id;
    }

    public void setProblemCar1Id(String problemCar1Id) {
        this.problemCar1Id = problemCar1Id;
    }

    public String getProblemCar2Id() {
        return problemCar2Id;
    }

    public void setProblemCar2Id(String problemCar2Id) {
        this.problemCar2Id = problemCar2Id;
    }

    public String getProblemCar3Id() {
        return problemCar3Id;
    }

    public void setProblemCar3Id(String problemCar3Id) {
        this.problemCar3Id = problemCar3Id;
    }

    @StringRes
    public int getProblemMotor1Name() {
        return HelpType.getNameFromHelpType(problemMotor1Id);
    }

    @StringRes
    public int getProblemMotor2Name() {
        return HelpType.getNameFromHelpType(problemMotor2Id);
    }

    @StringRes
    public int getProblemMotor3Name() {
        return HelpType.getNameFromHelpType(problemMotor3Id);
    }

    @StringRes
    public int getProblemCar1Name() {
        return HelpType.getNameFromHelpType(problemCar1Id);
    }

    @StringRes
    public int getProblemCar2Name() {
        return HelpType.getNameFromHelpType(problemCar2Id);
    }

    @StringRes
    public int getProblemCar3Name() {
        return HelpType.getNameFromHelpType(problemCar3Id);
    }


    @Override
    public String toString() {
        return "StatisticGarage{" +
               "problemMotor1=" + problemMotor1 +
               ", problemMotor2=" + problemMotor2 +
               ", problemMotor3=" + problemMotor3 +
               ", problemCar1=" + problemCar1 +
               ", problemCar2=" + problemCar2 +
               ", problemCar3=" + problemCar3 +
               ", total=" + total +
               ", date=" + date +
               ", problemMotor1Id=" + problemMotor1Id +
               ", problemMotor2Id=" + problemMotor2Id +
               ", problemMotor3Id=" + problemMotor3Id +
               ", problemCar1Id=" + problemCar1Id +
               ", problemCar2Id=" + problemCar2Id +
               ", problemCar3Id=" + problemCar3Id +
               '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(problemMotor1);
        parcel.writeInt(problemMotor2);
        parcel.writeInt(problemMotor3);
        parcel.writeInt(problemCar1);
        parcel.writeInt(problemCar2);
        parcel.writeInt(problemCar3);
        parcel.writeInt(total);
        parcel.writeString(problemMotor1Id);
        parcel.writeString(problemMotor2Id);
        parcel.writeString(problemMotor3Id);
        parcel.writeString(problemCar1Id);
        parcel.writeString(problemCar2Id);
        parcel.writeString(problemCar3Id);
    }
}
