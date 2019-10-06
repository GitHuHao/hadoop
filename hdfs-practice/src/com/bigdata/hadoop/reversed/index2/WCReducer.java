package com.bigdata.hadoop.reversed.index2;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class WCReducer extends Reducer<Text, IntWritable, WordBean,NullWritable> {

    private WordBean k = new WordBean();

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        String[] data = key.toString().split("-->");
        String word = data[0];
        String path = data[1];

        int count =0;
        for(IntWritable value:values){
            count += value.get();
        }

        k.setWord(word);
        k.setPath(path);
        k.setCount(count);

        context.write(k,NullWritable.get());

    }
}
