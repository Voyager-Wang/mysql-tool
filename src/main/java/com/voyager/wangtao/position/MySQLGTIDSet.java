package com.voyager.wangtao.position;

import java.util.*;

/**
 * Created by wt on 2020/8/10.
 */
public class MySQLGTIDSet {
    
    private LinkedHashMap<String, Range> uuidSets = new LinkedHashMap<>();
    
    public static MySQLGTIDSet parse(String gtidSets) {
        if (gtidSets == null || gtidSets.trim().equals("")) {
            return null;
        }
        String[] split = gtidSets.trim().split(",");
        MySQLGTIDSet mySQLGTIDSet = new MySQLGTIDSet();
        for (String gtidSet : split) {
            String[] uuidAndIntervals = gtidSet.split(":");
            UUID uuid = UUID.fromString(uuidAndIntervals[0].trim());
            Range range = new Range();
            for (int i = 1; i < uuidAndIntervals.length; i++) {
                range.append(new Interval(uuidAndIntervals[i].trim()));
            }
            mySQLGTIDSet.uuidSets.put(uuid.toString(), range);
        }
        return mySQLGTIDSet;
    }

    @Override
    public String toString() {
        if (uuidSets == null || uuidSets.isEmpty()) {
            return "";
        }
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Range> entry : uuidSets.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(entry.getKey()).append(":").append(entry.getValue().toString());
        }
        return sb.toString();
    }

    /**
     * Range 本质上是一个集合，具有求交集 并集 差集的运算
     */
    private static class Range {
        private List<Interval> intervals = new ArrayList<>();
        
        private void append(Interval interval) {
            if (!intervals.isEmpty()) {
                Interval lastInterval = intervals.get(intervals.size() -1);
                if (lastInterval.end >= interval.start) {
                    throw new IllegalArgumentException("append a illegal interval " + interval);
                } else {
                    intervals.add(interval);
                }
            }
        }
        
        private static Range (List<Interval> range) {
            
        }

        @Override
        public String toString() {
            if (intervals == null || intervals.isEmpty()) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Interval interval : intervals) {
                if (first) {
                    first = false;
                } else {
                    sb.append(":");
                }
                sb.append(interval);
            }
            return sb.toString();
        }
    }

    /**
     * 
     */
    private static class Interval {
        private final long start;
        private final long end;

        public Interval(long start, long end) {
            this.start = start;
            this.end = end;
        }
        
        public Interval(String interval) {
            String[] split = interval.split("-");
            if (split.length == 1) {
                this.start = Long.parseLong(split[0]);
                this.end = start;
            } else if (split.length == 2) {
                this.start = Long.parseLong(split[0]);
                this.end = Long.parseLong(split[1]);
            } else {
                throw new IllegalArgumentException("wrong GTID interval format " + interval);
            }
        }

        @Override
        public String toString() {

            if (start == end - 1) {
                return start + "";
            } else {
                return start + "-" + end;
            }
        }
    }
}
