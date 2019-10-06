package com.bigdata.hadoop.reversed.index2;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class GatherGroupingComparator extends WritableComparator {

    public GatherGroupingComparator() {
        super(WordBean.class,true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        WordBean a1 = (WordBean)a;
        WordBean b1 = (WordBean)b;
        return a1.getWord().compareTo(b1.getWord());
    }
}
