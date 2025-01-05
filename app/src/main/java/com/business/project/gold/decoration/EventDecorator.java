package com.business.project.gold.decoration;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineBackgroundSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class EventDecorator implements DayViewDecorator {

    private int color;
    private List<CalendarDay> dates;

    public EventDecorator(int color, List<CalendarDay> dates) {
        this.color = color;
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // Return true for dates that should be decorated
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Display a dot for the decorated dates
        view.addSpan(new DotSpan(10, color)); // 10 is the radius of the dot
    }
}