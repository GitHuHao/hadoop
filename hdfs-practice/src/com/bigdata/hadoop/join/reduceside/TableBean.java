package com.bigdata.hadoop.join.reduceside;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class TableBean implements Writable{

    private String orderId = "";
    private String productId = "";
    private int count = 0;
    private String name = "";

    public TableBean() {
    }

    public TableBean(String orderId, String productId, int count, String name) {
        this.orderId = orderId;
        this.productId = productId;
        this.count = count;
        this.name = name;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(orderId);
        out.writeUTF(productId);
        out.writeInt(count);
        out.writeUTF(name);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.orderId = in.readUTF();
        this.productId = in.readUTF();
        this.count = in.readInt();
        this.name = in.readUTF();
    }

    @Override
    public String toString() {
        return "TableBean{" +
                "orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", count=" + count +
                ", name='" + name + '\'' +
                '}';
    }
}
