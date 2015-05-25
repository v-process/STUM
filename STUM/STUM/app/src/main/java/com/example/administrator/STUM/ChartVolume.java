package com.example.administrator.STUM;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ChartVolume extends Fragment {

    ArrayList<String> arrayDate = new ArrayList<String>();
    ArrayList<Entry> arrayDrink = new ArrayList<Entry>();

    LineChart mChart;
    int a,b;

    public static ChartVolume newInstance() {
        ChartVolume fragment = new ChartVolume();
        return fragment;
    }


    public ChartVolume() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_chart_volume, container, false);

        // Inflate the layout for this fragment
        mChart = (LineChart) v.findViewById(R.id.chart);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable value highlighting
        mChart.setHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);
        mChart.getAxisRight().setEnabled(false);
        mChart.setDragDecelerationFrictionCoef(0.95f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("dataTestMH");
        query2.addDescendingOrder("createdAt");

        query2.whereEqualTo("month", 5);
        query2.setLimit(10);
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> drinkList, ParseException e) {

                if (e == null) {
                    for (int i=0; i<drinkList.size(); i++) {
                        ParseObject course = drinkList.get(i);

                        Number date = course.getNumber("day");
                        Number vol = course.getNumber("watervolume");

                        arrayDate.add(String.valueOf(date)+ "일");
                        arrayDrink.add(new Entry(vol.floatValue(), i));
                    }
                }
                else {
                    Log.d("score", "Error: " + e.getMessage());
                }

                mChart.animateX(2500);

                // get the legend (only possible after setting data)
                Legend l = mChart.getLegend();

                // modify the legend ...
                // l.setPosition(LegendPosition.LEFT_OF_CHART);
                l.setForm(Legend.LegendForm.LINE);
                l.setTextSize(11f);
                l.setTextColor(Color.WHITE);
                l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);

                XAxis xAxis = mChart.getXAxis();
                xAxis.setTextSize(12f);
                xAxis.setTextColor(Color.WHITE);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawAxisLine(false);
                xAxis.setSpaceBetweenLabels(1);
                xAxis.setDrawAxisLine(true);
                xAxis.enableGridDashedLine(10f, 10f, 0f);

                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.setTextColor(Color.GREEN);
                leftAxis.setDrawGridLines(false);
                //leftAxis.setStartAtZero(false);
                //leftAxis.setAxisMaxValue(330f);
                //leftAxis.setAxisMinValue(610f);
                leftAxis.setDrawAxisLine(true);
                leftAxis.enableGridDashedLine(10f, 10f, 0f);

                setData();
            }
        });
        return v;
    }

    private void setData() {
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(arrayDrink, "마신 양");
        set1.setColor(Color.GREEN);
        set1.setCircleColor(Color.WHITE);
        set1.setLineWidth(2f);
        set1.setCircleSize(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(arrayDate, dataSets);
        data.setValueTextColor(Color.GREEN);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
        //mChart.invalidate();
    }
}
