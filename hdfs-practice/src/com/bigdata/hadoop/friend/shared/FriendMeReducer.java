package com.bigdata.hadoop.friend.shared;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.hsqldb.lib.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FriendMeReducer extends Reducer<Text, Text, Text,NullWritable> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        List<String> list = new ArrayList<>();

        for(Text value:values){
            list.add(value.toString());
        }
        key.set(String.format("%s:%s",key.toString(),StringUtils.join(list,",")));
        context.write(key,NullWritable.get());
    }
}
