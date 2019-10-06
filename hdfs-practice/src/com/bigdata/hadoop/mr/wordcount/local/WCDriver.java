package com.bigdata.hadoop.mr.wordcount.local;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WCDriver {


    public static void main(String[] args) throws Exception{
        if(args.length==0){
            args = new String[]{"hdfs-practice/data/wordcount/in","hdfs-practice/data/wordcount/out"};
        }

        Path input = new Path(args[0]);
        Path output = new Path(args[1]);

        Runtime runtime = Runtime.getRuntime();
        Process pro = runtime.exec(String.format("rm -rf %s",args[1]));
        int status = pro.waitFor();
        if (status != 0)
        {
            System.out.println("Failed to call shell's command ");
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

        FileInputFormat.setInputPaths(job,input);
        FileOutputFormat.setOutputPath(job,output);

        boolean completion = job.waitForCompletion(true);
        System.exit(completion?0:1);

    }


}
