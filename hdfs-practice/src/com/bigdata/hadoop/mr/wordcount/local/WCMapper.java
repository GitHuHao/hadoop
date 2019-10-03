package com.bigdata.hadoop.mr.wordcount.local;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class WCMapper extends Mapper<LongWritable,Text, Text, IntWritable> {
    /**
     * 输入
     *  LongWritable 起始偏移量
     *  Text 行文本
     * 输出
     *  Text 单词
     *  IntWritable 计数
     */

    // 变量复用
    private Text k = new Text();
    private IntWritable v = new IntWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 按行读取，以空格切分，然后写出
        String line = value.toString();
        String[] words = line.split(" ");
        for(String word:words){
            k.set(word);
            v.set(1);
            context.write(k,v);
        }
    }
}
