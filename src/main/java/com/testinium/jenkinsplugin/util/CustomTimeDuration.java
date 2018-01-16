package com.testinium.jenkinsplugin.util;

import groovy.time.TimeDuration;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.ArrayList;
import java.util.List;

public class CustomTimeDuration extends TimeDuration {

    public CustomTimeDuration(int hours, int minutes, int seconds, int millis) {
        super(hours, minutes, seconds, millis);
    }

    public CustomTimeDuration(TimeDuration duration) {
        super(duration.getHours(), duration.getMinutes(), duration.getSeconds(), duration.getMillis());
    }


    @Override
    public String toString() {
        List buffer = new ArrayList();

        if (this.years != 0) buffer.add(this.years + "y");
        if (this.months != 0) buffer.add(this.months + "mon");
        if (this.days != 0) buffer.add(this.days + "d");
        if (this.hours != 0) buffer.add(this.hours + "h");
        if (this.minutes != 0) buffer.add(this.minutes + "m");

        int seconds = this.seconds;
        if (this.millis != 0) {
            seconds += 1;
        }

        if (seconds != 0) {
            buffer.add(seconds + "s");
        }

        if (!buffer.isEmpty()) {
            return DefaultGroovyMethods.join(buffer.iterator(), " ");
        } else {
            return "0";
        }
    }
}
