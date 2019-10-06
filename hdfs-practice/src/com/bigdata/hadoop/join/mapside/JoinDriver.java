package com.bigdata.hadoop.join.mapside;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.net.URI;

public class JoinDriver {

    public static void main(String[] args) throws Exception{
        if(args.length==0){
            args = new String[]{"hdfs-practice/data/join/in/order.txt","hdfs-practice/data/join/out"};
        }
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(String.format("rm -rf %s", args[1]));
        process.waitFor();

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(JoinDriver.class);

        job.setMapperClass(JoinMapper.class);

        job.setMapOutputKeyClass(TableBean.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setOutputKeyClass(TableBean.class);
        job.setOutputValueClass(NullWritable.class);

        // 注册缓存（预加载小表，直接实现 map 端 join，不需要 reduce）
        job.addCacheFile(new URI("hdfs-practice/data/join/in/product.txt"));
        job.setNumReduceTasks(0);

        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        boolean completion = job.waitForCompletion(true);
        System.exit(completion?0:1);

    }

}
