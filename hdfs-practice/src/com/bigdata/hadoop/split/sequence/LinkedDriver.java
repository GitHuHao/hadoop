package com.bigdata.hadoop.split.sequence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

public class LinkedDriver {

    private static final Log logger = LogFactory.getLog(LinkedDriver.class);

    public static boolean job1(String[] args,Configuration conf) throws Exception {
        Job job = Job.getInstance(conf);

        job.setJarByClass(SequenceOutputDriver.class);

        job.setMapperClass(GatherMapper.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(BytesWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(BytesWritable.class);

        job.setInputFormatClass(WholeFileInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));
        boolean completion = job.waitForCompletion(true);

        logger.info(String.format("job1 run over: %s",completion));

        return completion;
    }

    private static boolean job2(String[] args,Configuration conf) throws Exception {
        Job job = Job.getInstance(conf);

        job.setJarByClass(WCDriver.class);

        job.setMapperClass(WCMapper.class);
        job.setReducerClass(WCReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setInputFormatClass(SequenceFileInputFormat.class);

        FileInputFormat.setInputPaths(job,new Path(args[1]));
        FileOutputFormat.setOutputPath(job,new Path(args[2]));

        boolean completion = job.waitForCompletion(true);

        logger.info(String.format("job2 run over: %s",completion));

        return completion;
    }



    public static void main(String[] args) throws Exception{
        if(args.length==0){
            args = new String[]{"hdfs-practice/data/wordcount/in","hdfs-practice/data/wordcount/out","hdfs-practice/data/wordcount/out2"};
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(String.format("rm -rf %s", args[1]));
        process.waitFor();

        Configuration conf = new Configuration();
        boolean success1 = false;
        boolean success2 = false;

        success1 = job1(args,conf);

        if(success1){
            process = runtime.exec(String.format("rm -rf %s", args[2]));
            process.waitFor();
            success2 = job2(args,conf);
        }

        System.exit(success1 && success2?0:1);
    }


}
