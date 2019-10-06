package com.bigdata.hadoop.reversed.index2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class WCDriver {
    /**
     * 第一个 MR：统计文件内 wordcount
     * hadoop	data1.txt	3
     * hadoop	data2.txt	1
     * hadoop	data3.txt	2
     * hello	data1.txt	2
     * hello	data2.txt	1
     * hello	data3.txt	3
     * hive	data1.txt	4
     * hive	data2.txt	1
     * hive	data3.txt	3
     * spark	data1.txt	1
     * spark	data2.txt	1
     * spark	data3.txt	3
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception{
        if(args.length==0){
            args = new String[]{"hdfs-practice/data/index/in","hdfs-practice/data/index/out"};
        }
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(String.format("rm -rf %s", args[1]));
        process.waitFor();

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(WCDriver.class);

        job.setMapperClass(WCMapper.class);
        job.setReducerClass(WCReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(WordBean.class);
        job.setOutputValueClass(NullWritable.class);

        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        boolean completion = job.waitForCompletion(true);
        System.exit(completion?0:1);
    }


}
