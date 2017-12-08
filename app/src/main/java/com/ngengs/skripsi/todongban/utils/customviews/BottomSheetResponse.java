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

package com.ngengs.skripsi.todongban.utils.customviews;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

import com.ngengs.skripsi.todongban.utils.customviews.interfaces.BottomSheetSlideResponse;
import com.ngengs.skripsi.todongban.utils.customviews.interfaces.BottomSheetStateResponse;

public class BottomSheetResponse extends BottomSheetBehavior.BottomSheetCallback {
    private BottomSheetStateResponse mStateListener;
    private BottomSheetSlideResponse mSlideListener;

    public BottomSheetResponse(
            @NonNull BottomSheetStateResponse mStateListener,
            @Nullable BottomSheetSlideResponse mSlideListener) {
        this.mStateListener = mStateListener;
        this.mSlideListener = mSlideListener;
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if(mStateListener != null) {
            mStateListener.onStateChanged(bottomSheet, newState);
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        if(mSlideListener != null){
            mSlideListener.onSlide(bottomSheet, slideOffset);
        }
    }
}
