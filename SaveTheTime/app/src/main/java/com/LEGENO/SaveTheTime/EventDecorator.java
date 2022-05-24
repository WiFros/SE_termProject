package com.LEGENO.SaveTheTime;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.time.LocalDate;
import java.util.List;

public class EventDecorator implements DayViewDecorator {
    private Drawable drawble;
    boolean isGroup;
    List list;

    public EventDecorator(Activity context, List scheduleList, boolean isGroup) {
        this.isGroup = isGroup;
        if(isGroup){
            drawble = context.getResources().getDrawable(R.drawable.group_event_dot);
        }
        else{
            drawble = context.getResources().getDrawable(R.drawable.event_dot);
        }
        this.list = scheduleList;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        LocalDate d = LocalDate.of(day.getYear(), day.getMonth() + 1, day.getDay());

        for (Object s : list) {
            if (((Schedule) s).containsDate(d)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(drawble);
    }

}
