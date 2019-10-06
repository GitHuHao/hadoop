package com.bigdata.hadoop.split.sequence;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class WCMapper extends Mapper<Text, BytesWritable,Text, IntWritable> {

    private Text k = new Text();
    private IntWritable v = new IntWritable();

    @Override
    protected void map(Text key, BytesWritable value, Context context) throws IOException, InterruptedException {
        // 删除 BytesWritable 中<NUL> 类型多余字符
        value.setCapacity(value.getLength());
        byte[] bytes = value.getBytes();
        String[] lines = new String(bytes).split("\n");
        for(String line: lines){
            String[] words = line.split(" ");
            for(String word:words){
                k.set(word);
                v.set(1);
                context.write(k,v);
            }
        }

    }
}
