package com.bigdata.hadoop.bean.globalsort;

import org.apache.hadoop.io.WritableComparable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FlowBean implements WritableComparable<FlowBean> {

    private long up;
    private long down;
    private long sum;

    public FlowBean() {}

    public FlowBean(long up, long down) {
        this.up = up;
        this.down = down;
        this.sum = up + down;
    }

    public long getUp() {
        return up;
    }

    public void setUp(long up) {
        this.up = up;
    }

    public long getDown() {
        return down;
    }

    public void setDown(long down) {
        this.down = down;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        return String.format("FlowBean[up:%s\tdown:%s\tsum:%s]",this.up,this.down,this.sum);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(this.up);
        out.writeLong(this.down);
        out.writeLong(this.sum);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.up = in.readLong();
        this.down = in.readLong();
        this.sum = in.readLong();
    }

    @Override
    public int compareTo(FlowBean o) {
        int result = o.up - this.up >0?1:-1;
        return result;
    }
}
