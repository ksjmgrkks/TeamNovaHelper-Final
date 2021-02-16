package com.example.teamnovahelper.MainActivity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamnovahelper.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphActivity extends AppCompatActivity {
    private LineChart lineChart;
    final String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private List<Entry> getIncomeEntries() {
        ArrayList<Entry> incomeEntries = new ArrayList<>();
        incomeEntries.add(new Entry(1, 5000));
        incomeEntries.add(new Entry(2, 1390));
        incomeEntries.add(new Entry(3, 1190));
        incomeEntries.add(new Entry(4, 7200));
        incomeEntries.add(new Entry(5, 4790));
        incomeEntries.add(new Entry(6, 4500));
        incomeEntries.add(new Entry(7, 8000));
        return incomeEntries.subList(0, 7);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

//        lineChart = (LineChart)findViewById(R.id.chart);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        List<String> xAxisValues = new ArrayList<>(Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri","Sat"));
        List<Entry> incomeEntries = getIncomeEntries();
        dataSets = new ArrayList<>();
        LineDataSet set1;

        set1 = new LineDataSet(incomeEntries, "Income");
        set1.setColor(Color.rgb(65, 168, 121));
        set1.setValueTextColor(Color.rgb(55, 70, 73));
        set1.setValueTextSize(10f);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSets.add(set1);

//customization
        LineChart mLineGraph = (LineChart)findViewById(R.id.chart);
        mLineGraph.setTouchEnabled(true);
        mLineGraph.setDragEnabled(true);
        mLineGraph.setScaleEnabled(false);
        mLineGraph.setPinchZoom(false);
        mLineGraph.setDrawGridBackground(false);
        mLineGraph.setExtraLeftOffset(15);
        mLineGraph.setExtraRightOffset(15);
//to hide background lines
        mLineGraph.getXAxis().setDrawGridLines(false);
        mLineGraph.getAxisLeft().setDrawGridLines(false);
        mLineGraph.getAxisRight().setDrawGridLines(false);

//to hide right Y and top X border
        YAxis rightYAxis = mLineGraph.getAxisRight();
        rightYAxis.setEnabled(false);
        YAxis leftYAxis = mLineGraph.getAxisLeft();
        leftYAxis.setEnabled(false);
        XAxis topXAxis = mLineGraph.getXAxis();
        topXAxis.setEnabled(false);


        XAxis xAxis = mLineGraph.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        set1.setLineWidth(4f);
        set1.setCircleRadius(3f);
        set1.setDrawValues(false);

//String setter in x-Axis
        mLineGraph.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

        LineData data = new LineData(dataSets);
        mLineGraph.setData(data);
        mLineGraph.animateX(2000);
        mLineGraph.invalidate();
        mLineGraph.getLegend().setEnabled(false);
        mLineGraph.getDescription().setEnabled(false);
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTextColor(Color.BLACK);
//        xAxis.setTextSize(8);
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(weekdays));
//
//        YAxis yLAxis = lineChart.getAxisLeft();
//        yLAxis.setTextColor(Color.BLACK);
//
//        YAxis yRAxis = lineChart.getAxisRight();
//        yRAxis.setDrawLabels(false);
//        yRAxis.setDrawAxisLine(false);
//        yRAxis.setDrawGridLines(false);
//
//        List<Entry> entries = new ArrayList<>();
//        entries.add(new Entry(0, 1));
//        entries.add(new Entry(1, 2));
//        entries.add(new Entry(2, 0));
//        entries.add(new Entry(3, 4));
//        entries.add(new Entry(4, 3));
//
//        LineDataSet lineDataSet = new LineDataSet(entries, "공부 시간");
//        lineDataSet.setLineWidth(2);
//        lineDataSet.setCircleRadius(6);
//        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
//        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
//        lineDataSet.setDrawCircleHole(true);
//        lineDataSet.setDrawCircles(true);
//        lineDataSet.setDrawHorizontalHighlightIndicator(false);
//        lineDataSet.setDrawHighlightIndicators(false);
//        lineDataSet.setDrawValues(false);
//
//        LineData lineData = new LineData(lineDataSet);
//        lineChart.setData(lineData);
//
//        Description description = new Description();
//        description.setText("");
//
//        lineChart.setDoubleTapToZoomEnabled(false);
//        lineChart.setDrawGridBackground(false);
//        lineChart.setDescription(description);
//        lineChart.invalidate();
//
//        MyMarkerView marker = new MyMarkerView(this,R.layout.chart_marker_view);
//        marker.setChartView(lineChart);
//        lineChart.setMarker(marker);
    }
}