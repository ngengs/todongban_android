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

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.ngengs.skripsi.todongban.data.local.StatisticGarage;
import com.ngengs.skripsi.todongban.data.local.User;
import com.ngengs.skripsi.todongban.data.remote.CheckStatus;
import com.ngengs.skripsi.todongban.data.remote.StatisticGarageList;
import com.ngengs.skripsi.todongban.utils.charts.PrettyValueFormatter;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivityGarage extends AppCompatActivity {
    public static final String ARGS_USER = "USER";

    @SuppressWarnings("FieldCanBeLocal")
    private NavigationView mNavView;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private DrawerLayout mDrawerLayout;
    private MaterialDialog mDialog;
    private CircleImageView mNavHeaderAvatar;
    private TextView mNavHeaderName;
    private TextView mNavHeaderType;
    private API mApi;
    private List<StatisticGarage> mData;
    private User mUser;
    private Date mStartDate;
    private Date mEndDate;
    private Toolbar mToolbarMainGarage;
    private AppBarLayout mAppBarMainGarage;
    /** Tanggal Mulai */
    private TextInputEditText mInputStatisticStartDate;
    /** Tanggal Selesai */
    private TextInputEditText mInputStatisticEndDate;
    /** Tampilkan */
    private Button mStatisticButtonSubmit;
    private BarChart mStatisticChartHelpRequest;
    private BarChart mStatisticChartMotorCycle;
    private BarChart mStatisticChartCar;
    private PieChart mStatisticChartMotorCyclePie;
    private PieChart mStatisticChartCarPie;
    private LinearLayout mStatisticLayoutChart;
    private SimpleDateFormat mDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_garage);
        initView();
        if (getIntent().getParcelableExtra(ARGS_USER) != null) {
            mUser = getIntent().getParcelableExtra(ARGS_USER);
        }
        mApi = NetworkHelpers.provideAPI(this);
        mDateFormat = new SimpleDateFormat("dd-MMM", Locale.getDefault());
        if (mUser == null) {
            mDialog.show();
            mApi.checkStatus()
                .enqueue(new ApiResponse<>(this::getUserSuccess, this::getUserFailure));
        } else {
            initNavHeader();
        }
    }

    private void getUserSuccess(Response<CheckStatus> response) {
        Timber.d("getUserSuccess() called with: response = [ %s ]", response);
        mDialog.dismiss();
        CheckStatus responseBody = response.body();
        if (responseBody != null) {
            mUser = responseBody.getData();
            initNavHeader();
        } else {
            getUserFailure(new Throwable("Empty Response Data"));
        }
    }

    private void getUserFailure(Throwable t) {
        Timber.e(t, "getUserFailure: ");
        mDialog.dismiss();
        Snackbar.make(mToolbarMainGarage, "Terjadi kesalahan pada server", Snackbar.LENGTH_SHORT)
                .show();
    }

    private void getStatistic() {
        if (mStartDate == null || mEndDate == null) {
            Snackbar.make(mToolbarMainGarage,
                          "Atur tanggal mulai dan tanggal selesai terlebih dahulu",
                          Snackbar.LENGTH_SHORT).show();
            return;
        }
        mDialog.show();
        mApi.statisticGarage(mStartDate, mEndDate)
            .enqueue(new ApiResponse<>(this::loadStatisticSuccess, this::loadStatisticFailed));
    }

    @SuppressWarnings("ConstantConditions")
    private void loadStatisticSuccess(Response<StatisticGarageList> response) {
        Timber.d("loadStatisticSuccess() called with: response = [ %s ]", response);
        mData = new ArrayList<>(response.body().getData());
        mDialog.dismiss();
        mStatisticLayoutChart.setVisibility(View.VISIBLE);
        createRequestHelpChart();
        createMotorCycleChart();
        createCarChart();
    }

    private void loadStatisticFailed(Throwable t) {
        Timber.e(t, "loadStatisticFailed: ");
        mDialog.dismiss();
        Snackbar.make(mStatisticLayoutChart, "Terjadi kesalahan", Snackbar.LENGTH_SHORT).show();
    }

    private boolean handleNavigation(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                break;
            case R.id.menu_config_help:
                HelpSettingActivity.runSetting(this);
                break;
            case R.id.menu_config_garage:
                EditGarageActivity.runSetting(this);
                break;
            case R.id.menu_config_profile:
                EditProfileActivity.runSetting(this);
                break;
            case R.id.menu_signout:
                SignoutActivity.runSignout(this);
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initView() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavView = findViewById(R.id.nav_view);
        mDialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .title("Memproses")
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .build();
        mNavView.setNavigationItemSelectedListener(this::handleNavigation);
        mNavHeaderAvatar = mNavView.getHeaderView(0).findViewById(R.id.nav_header_avatar);
        mNavHeaderName = mNavView.getHeaderView(0).findViewById(R.id.nav_header_name);
        mNavHeaderType = mNavView.getHeaderView(0).findViewById(R.id.nav_header_type);
        mToolbarMainGarage = findViewById(R.id.toolbarMainGarage);
        mAppBarMainGarage = findViewById(R.id.appBarMainGarage);
        mInputStatisticStartDate = findViewById(R.id.inputStatisticStartDate);
        mInputStatisticEndDate = findViewById(R.id.inputStatisticEndDate);
        mStatisticButtonSubmit = findViewById(R.id.statisticButtonSubmit);
        mNavView.setCheckedItem(R.id.menu_home);
        mInputStatisticStartDate.setOnClickListener(this::openCalendar);
        mInputStatisticEndDate.setOnClickListener(this::openCalendar);
        mStatisticButtonSubmit.setOnClickListener(view -> getStatistic());
        setSupportActionBar(mToolbarMainGarage);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                                                                 mToolbarMainGarage,
                                                                 R.string.navigation_drawer_open,
                                                                 R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mStatisticChartHelpRequest = findViewById(R.id.statisticChartHelpRequest);
        mStatisticChartMotorCycle = findViewById(R.id.statisticChartMotorCycle);
        mStatisticChartCar = findViewById(R.id.statisticChartCar);
        mStatisticChartMotorCyclePie = findViewById(R.id.statisticChartMotorCyclePie);
        mStatisticChartCarPie = findViewById(R.id.statisticChartCarPie);
        mStatisticChartHelpRequest.setNoDataText("Data kosong");
        mStatisticChartMotorCycle.setNoDataText("Data kosong");
        mStatisticChartCar.setNoDataText("Data kosong");
        mStatisticChartMotorCyclePie.setNoDataText("Data kosong");
        mStatisticChartCarPie.setNoDataText("Data kosong");
        Description chartDescription = new Description();
        chartDescription.setText("");
        mStatisticChartHelpRequest.setDrawValueAboveBar(true);
        mStatisticChartHelpRequest.setDrawGridBackground(false);
        mStatisticChartHelpRequest.getAxisRight().setEnabled(false);
        mStatisticChartHelpRequest.getAxisLeft().setGranularity(1f);
        mStatisticChartHelpRequest.setDescription(chartDescription);
        mStatisticChartMotorCycle.setDrawValueAboveBar(true);
        mStatisticChartMotorCycle.setDrawGridBackground(false);
        mStatisticChartMotorCycle.getAxisRight().setEnabled(false);
        mStatisticChartMotorCycle.getAxisLeft().setGranularity(1f);
        mStatisticChartMotorCycle.setDescription(chartDescription);
        mStatisticChartCar.setDrawValueAboveBar(true);
        mStatisticChartCar.setDrawGridBackground(false);
        mStatisticChartCar.getAxisRight().setEnabled(false);
        mStatisticChartCar.getAxisLeft().setGranularity(1f);
        mStatisticChartCar.setDescription(chartDescription);
        mStatisticChartMotorCyclePie.setDescription(chartDescription);
        mStatisticChartCarPie.setDescription(chartDescription);
        mStatisticLayoutChart = findViewById(R.id.statisticLayoutChart);
    }

    private void openCalendar(View view) {
        Calendar now = Calendar.getInstance();
        if (view.getId() == mInputStatisticEndDate.getId()) {
            if (mStartDate == null) {
                Snackbar.make(mToolbarMainGarage, "Atur tanggal mulai terlebih dahulu",
                              Snackbar.LENGTH_SHORT).show();
                return;
            } else {
                now.setTime(mStartDate);
                now.add(Calendar.DATE, 1);
            }
        }
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                (v, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                    DateFormat format = DateFormat.getDateInstance();
                    String formatDate = format.format(calendar.getTime());
                    ((TextInputEditText) view).setText(formatDate);
                    if (view.getId() == mInputStatisticStartDate.getId()) {
                        mStartDate = calendar.getTime();
                    } else {
                        mEndDate = calendar.getTime();
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.autoDismiss(false);
        if (view.getId() == mInputStatisticEndDate.getId()) {
            datePickerDialog.setMinDate(now);
        }
        datePickerDialog.show(getSupportFragmentManager(), "DatePickerStatistic");
    }

    private void initNavHeader() {
        mNavHeaderName.setText(mUser.getFullName());
        mNavHeaderType.setText((mUser.getType() == User.TYPE_PERSONAL) ? "Personal" : "Bengkel");
        Picasso.with(this)
               .load(mUser.getAvatar())
               .resize(50, 50)
               .centerInside()
               .into(mNavHeaderAvatar);
    }

    private void createRequestHelpChart() {
        Timber.d("createRequestHelpChart() called");
        if (mData == null) return;
        if (!mStatisticChartHelpRequest.isEmpty()) mStatisticChartHelpRequest.clear();
        List<BarEntry> entries = new ArrayList<>();
        List<String> xLabels = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            StatisticGarage item = mData.get(i);
            Timber.d("createRequestHelpChart: data: %s , data: %s", i, item);
            entries.add(new BarEntry(i, item.getTotal()));
            xLabels.add(mDateFormat.format(item.getDate()));
        }
        BarDataSet dataSet = new BarDataSet(entries, "Permintaan Bantuan");
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorChart4));
        dataSet.setValueFormatter(new PrettyValueFormatter());
        BarData barData = new BarData(dataSet);
        mStatisticChartHelpRequest.setData(barData);
        mStatisticChartHelpRequest.setFitBars(true);
        XAxis xAxis = mStatisticChartHelpRequest.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(mData.size());
//        xAxis.setCenterAxisLabels(true);
//        xAxis.setSpaceMin(barData.getBarWidth() / 2f);
//        xAxis.setSpaceMax(barData.getBarWidth() / 2f);
        xAxis.setValueFormatter((value, axis) -> {
            if (value % 1 == 0 && value >= 0) {
                return xLabels.get((int) value % xLabels.size());
            } else {
                return "";
            }
        });
        mStatisticChartHelpRequest.invalidate();
        mStatisticChartHelpRequest.fitScreen();
    }

    private void createMotorCycleChart() {
        Timber.d("createMotorCycleChart() called");
        if (mData == null) return;
        if (!mStatisticChartMotorCycle.isEmpty()) mStatisticChartMotorCycle.clear();
        if (!mStatisticChartMotorCyclePie.isEmpty()) mStatisticChartMotorCyclePie.clear();
        List<BarEntry> entries1 = new ArrayList<>();
        List<BarEntry> entries2 = new ArrayList<>();
        List<BarEntry> entries3 = new ArrayList<>();
        List<String> xLabels = new ArrayList<>();
        String label1 = "";
        String label2 = "";
        String label3 = "";
        float count1 = 0;
        float count2 = 0;
        float count3 = 0;
        for (int i = 0; i < mData.size(); i++) {
            StatisticGarage item = mData.get(i);
            Timber.d("createMotorCycleChart: data: %s , data: %s", i, item);
            entries1.add(new BarEntry(i, item.getProblemMotor1()));
            entries2.add(new BarEntry(i, item.getProblemMotor2()));
            entries3.add(new BarEntry(i, item.getProblemMotor3()));
            count1 += item.getProblemMotor1();
            count2 += item.getProblemMotor2();
            count3 += item.getProblemMotor3();
            xLabels.add(mDateFormat.format(item.getDate()));
            if (TextUtils.isEmpty(label1)) {
                label1 = getString(item.getProblemMotor1Name());
            }
            if (TextUtils.isEmpty(label2)) {
                label2 = getString(item.getProblemMotor2Name());
            }
            if (TextUtils.isEmpty(label3)) {
                label3 = getString(item.getProblemMotor3Name());
            }
        }
        BarDataSet dataSet1 = new BarDataSet(entries1, label1);
        dataSet1.setValueFormatter(new PrettyValueFormatter());
        dataSet1.setColor(ContextCompat.getColor(this, R.color.colorChart1));
        BarDataSet dataSet2 = new BarDataSet(entries2, label2);
        dataSet2.setValueFormatter(new PrettyValueFormatter());
        dataSet2.setColor(ContextCompat.getColor(this, R.color.colorChart2));
        BarDataSet dataSet3 = new BarDataSet(entries3, label3);
        dataSet3.setValueFormatter(new PrettyValueFormatter());
        dataSet3.setColor(ContextCompat.getColor(this, R.color.colorChart3));
        BarData barData = new BarData(dataSet1, dataSet2, dataSet3);
        mStatisticChartMotorCycle.setData(barData);
        mStatisticChartMotorCycle.getBarData().setBarWidth(0.32f);
        mStatisticChartMotorCycle.groupBars(0f, 0.04f, 0f);
        XAxis xAxis = mStatisticChartMotorCycle.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(mData.size());
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter((value, axis) -> {
            if (value % 1 == 0 && value >= 0) {
                return xLabels.get((int) value % xLabels.size());
            } else {
                return "";
            }
        });
        mStatisticChartMotorCycle.invalidate();
        mStatisticChartMotorCycle.fitScreen();

        List<PieEntry> pieEntries = new ArrayList<>();
        float pieEntry1 = (count1 / (count1 + count2 + count3)) * 100;
        float pieEntry2 = (count2 / (count1 + count2 + count3)) * 100;
        float pieEntry3 = (count3 / (count1 + count2 + count3)) * 100;
        Timber.d("createMotorCycleChart: %f %f %f", pieEntry1, pieEntry2, pieEntry3);
        Timber.d("createMotorCycleChart: %s %s %s", count1, count2, count3);
        pieEntries.add(new PieEntry(pieEntry1, label1));
        pieEntries.add(new PieEntry(pieEntry2, label2));
        pieEntries.add(new PieEntry(pieEntry3, label3));
        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(this, R.color.colorChart1));
        colors.add(ContextCompat.getColor(this, R.color.colorChart2));
        colors.add(ContextCompat.getColor(this, R.color.colorChart3));
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        mStatisticChartMotorCyclePie.setData(pieData);
        mStatisticChartMotorCyclePie.invalidate();
    }

    private void createCarChart() {
        Timber.d("createCarChart() called");
        if (mData == null) return;
        if (!mStatisticChartCar.isEmpty()) mStatisticChartCar.clear();
        List<BarEntry> entries1 = new ArrayList<>();
        List<BarEntry> entries2 = new ArrayList<>();
        List<BarEntry> entries3 = new ArrayList<>();
        List<String> xLabels = new ArrayList<>();
        String label1 = "";
        String label2 = "";
        String label3 = "";
        float count1 = 0;
        float count2 = 0;
        float count3 = 0;
        for (int i = 0; i < mData.size(); i++) {
            StatisticGarage item = mData.get(i);
            Timber.d("createCarChart: data: %s , data: %s", i, item);
            entries1.add(new BarEntry(i, item.getProblemCar1()));
            entries2.add(new BarEntry(i, item.getProblemCar2()));
            entries3.add(new BarEntry(i, item.getProblemCar3()));
            count1 += item.getProblemCar1();
            count2 += item.getProblemCar2();
            count3 += item.getProblemCar3();
            xLabels.add(mDateFormat.format(item.getDate()));
            if (TextUtils.isEmpty(label1)) {
                label1 = getString(item.getProblemCar1Name());
            }
            if (TextUtils.isEmpty(label2)) {
                label2 = getString(item.getProblemCar2Name());
            }
            if (TextUtils.isEmpty(label3)) {
                label3 = getString(item.getProblemCar3Name());
            }
        }
        BarDataSet dataSet1 = new BarDataSet(entries1, label1);
        dataSet1.setValueFormatter(new PrettyValueFormatter());
        dataSet1.setColor(ContextCompat.getColor(this, R.color.colorChart1));
        BarDataSet dataSet2 = new BarDataSet(entries2, label2);
        dataSet2.setValueFormatter(new PrettyValueFormatter());
        dataSet2.setColor(ContextCompat.getColor(this, R.color.colorChart2));
        BarDataSet dataSet3 = new BarDataSet(entries3, label3);
        dataSet3.setValueFormatter(new PrettyValueFormatter());
        dataSet3.setColor(ContextCompat.getColor(this, R.color.colorChart3));
        BarData barData = new BarData(dataSet1, dataSet2, dataSet3);
        mStatisticChartCar.setData(barData);
        mStatisticChartCar.getBarData().setBarWidth(0.32f);
        mStatisticChartCar.groupBars(0f, 0.04f, 0f);
        XAxis xAxis = mStatisticChartCar.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(mData.size());
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter((value, axis) -> {
            if (value % 1 == 0 && value >= 0) {
                return xLabels.get((int) value % xLabels.size());
            } else {
                return "";
            }
        });
        mStatisticChartCar.invalidate();
        mStatisticChartCar.fitScreen();

        List<PieEntry> pieEntries = new ArrayList<>();
        float pieEntry1 = (count1 / (count1 + count2 + count3)) * 100;
        float pieEntry2 = (count2 / (count1 + count2 + count3)) * 100;
        float pieEntry3 = (count3 / (count1 + count2 + count3)) * 100;
        Timber.d("createCarChart: %f %f %f", pieEntry1, pieEntry2, pieEntry3);
        Timber.d("createCarChart: %s %s %s", count1, count2, count3);
        pieEntries.add(new PieEntry(pieEntry1, label1));
        pieEntries.add(new PieEntry(pieEntry2, label2));
        pieEntries.add(new PieEntry(pieEntry3, label3));
        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(this, R.color.colorChart1));
        colors.add(ContextCompat.getColor(this, R.color.colorChart2));
        colors.add(ContextCompat.getColor(this, R.color.colorChart3));
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        mStatisticChartCarPie.setData(pieData);
        mStatisticChartCarPie.invalidate();
    }

}
