package com.bigdata.hadoop.compress.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WCDriver {
    /**
     * map 端开启压缩减少 shuffle 阶段数据传输量
     * 输出文件开启压缩，可以节省存储，方便数据中转
     * 使用 hdfs dfs -text xxx 可查看压缩文件
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception{
        args = new String[]{"hdfs-practice/data/wordcount/in","hdfs-practice/data/wordcount/out"};

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(String.format("rm -rf %s", args[1]));
        process.waitFor();

        Configuration conf = new Configuration();

        // 开启map端输出压缩
        conf.setBoolean("mapreduce.map.output.compress", true);
        // 设置map端输出压缩方式
        conf.setClass("mapreduce.map.output.compress.codec", BZip2Codec.class, CompressionCodec.class);

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

        // 开启输出 压缩
        FileOutputFormat.setCompressOutput(job,true);
        FileOutputFormat.setOutputCompressorClass(job, BZip2Codec.class);
//        FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
//        FileOutputFormat.setOutputCompressorClass(job, Lz4Codec.class);
//        FileOutputFormat.setOutputCompressorClass(job, DefaultCodec.class);

        boolean completion = job.waitForCompletion(true);
        System.exit(completion?0:1);

    }



}
