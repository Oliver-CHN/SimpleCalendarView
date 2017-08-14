package com.gdswww.simplecalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/13.
 */

public class CalendarView extends LinearLayout {
    private ImageView ivPrevious, ivNext;
    private TextView tvShowDate;
    private GridView gv;
    /**
     * 初始化系统日历控件
     */
    private Calendar curDate = Calendar.getInstance();
    /**
     * 日期格式
     */
    private String displayFormat;
    /**
     * 事件回调
     */
    public CalendarLongClick longclick;
    public CalendarClick click;

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 1、绑定视图控件
     * 2、注册事件
     * 3、渲染
     */
    private void init(Context context, AttributeSet attrs) {
        initView(context);
        initEvent();
        //日期格式
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);
        String str = ta.getString(R.styleable.CalendarView_dateFormat);
        if (str == null) {
            displayFormat = "yyyy年MM月";
        }else{
            displayFormat = str;
        }
        ta.recycle();
        renderCalendar();
    }

    /**
     * 绑定视图控件
     *
     * @param context
     */
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.calendar_view, this);
        ivPrevious = (ImageView) findViewById(R.id.ivPrevious);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        tvShowDate = (TextView) findViewById(R.id.tvShowDate);
        gv = (GridView) findViewById(R.id.gv_calendar);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                click.onItemClick((Date) parent.getItemAtPosition(position));
            }
        });
        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(longclick == null){
                    return false;
                }else{
                    longclick.onItemLongClick((Date) parent.getItemAtPosition(position));
                    return true;
                }
            }
        });
    }

    /**
     * 注册事件
     */
    private void initEvent() {
        //上一个月
        ivPrevious.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //向前一个月
                curDate.add(Calendar.MONTH, -1);
                renderCalendar();
            }
        });
        //下一个月
        ivNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //向后一个月
                curDate.add(Calendar.MONTH, 1);
                renderCalendar();
            }
        });
    }

    /**
     * 渲染
     */
    private void renderCalendar() {
        /**
         * 头部文本显示
         */
        SimpleDateFormat sdf = new SimpleDateFormat(displayFormat);
        tvShowDate.setText(sdf.format(curDate.getTime()));
        /**
         * 日期显示
         */
        ArrayList<Date> cells = new ArrayList<>();
        //防止干扰其他Calendar
        Calendar calendar = (Calendar) curDate.clone();
        //calendar设置为当前月份的第一天
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //上个月剩余天数
        int preDays = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        //一个月最大的天数
        int maxDays = calendar.getActualMaximum(Calendar.DATE);
        //calendar设置为当前月份的最后一天
        calendar.set(Calendar.DAY_OF_MONTH, maxDays);
        //下个月剩余天数
        int nexDays = 7 - calendar.get(Calendar.DAY_OF_WEEK);
        //计算显示的数量
        int maxCellCount = preDays + maxDays + nexDays;
        //calendar设置为当前月份的第一天
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //calendar前移preDays天
        calendar.add(Calendar.DAY_OF_MONTH, -preDays);
        while (cells.size() < maxCellCount) {
            //将日期添加至数据源
            cells.add(calendar.getTime());
            //每次添加数据之后，向前推一天
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        gv.setAdapter(new CalendarAdapter(getContext(), cells));
    }

    /**
     * 日历适配器
     */
    private class CalendarAdapter extends ArrayAdapter<Date> {
        private LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<Date> days) {
            super(context, R.layout.item_calendar, days);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Date date = getItem(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_calendar, parent, false);
            }
            int day = date.getDate();
            ((TextView) convertView).setText(String.valueOf(day));
            //获取当前日期
            Date now = new Date();
            //判断是否为同一个月
            if (curDate.getTime().getMonth() == date.getMonth()) {
                ((TextView) convertView).setTextColor(parent.getContext().getColor(R.color.calendar_current_color));
            } else {
                ((TextView) convertView).setTextColor(parent.getContext().getColor(R.color.calendar_other_color));
            }
            //判断是否为当前日期
            if (now.getYear() == date.getYear()
                    && now.getMonth() == date.getMonth()
                    && now.getDate() == date.getDate()) {
                ((TextView) convertView).setTextColor(parent.getContext().getColor(R.color.colorPrimary));
            }
            return convertView;
        }
    }

    /**
     * 长按
     */
    public interface CalendarLongClick {
        void onItemLongClick(Date date);
    }

    /**
     * 点击
     */
    public interface CalendarClick {
        void onItemClick(Date date);
    }


}
