package io.github.thewebcode.yplugin.time;


import io.github.thewebcode.yplugin.utilities.StringUtil;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum TimeType {
    TICK("tick", "ticks"),
    YEAR("y", "year", "years"),
    MONTH("m", "month", "months"),
    WEEK("w", "week", "weeks"),
    DAY("d", "day", "days"),
    HOUR("h", "hour", "hours"),
    MINUTE("i", "minute", "mins", "min"),
    SECOND("s", "second", "seconds", "secs", "sec"),
    MILLISECOND("o", "mille", "mil", "millesecond", "milles", "mils");

    private static Map<String, TimeType> timeTypes = new HashMap<>();

    static {
        for (TimeType timeType : EnumSet.allOf(TimeType.class)) {
            for (String id : timeType.getIds()) {
                timeTypes.put(id, timeType);
            }
        }
    }

    private String[] ids;

    TimeType(String... identifiers) {
        this.ids = identifiers;
    }

    public String[] getIds() {
        return ids;
    }

    public static TimeType getTimeType(String id) {
        return timeTypes.containsKey(id) ? timeTypes.get(id) : null;
    }

    public static boolean isTimeType(String id) {
        return isTimeType(id, false);
    }

    @Deprecated
    public static boolean isTimeType(String id, boolean lazy) {
        if (lazy) {
            for (String key : timeTypes.keySet()) {
                if (StringUtil.getLevenshteinDistance(id, key) < 4) {
                    return true;
                }
            }
            return false;
        }
        return timeTypes.containsKey(id);
    }
}
