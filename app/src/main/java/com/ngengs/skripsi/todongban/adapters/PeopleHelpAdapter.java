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

package com.ngengs.skripsi.todongban.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.data.local.Badge;
import com.ngengs.skripsi.todongban.data.local.PeopleHelp;
import com.ngengs.skripsi.todongban.data.local.User;

import java.util.ArrayList;
import java.util.List;

public class PeopleHelpAdapter extends RecyclerView.Adapter<PeopleHelpAdapter.ViewHolder> {
    private List<PeopleHelp> mData;
    private OnClickListener mListener;
    private Context mContext;

    public PeopleHelpAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PeopleHelpAdapter.ViewHolder(
                LayoutInflater.from(parent.getContext())
                              .inflate(R.layout.item_people_help, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PeopleHelp data = mData.get(position);
        Badge badge = new Badge(data.getBadgeType());
        holder.mPeopleHelpBadge.setText(badge.getBadgeName(mContext, data.getUserType()));
        holder.mPeopleHelpName.setText(data.getName());
        String distance;
        if (data.getDistance() >= 1) {
            distance = data.getDistance() + " Km";
        } else {
            double meters = data.getDistance() * 1000;
            if (meters < 50) {
                distance = "Dekat dari anda";
            } else {
                distance = meters + " Meter";
            }
        }
        if (data.getUserType() == User.TYPE_GARAGE) {
            distance += (data.isAccept()) ? " (Dapat menghampiri)" : " (Belum dapat menghampiri)";
        }
        holder.mPeopleHelpDistance.setText(distance);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addData(PeopleHelp data) {
        boolean change = false;
        int changePosition = 0;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getId().equalsIgnoreCase(data.getId())) {
                PeopleHelp newSavedData = mData.get(i);
                newSavedData.setAccept(data.isAccept());
                mData.set(i, newSavedData);
                change = true;
                changePosition = i;
                break;
            }
        }
        if (!change) {
            mData.add(data);
            notifyItemInserted(getItemCount() - 1);
        } else {
            notifyItemChanged(changePosition);
        }
    }

    public List<PeopleHelp> getData() {
        return mData;
    }

    public void setOnClickListener(OnClickListener clickListener) { mListener = clickListener; }


    public interface OnClickListener {
        void onClick(@NonNull String helpResponseId);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mPeopleHelpName;
        TextView mPeopleHelpBadge;
        TextView mPeopleHelpDistance;
        CardView mPeopleHelpLayout;

        ViewHolder(View view) {
            super(view);
            this.mPeopleHelpName = view.findViewById(R.id.text_people_help_name);
            this.mPeopleHelpBadge = view.findViewById(R.id.text_people_help_badge);
            this.mPeopleHelpDistance = view.findViewById(R.id.text_people_help_distance);
            this.mPeopleHelpLayout = view.findViewById(R.id.text_people_help_layout);
            mPeopleHelpLayout.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onClick(mData.get(getAdapterPosition()).getId());
                }
            });
        }
    }
}
