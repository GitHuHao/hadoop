package com.bigdata.hadoop.mr.split;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class WCMapper extends Mapper<LongWritable, Text,Text, IntWritable> {

    private Text k = new Text();
    private IntWritable v = new IntWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] words = value.toString().split(" ");
        for(String word:words){
            k.set(word);
            v.set(1);
            context.write(k,v);
        }
    }
}
