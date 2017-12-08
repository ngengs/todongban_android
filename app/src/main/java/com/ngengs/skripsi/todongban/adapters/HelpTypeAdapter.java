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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.data.local.RequestHelp;

import java.util.ArrayList;
import java.util.List;

public class HelpTypeAdapter extends RecyclerView.Adapter<HelpTypeAdapter.ViewHolder> {
    private List<String> mData;
    private OnClickListener mListener;

    public HelpTypeAdapter() {
        mData = new ArrayList<>();
        mListener = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                              .inflate(R.layout.item_help_type, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String helpType = mData.get(position);
        holder.mImageHelpType.setImageResource(RequestHelp.getIconFromHelpType(helpType));
        holder.mImageVehicleType.setImageResource(RequestHelp.getVehicleIconFromHelpType(helpType));
        holder.mTextHelpType.setText(RequestHelp.getNameFromHelpType(helpType));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setVehicleType(int vehicleType) {
        int firstSize = getItemCount();
        mData.clear();
        notifyItemRangeRemoved(0, firstSize);
        mData.addAll(RequestHelp.getHelpTypeList(vehicleType));
        notifyItemRangeInserted(0, getItemCount());
    }

    public void setOnClickListener(OnClickListener clickListener) {
        mListener = clickListener;
    }

    public interface OnClickListener {
        void onClick(@NonNull String helpTypeId);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageHelpType;
        ImageView mImageVehicleType;
        TextView mTextHelpType;

        ViewHolder(View view) {
            super(view);
            this.mImageHelpType = view.findViewById(R.id.image_help_type);
            this.mImageVehicleType = view.findViewById(R.id.image_vehicle_type);
            this.mTextHelpType = view.findViewById(R.id.text_help_type);
            view.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onClick(mData.get(getAdapterPosition()));
                }
            });
        }
    }
}
