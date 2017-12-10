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

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class User implements Parcelable {
    // Static value
    public static final int TYPE_PERSONAL = 1;
    public static final int TYPE_GARAGE = 2;
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_DEACTIVE = 2;
    public static final int STATUS_REJECTED = 3;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    @SerializedName("id")
    private String id;
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("phone")
    private String phone;
    @SerializedName("gender")
    private int gender;
    @SerializedName("birth_date")
    private Date birthDate;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("address")
    private String address;
    @SerializedName("identity_picture")
    private String identityPicture;
    @SerializedName("identity_number")
    private String identityNumber;
    @SerializedName("type")
    private int type;
    @SerializedName("status")
    private int status;
    @SerializedName("device_id")
    private String deviceId;
    // Local image
    private transient Uri avatarUri;
    private transient Uri identityUri;
    // Local Password
    @SerializedName("password")
    private String passwordClean;

    public User() {
    }

    protected User(Parcel in) {
        id = in.readString();
        username = in.readString();
        email = in.readString();
        fullName = in.readString();
        phone = in.readString();
        gender = in.readInt();
        avatar = in.readString();
        address = in.readString();
        identityPicture = in.readString();
        identityNumber = in.readString();
        type = in.readInt();
        status = in.readInt();
        deviceId = in.readString();
        passwordClean = in.readString();
        avatarUri = in.readParcelable(Uri.class.getClassLoader());
        identityUri = in.readParcelable(Uri.class.getClassLoader());
        birthDate = new Date(in.readLong());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIdentityPicture() {
        return identityPicture;
    }

    public void setIdentityPicture(String identityPicture) {
        this.identityPicture = identityPicture;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Uri getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(Uri avatarUri) {
        this.avatarUri = avatarUri;
    }

    public Uri getIdentityUri() {
        return identityUri;
    }

    public void setIdentityUri(Uri identityUri) {
        this.identityUri = identityUri;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPasswordClean() {
        return passwordClean;
    }

    public void setPasswordClean(String passwordClean) {
        this.passwordClean = passwordClean;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(username);
        parcel.writeString(email);
        parcel.writeString(fullName);
        parcel.writeString(phone);
        parcel.writeInt(gender);
        parcel.writeString(avatar);
        parcel.writeString(address);
        parcel.writeString(identityPicture);
        parcel.writeString(identityNumber);
        parcel.writeInt(type);
        parcel.writeInt(status);
        parcel.writeString(deviceId);
        parcel.writeString(passwordClean);
        parcel.writeParcelable(avatarUri, i);
        parcel.writeParcelable(identityUri, i);
        if (birthDate != null) {
            parcel.writeLong(birthDate.getTime());
        }
    }

    @Override
    public String toString() {
        return "User{" +
               "id='" + id + '\'' +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", fullName='" + fullName + '\'' +
               ", phone='" + phone + '\'' +
               ", gender=" + gender +
               ", birthDate=" + birthDate +
               ", avatar='" + avatar + '\'' +
               ", address='" + address + '\'' +
               ", identityPicture='" + identityPicture + '\'' +
               ", identityNumber='" + identityNumber + '\'' +
               ", type=" + type +
               ", status=" + status +
               ", deviceId='" + deviceId + '\'' +
               ", avatarUri=" + avatarUri +
               ", identityUri=" + identityUri +
               ", passwordClean='" + passwordClean + '\'' +
               '}';
    }
}
