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

package com.ngengs.skripsi.todongban.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by ngengs on 10/26/2017.
 */

public class FitSystemWindowsFrameLayout extends FrameLayout {
    public FitSystemWindowsFrameLayout(
            @NonNull Context context) {
        super(context);
    }

    public FitSystemWindowsFrameLayout(@NonNull Context context,
                                       @Nullable
                                               AttributeSet attrs) {
        super(context, attrs);
    }

    public FitSystemWindowsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                                       int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FitSystemWindowsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                                       int defStyleAttr,
                                       int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
