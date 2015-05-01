package com.example.administrator.slidertest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.util.ArrayList;
import java.util.List;


public class ChartFragment extends Fragment {

    private XYMultipleSeriesDataset dataset;
    private GraphicalView graphicalView;
    private double addX = 6;
    private double plus = 6;
    private double minus = 13;
    //private Handler handler = new Handler();
    /*
    private Runnable updateRunnable = new Runnable() {

        @Override
        public void run() {
            dataset.getSeriesAt(0).add(addX, plus);
            dataset.getSeriesAt(1).add(addX, minus);
            addX++;
            plus++;
            minus--;
            graphicalView.repaint();
            if (addX < 20) handler.postDelayed(updateRunnable, 1000);
        }
    };
    */

    XYMultipleSeriesRenderer renderer;
    Context context;
    FrameLayout frameLayout;

    ArrayList<String> mFuncDate = new ArrayList<String>();
    String[] strDate;



    public static ChartFragment newInstance(){
        ChartFragment fragment = new ChartFragment();
        return fragment;
    }

    public ChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ChartTest");
        query.whereEqualTo("date", "drink");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> dateList, ParseException e) {
                if (e == null) {
                    for(int i=0; i<2; i++) {
                        Log.d("data", "Retrieved Object is " + dateList.get(i).getString("Date"));
                        strDate[i] = dateList.get(i).getString("Date");
                    }

                } else {
                    Log.d("score", "Error: " + e.getMessage());

                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.chart_fragment, container, false);
        LinearLayout chartContainerlayout = (LinearLayout) v.findViewById(R.id.chart_container);

        return v;
    }

        /*

        String[] titles = new String[] { "마신 양", "온도" };


        List<double[]> values = new ArrayList<double[]>();
        values.add(new double[] { 1, 2, 3, 4, 5 });
        values.add(new double[] { 18, 17, 16, 15, 14 });
        int[] colors = new int[] { Color.BLUE, Color.RED };

        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE,	PointStyle.DIAMOND };


        double[] minValues = new double[] { -24, -19, -10, -1, 7, 12, 15, 14, 9, 1, -11, -16 };
        double[] maxValues = new double[] { 7, 12, 24, 28, 33, 35, 37, 36, 28, 19, 11, 4 };
        RangeCategorySeries series = new RangeCategorySeries("Temperature");
        int length2 = minValues.length;
        for (int k = 0; k < length2; k++) {
            series.add(minValues[k], maxValues[k]);
        }
        dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series.toXYSeries());


        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
            ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
        }
        setChartSettings(renderer, "Average temperature", "날짜",
                "온도", 0.5, 12.5, -10, 40, Color.LTGRAY, Color.LTGRAY);
        //renderer.setXLabels(300);
        for(int i=0; i< strDate.length; i++) {
            renderer.addXTextLabel(i, strDate[i]);
        }
        renderer.setYLabels(5);
        renderer.setShowGrid(true);
        renderer.setXLabelsAlign(Paint.Align.RIGHT);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setZoomButtonsVisible(true);
        renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
        renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });

        //dataset = buildDataset(titles, x, values);

        graphicalView = ChartFactory.getLineChartView(getActivity().getApplicationContext(), dataset, renderer);
        //getActivity().setContentView(graphicalView);
        chartContainerlayout.addView(graphicalView);

        //handler.postDelayed(updateRunnable, 1000);

        return v;
    }



    private XYMultipleSeriesDataset buildDataset(String[] titles,
                                                 List<double[]> xValues, List<double[]> yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        addXYSeries(dataset, titles, xValues, yValues, 0);
        return dataset;
    }





    private void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles,
                             List<double[]> xValues, List<double[]> yValues, int scale) {
        int length = titles.length;
        for (int i = 0; i < length; i++) {
            XYSeries series = new XYSeries(titles[i], scale);
            double[] xV = xValues.get(i);
            double[] yV = yValues.get(i);
            int seriesLength = xV.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
        }
    }

    private XYMultipleSeriesRenderer buildRenderer(int[] colors,
                                                   PointStyle[] styles) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        setRenderer(renderer, colors, styles);
        return renderer;
    }

    private void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors,
                             PointStyle[] styles) {
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setPointSize(5f);
        renderer.setMargins(new int[] { 20, 30, 15, 20 });
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            renderer.addSeriesRenderer(r);
        }
    }

    private void setChartSettings(XYMultipleSeriesRenderer renderer,
                                  String title, String xTitle, String yTitle, double xMin,
                                  double xMax, double yMin, double yMax, int axesColor,
                                  int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }

    */

}
