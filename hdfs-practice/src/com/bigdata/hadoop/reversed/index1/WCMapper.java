package com.bigdata.hadoop.reversed.index1;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class WCMapper extends Mapper<LongWritable, Text,Text, IntWritable> {

    private Text k = new Text();
    private IntWritable v = new IntWritable(1);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        FileSplit split = (FileSplit)context.getInputSplit();
        String path = split.getPath().getName();
        String[] words = value.toString().split(" ");
        for(String word:words){
            k.set(String.format("%s-->%s",word,path));
            context.write(k,v);
        }
    }
}
