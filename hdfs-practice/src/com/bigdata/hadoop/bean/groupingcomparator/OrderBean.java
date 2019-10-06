package com.bigdata.hadoop.bean.groupingcomparator;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class OrderBean implements WritableComparable<OrderBean> {

    private String id;
    private float price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public OrderBean() {
    }

    public OrderBean(String id, float price) {
        this.id = id;
        this.price = price;
    }

    @Override
    public int compareTo(OrderBean o) {
        // 二次排序
        int result = 0;
        if(this.id.equals(o.id)){
            result = this.price - o.price>0?-1:1;
        }else{
            result = this.id.compareTo(o.id);
        }
        return result;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeFloat(price);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.id = in.readUTF();
        this.price = in.readFloat();
    }

    @Override
    public String toString() {
        return "OrderBean{" +
                "id='" + id + '\'' +
                "\tprice=" + price +
                '}';
    }
}
