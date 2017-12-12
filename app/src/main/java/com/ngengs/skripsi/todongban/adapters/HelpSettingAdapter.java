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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.data.local.HelpType;

import java.util.ArrayList;
import java.util.List;

public class HelpSettingAdapter extends RecyclerView.Adapter<HelpSettingAdapter.ViewHolder> {

    private List<HelpType> mData;

    public HelpSettingAdapter() {
        this.mData = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                              .inflate(R.layout.item_help_setting_select, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HelpType data = mData.get(position);
        holder.mSwitchHelpSetting.setChecked(data.getStatusBoolean());
        holder.mTitleHelpSetting.setText(HelpType.getNameFromHelpType(data.getId()));
        int resDescId;
        if (data.getStatusBoolean()) {
            resDescId = R.string.help_setting_description_on;
        } else {
            resDescId = R.string.help_setting_description_off;
        }
        holder.mDescHelpSetting.setText(resDescId);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void add(HelpType data) {
        mData.add(data);
    }

    public List<HelpType> get() {
        return mData;
    }

    private void toggleValue(int position) {
        HelpType data = mData.get(position);
        data.setStatusBoolean(!data.getStatusBoolean());
        mData.set(position, data);
        notifyItemChanged(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Switch mSwitchHelpSetting;
        TextView mTitleHelpSetting;
        TextView mDescHelpSetting;
        RelativeLayout mParentItemHelpSetting;

        ViewHolder(View view) {
            super(view);
            this.mSwitchHelpSetting = view.findViewById(R.id.switch_help_setting);
            this.mTitleHelpSetting = view.findViewById(R.id.title_help_setting);
            this.mDescHelpSetting = view.findViewById(R.id.desc_help_setting);
            this.mParentItemHelpSetting = view.findViewById(R.id.parent_item_help_setting);
            this.mParentItemHelpSetting.setOnClickListener(v -> toggleValue(getAdapterPosition()));
            this.mSwitchHelpSetting.setOnClickListener(v -> toggleValue(getAdapterPosition()));
        }
    }
}
