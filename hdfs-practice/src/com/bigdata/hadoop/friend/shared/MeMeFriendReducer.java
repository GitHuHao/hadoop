package com.bigdata.hadoop.friend.shared;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class MeMeFriendReducer extends Reducer<Text,Text,Text, NullWritable> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        StringBuilder builder = new StringBuilder();
        builder.append(key.toString());
        for(Text value:values){
            builder.append(String.format(" %s",value.toString()));
        }
        key.set(builder.toString());
        context.write(key,NullWritable.get());
    }
}
