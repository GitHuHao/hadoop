package com.bigdata.hadoop.filter.logclean;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class LogMapper extends Mapper<LongWritable, Text,Text, NullWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        boolean isOk = parseLog(line,context);
        if(!isOk){
            return;
        }
        context.write(value,NullWritable.get());
    }

    private boolean parseLog(String line,Context context){
        String[] data = line.split(" ");
        boolean isOk = false;
        // 字段数少于 11 的删除
        if(data.length>11){
            context.getCounter("lineNum","effective").increment(1);
            isOk = true;
        }else{
            context.getCounter("lineNum","ineffective").increment(1);
        }
        return isOk;
    }

}
