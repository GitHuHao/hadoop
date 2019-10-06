package com.bigdata.hadoop.filter.output;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;

public class ContentFilterWriter extends RecordWriter<Text, NullWritable> {

    private FSDataOutputStream warnOut = null;
    private FSDataOutputStream infoOut = null;

    public ContentFilterWriter(TaskAttemptContext context) {
        FileSystem fs = null;
        try {

            Configuration conf = context.getConfiguration();
            // 获取输出目录
            String outDir = conf.get("mapreduce.output.fileoutputformat.outputdir");
            fs = FileSystem.get(conf);
            // 基于关键词分流
            warnOut = fs.create(new Path(String.format("%s/warn.txt",outDir)));
            infoOut =  fs.create(new Path(String.format("%s/info.txt",outDir)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(Text text, NullWritable nullWritable) throws IOException, InterruptedException {
        String data = String.format("%s\n",text.toString());
        if(data.contains("INFO")){
            infoOut.write(data.getBytes());
        }
        if(data.contains("WARN")){
            warnOut.write(data.getBytes());
        }
    }

    @Override
    public void close(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        if(warnOut!=null){
            warnOut.close();
        }
        if(infoOut!=null){
            infoOut.close();
        }
    }
}
