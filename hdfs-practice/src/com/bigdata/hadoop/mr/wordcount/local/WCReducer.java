package com.bigdata.hadoop.mr.wordcount.local;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class WCReducer extends Reducer<Text, IntWritable, Text,IntWritable> {
    /**
     * map task 输入
     *  Text 单词
     *  IntWritable 计数
     * reduce task 输出
     *  Text 单词
     *  IntWritable 累加和
     */

    private IntWritable value = new IntWritable();

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        // 相同 key 以组为单位读取，然后汇总
        int count = 0;
        Iterator<IntWritable> iterator = values.iterator();
        while(iterator.hasNext()){
            count += iterator.next().get();
        }
        value.set(count);
        context.write(key,value);
    }
}
