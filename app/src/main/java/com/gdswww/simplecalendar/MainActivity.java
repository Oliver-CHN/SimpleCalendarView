package com.gdswww.simplecalendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements CalendarView.CalendarLongClick, CalendarView.CalendarClick {
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.longclick = this;
        calendarView.click = this;
    }

    @Override
    public void onItemLongClick(Date date) {
        Toast.makeText(this, "click item " + new SimpleDateFormat("yyyy-MM-dd").format(date), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Date date) {
        Toast.makeText(this, "click item " + new SimpleDateFormat("yyyy-MM-dd").format(date), Toast.LENGTH_SHORT).show();
    }
}
