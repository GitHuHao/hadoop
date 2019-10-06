package com.bigdata.hadoop.filter.logclean2;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class LogMapper extends Mapper<LongWritable,Text,Text, NullWritable> {

    private Text k = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        LogBean bean = parseLog(line, context);

        if(!bean.isValid()){
            return;
        }

        k.set(bean.toString());
        context.write(k,NullWritable.get());
    }

    private LogBean parseLog(String line,Context context){
        String[] data = line.split(" ");
        LogBean bean = new LogBean();
        if(data.length>11){
            bean.setRemoteAddr(data[0]);
            bean.setRemoteUser(data[1]);
            bean.setTimeLocal(data[3].substring(1));
            bean.setRequest(data[6]);
            bean.setStatus(data[8]);
            bean.setBody(data[9]);
            bean.setHttpRefer(data[10]);

            if(data.length>12){
                bean.setUserAgent(String.format("%s %s",data[11],data[12]));
            }else{
                bean.setUserAgent(data[11]);
            }

            if(Integer.parseInt(bean.getStatus())>=400){
                bean.setValid(false);
            }else{
                bean.setValid(true);
            }

            context.getCounter("lineNum","valid").increment(1);
        }else{
            context.getCounter("lineNum","invalid").increment(1);
        }
        return bean;
    }



}
