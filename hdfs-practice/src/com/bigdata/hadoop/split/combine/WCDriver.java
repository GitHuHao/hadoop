package com.bigdata.hadoop.split.combine;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WCDriver {
    /**
     * 1.超过 128M 文件，按 128M 拆分成若干切片，每个 map 处理一个切片
     * 2.小于 128M 文件，整体作为一个切片，交给一个 map 处理
     * 3.小文件过多，每个文件作为一个切片，交给 map 处理，产生 map 过多，每个 map task 处理数据很有限，浪费计算资源
     * 4.mr 默认使用 TextFileInputFormat 按行读取，自定义使用 CombineTextInputFormat 时，通过设置分片上下界，可按从小到大顺序合并小文件，将其整体作为分片，交给一个 map 处理，压缩 map 个数，提高计算效率
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception{
        if(args.length==0){
            args = new String[]{"hdfs-practice/data/wordcount/in","hdfs-practice/data/wordcount/out"};
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

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setInputFormatClass(CombineTextInputFormat.class);
        FileInputFormat.setMaxInputSplitSize(job,4*1024*1024);
        FileInputFormat.setMinInputSplitSize(job,2*1024*1024);

//  合并分片前      2019-10-05 22:30:51,427 INFO [org.apache.hadoop.mapreduce.JobSubmitter] - number of splits:3
//  合并分片后      2019-10-05 22:24:35,284 INFO [org.apache.hadoop.mapreduce.JobSubmitter] - number of splits:1



        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        boolean completion = job.waitForCompletion(true);

        System.exit(completion?0:1);

    }


}
