package com.bigdata.hadoop.friend.shared;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MeMeFriendMapper extends Mapper<LongWritable,Text, Text,Text> {

    private Text k = new Text();
    private Text v = new Text();


    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] data = value.toString().split(":");
        String friend = data[0];
        List<String> mes = Arrays.asList(data[1].split(","));
        Collections.sort(mes, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        for(int i=0;i<mes.size();i++){
            String frist = mes.get(i);
            for(int j=i+1;j<mes.size();j++){
                String second = mes.get(j);
                k.set(String.format("%s&%s",frist,second));
                v.set(friend);
                context.write(k,v);
            }
        }

    }
}
