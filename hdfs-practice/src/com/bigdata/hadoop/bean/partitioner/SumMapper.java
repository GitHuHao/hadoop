package com.bigdata.hadoop.bean.partitioner;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class SumMapper extends Mapper<LongWritable, Text,Text, FlowBean> {

    private Text k = new Text();
    private FlowBean bean = new FlowBean();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] data = value.toString().split("\t");
        String phone = data[1];
        Integer up = Integer.parseInt(data[data.length-3]);
        Integer down = Integer.parseInt(data[data.length-2]);
        bean.setUp(up);
        bean.setDown(down);
        bean.setSum(up + down);
        k.set(phone);
        context.write(k,bean);
    }
}
