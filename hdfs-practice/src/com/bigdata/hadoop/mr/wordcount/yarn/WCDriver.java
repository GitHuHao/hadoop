package com.bigdata.hadoop.mr.wordcount.yarn;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.net.URI;


public class WCDriver{

    public static void main(String[] args) throws Exception{
        if(args.length==0){
            args = new String[]{"/apps/mr/wc/in/","/apps/mr/wc/out"};
        }

        Configuration conf = new Configuration();
        // 清空输出文件目录
        FileSystem fs = FileSystem.get(new URI("hdfs://hadoop01:9000"),conf,"admin");
        fs.delete(new Path(args[1]),true);
        // MR 提交者
        System.setProperty("HADOOP_USER_NAME","admin");
        // 本地 hadoop 根目录
        conf.set("hadoop.tmp.dir","/Users/huhao/softwares/idea_proj/hadoop/home");
        // 打包 mr jar 目录
        conf.set("mapred.jar","/Users/huhao/softwares/idea_proj/hadoop/out/artifacts/hdfs_practice_jar/hdfs-practice.jar");
        // 文件系统
        conf.set("fs.defaultFS","hdfs://hadoop01:9000");
        conf.set("mapreduce.framework.name","yarn");
        conf.set("yarn.nodemanager.aux-services","mapreduce_shuffle");
        conf.set("yarn.resourcemanager.hostname","hadoop03");
        conf.set("mapreduce.app-submission.cross-platform","true");

        Job job = Job.getInstance(conf);

        job.setJarByClass(WCDriver.class);

        job.setMapperClass(WCMapper.class);
        job.setReducerClass(WCReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        boolean completion = job.waitForCompletion(true);
        System.exit(completion?0:1);

    }

}
