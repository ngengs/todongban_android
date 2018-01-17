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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.data.local.HelpType;
import com.ngengs.skripsi.todongban.data.local.History;
import com.ngengs.skripsi.todongban.data.local.RequestHelp;
import com.ngengs.skripsi.todongban.data.local.ResponseHelp;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    List<History> mData;
    Context mContext;

    public HistoryAdapter(Context context) {
        mData = new ArrayList<>();
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryAdapter.ViewHolder(
                LayoutInflater.from(parent.getContext())
                              .inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        History item = mData.get(position);
        String status;
        if (item.getResponse() == -999) {
            holder.mHistoryResponse.setVisibility(View.GONE);
            switch (item.getStatus()) {
                case RequestHelp.STATUS_CANCEL:
                    status = "Dibatalkan";
                    break;
                case RequestHelp.STATUS_PROCESS:
                    status = "Dalam Proses";
                    break;
                case RequestHelp.STATUS_REQUEST:
                    status = "Mencari bantuan";
                    break;
                case RequestHelp.STATUS_FINISH:
                    status = "Selesai";
                    break;
                default:
                    status = "";
            }
        } else {
            holder.mHistoryResponse.setVisibility(View.VISIBLE);
            String response;
            switch (item.getResponse()) {
                case ResponseHelp.RESPONSE_NOT_YET:
                    response = "Belum merespon";
                    break;
                case ResponseHelp.RESPONSE_ACCEPT:
                    response = "Memilih untuk membantu";
                    break;
                case ResponseHelp.RESPONSE_REJECT:
                    response = "Memilih untuk tidak membantu";
                    break;
                default:
                    response = "";
            }
            holder.mHistoryResponse.setText(
                    mContext.getString(R.string.history_response, response));
            switch (item.getStatus()) {
                case ResponseHelp.STATUS_NOT_SELECTED:
                    status = "Tidak/Belum terpilih";
                    break;
                case ResponseHelp.STATUS_SELECTED:
                    status = "Terpilih";
                    break;
                default:
                    status = "";
            }
        }
        holder.mHistoryStatus.setText(mContext.getString(R.string.history_status, status));
        holder.mHistoryTime.setText(mContext.getString(R.string.history_date,
                                                       DateFormat.getDateTimeInstance()
                                                                 .format(item.getDate())));
        holder.mHistoryHelpTypeImage.setImageResource(
                HelpType.getIconFromHelpType(item.getHelpType()));
        holder.mHistoryVehicleImage.setImageResource(
                HelpType.getVehicleIconFromHelpType(item.getHelpType()));
    }

    public void add(List<History> data) {
        int oldSize = mData.size();
        mData.clear();
        notifyItemRangeRemoved(0, oldSize);
        mData.addAll(data);
        notifyItemRangeInserted(0, mData.size());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mHistoryHelpTypeImage;
        ImageView mHistoryVehicleImage;
        TextView mHistoryTime;
        TextView mHistoryResponse;
        TextView mHistoryStatus;

        ViewHolder(View view) {
            super(view);
            this.mHistoryHelpTypeImage = view.findViewById(R.id.history_help_type_image);
            this.mHistoryVehicleImage = view.findViewById(R.id.history_vehicle_image);
            this.mHistoryTime = view.findViewById(R.id.history_time);
            this.mHistoryResponse = view.findViewById(R.id.history_response);
            this.mHistoryStatus = view.findViewById(R.id.history_status);
        }
    }
}
