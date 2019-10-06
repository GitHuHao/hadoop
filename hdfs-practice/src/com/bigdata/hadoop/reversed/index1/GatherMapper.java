package com.bigdata.hadoop.reversed.index1;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class GatherMapper extends Mapper<LongWritable,Text, WordBean,WordBean> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        WordBean bean = WordBean.parse(line);
        context.write(bean,bean);
    }
}
