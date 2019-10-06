package com.bigdata.hadoop.mr.split;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WCDriver {

    public static void main(String[] args) throws Exception{

        if(args.length==0){
            args = new String[]{"hdfs-practice/data/wordcount/in","hdfs-practice/data/wordcount/out"};
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(String.format("rm -rf %s", args[1]));
        int code = process.waitFor();
        if(code!=0){
            System.out.println(String.format("delete %s failed.",args[1]));
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(WCDriver.class);

        job.setMapperClass(WCMapper.class);
        job.setReducerClass(WCReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 进入 mapper 前小文件合并，减少切片数，提高 mapper 节点使用率，否则一个文件对应一个 mapper
        job.setInputFormatClass(CombineTextInputFormat.class);
        // 从小到大合并
        CombineTextInputFormat.setMaxInputSplitSize(job,4*1024*1024); // 4m
        CombineTextInputFormat.setMinInputSplitSize(job,2*1024*1024); // 2m

        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        boolean completion = job.waitForCompletion(true);
        System.exit(completion?0:1);
    }



}
