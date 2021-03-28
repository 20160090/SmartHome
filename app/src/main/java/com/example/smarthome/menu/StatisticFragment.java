
package com.example.smarthome.menu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smarthome.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class StatisticFragment extends Fragment {

    public StatisticFragment() {
        // Required empty public constructor
    }

    public static StatisticFragment newInstance(String param1, String param2) {
        StatisticFragment fragment = new StatisticFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        LineChart chart = view.findViewById(R.id.chart);
        ArrayList<String> xValues = new ArrayList();
        for (int i = 0; i < 11; i++) {
            xValues.add(LocalDate.now().minusDays(i).toString());
        }
        Description description = new Description();
        description.setText("TESTTTT");
        chart.setData(getData());
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return xValues.get((int) value);
            }
        });
        chart.setDescription(description);
        chart.getRenderer().getPaintRender().setShader(new LinearGradient(0,1000,0f,1000f, Color.RED, Color.GREEN, Shader.TileMode.MIRROR));
        chart.animateXY(2000, 2000);
        chart.invalidate();
        return view;
    }

    private LineData getData() {
        ArrayList yValues = new ArrayList();

        yValues.add(new Entry(0, 55f));
        yValues.add(new Entry(1, -20f));
        yValues.add(new Entry(2, 520f));
        yValues.add(new Entry(3, 800f));
        yValues.add(new Entry(4, 425f));
        yValues.add(new Entry(5, -220));
        yValues.add(new Entry(6, 530f));
        yValues.add(new Entry(7, 750f));
        yValues.add(new Entry(8, -220));
        yValues.add(new Entry(9, -1000f));
        yValues.add(new Entry(10, 725f));


        LineDataSet lineDataSet = new LineDataSet(yValues,"kW");
        lineDataSet.setColor(R.color.colorAccentTransparent);
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        lineDataSet.setLineWidth(1.75f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawFilled(true);
        LineData lineData = new LineData();
        lineData.addDataSet(lineDataSet);
        return lineData;
    }



}
