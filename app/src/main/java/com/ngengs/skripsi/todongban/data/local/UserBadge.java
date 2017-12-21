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

public class UserBadge implements Parcelable {
    @SerializedName("name")
    private String name;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("share_url")
    private String shareUrl;
    @SerializedName("badge")
    private int badge;
    @SerializedName("response_count")
    private int response;
    @SerializedName("user_type")
    private int type;

    protected UserBadge(Parcel in) {
        name = in.readString();
        avatar = in.readString();
        shareUrl = in.readString();
        badge = in.readInt();
        response = in.readInt();
        type = in.readInt();
    }

    public static final Creator<UserBadge> CREATOR = new Creator<UserBadge>() {
        @Override
        public UserBadge createFromParcel(Parcel in) {
            return new UserBadge(in);
        }

        @Override
        public UserBadge[] newArray(int size) {
            return new UserBadge[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(avatar);
        parcel.writeString(shareUrl);
        parcel.writeInt(badge);
        parcel.writeInt(response);
        parcel.writeInt(type);
    }

    @Override
    public String toString() {
        return "UserBadge{" +
               "name='" + name + '\'' +
               ", avatar='" + avatar + '\'' +
               ", shareUrl='" + shareUrl + '\'' +
               ", badge=" + badge +
               ", response=" + response +
               ", type=" + type +
               '}';
    }
}
