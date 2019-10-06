package com.bigdata.hadoop.friend.shared;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FriendMeMapper extends Mapper<LongWritable, Text,Text, Text> {

    private Text k = new Text();
    private Text v = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // me:friend,friend,... -> friend me
        String line = value.toString();
        String[] data = line.split(":");
        String me = data[0];
        String[] friends = data[1].split(",");
        v.set(me);
        for(String friend:friends){
            k.set(friend);
            context.write(k,v);
        }
    }
}
