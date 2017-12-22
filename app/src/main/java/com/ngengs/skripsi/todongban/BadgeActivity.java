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

package com.ngengs.skripsi.todongban;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ngengs.skripsi.todongban.data.local.Badge;
import com.ngengs.skripsi.todongban.data.local.User;
import com.ngengs.skripsi.todongban.data.local.UserBadge;
import com.ngengs.skripsi.todongban.data.remote.CheckBadge;
import com.ngengs.skripsi.todongban.utils.ResourceUtils;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Response;
import timber.log.Timber;

public class BadgeActivity extends AppCompatActivity {

    private API mApi;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private AppBarLayout mAppbar;
    private TextView mBadgeTextName;
    private TextView mBadgeTextType;
    private TextView mBadgeTextBadgeName;
    private TextView mBadgeTextShareDesc;
    private CardView mBadgeCard;
    private FloatingActionButton mBadgeButtonShare;
    private CircleImageView mBadgeAvatar;
    private UserBadge mData;

    private MaterialDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge);
        initView();
        mApi = NetworkHelpers.provideAPI(this);
        mDialog.show();
        mApi.checkBadge().enqueue(new ApiResponse<>(this::checkSuccess, this::checkFailure));
    }

    public static void runBadge(AppCompatActivity activity) {
        Intent intent = new Intent(activity, BadgeActivity.class);
        activity.startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("ConstantConditions")
    private void checkSuccess(Response<CheckBadge> response) {
        Timber.d("checkSuccess() called with: response = [ %s ]", response);
        Timber.d("checkSuccess: %s", response.body().getData());
        mData = response.body().getData();
        Badge badge = new Badge(mData.getBadge());
        mBadgeTextName.setText(mData.getName());
        mBadgeTextBadgeName.setText("\"" + badge.getBadgeName(this, mData.getType()) + "\"");
        mBadgeTextType.setText(mData.getType() == User.TYPE_PERSONAL ? "Personal" : "Bengkel");
        mBadgeTextShareDesc.setText(
                getString(R.string.badge_share_desc, mData.getResponse()));
        Picasso.with(this)
               .load(mData.getAvatar())
               .resize(100, 100)
               .centerCrop()
               .into(mBadgeAvatar);
        mDialog.dismiss();
    }

    private void checkFailure(Throwable t) {
        Timber.e(t, "checkFailure: ");
        mDialog.dismiss();
        Snackbar.make(mBadgeButtonShare, "Terjadi kesalahan pada server", Snackbar.LENGTH_SHORT)
                .show();
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        mCollapsingToolbar = findViewById(R.id.collapsingToolbar);
        mAppbar = findViewById(R.id.appbar);
        mBadgeTextName = findViewById(R.id.badge_text_name);
        mBadgeTextType = findViewById(R.id.badge_text_type);
        mBadgeTextBadgeName = findViewById(R.id.badge_text_badge_name);
        mBadgeTextShareDesc = findViewById(R.id.badge_text_share_desc);
        mBadgeCard = findViewById(R.id.badge_card);
        mBadgeButtonShare = findViewById(R.id.badge_button_share);
        mBadgeAvatar = findViewById(R.id.badge_avatar);

        mBadgeAvatar.setImageBitmap(
                ResourceUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_avatar));

        mToolbar.setTitle("Gelar Anda");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mDialog = new MaterialDialog.Builder(this)
                .title("Memproses...")
                .progress(true, 0)
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .build();
        mBadgeButtonShare.setOnClickListener(view -> share());
    }

    private void share() {
        Timber.d("share() called");
        if (mData == null) return;
        Badge badge = new Badge(mData.getBadge());
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.badge_share_text,
                                      badge.getBadgeName(this, mData.getType()),
                                      mData.getShareUrl()));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Bagikan dengan..."));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
