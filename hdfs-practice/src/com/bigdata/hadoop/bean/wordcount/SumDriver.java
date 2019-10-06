package com.bigdata.hadoop.bean.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SumDriver {

    public static void main(String[] args) throws Exception{
        if(args.length==0){
            args = new String[]{"hdfs-practice/data/phone/in","hdfs-practice/data/phone/out"};
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(String.format("rm -rf %s", args[1]));
        int code = process.waitFor();
        if(code!=0){
            System.out.println(String.format("delete %s failed.",args[1]));
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(SumDriver.class);

        job.setMapperClass(SumMapper.class);
        job.setReducerClass(SumReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        boolean completion = job.waitForCompletion(true);
        System.exit(completion?0:1);

    }

}
