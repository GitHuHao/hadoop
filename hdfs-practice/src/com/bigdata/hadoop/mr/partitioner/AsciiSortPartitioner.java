package com.bigdata.hadoop.mr.partitioner;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class AsciiSortPartitioner extends Partitioner<Text, IntWritable> {

    @Override
    public int getPartition(Text text, IntWritable intWritable, int i) {
        // 单词首字母
        int first = (int) text.toString().charAt(0);
        if(first%2==0){
            // 偶数分区
            return 0;
        }else{
            // 奇数分
            return 1;
        }
    }
}
