package com.bigdata.hadoop.mr.partitioner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AsciiSortDriver {

    public static void main(String[] args) throws Exception{
        if(args.length==0){
            args = new String[]{"hdfs-practice/data/wordcount/in/data.txt","hdfs-practice/data/wordcount/out"};
        }

        // 清空输出目录
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(String.format("rm -rf %s", args[1]));
        int code = process.waitFor();
        if(code!=0){
            System.out.println(String.format("delete %s failed",args[1]));
        }

        // 1.创建配置和任务
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        // 2.注册驱动类
        job.setJarByClass(AsciiSortDriver.class);

        // 3. 注册 mapper reducer 实现类
        job.setMapperClass(AsciiSortMapper.class);
        job.setReducerClass(AsciiSortReducer.class);

        // 4.注册自定义分区类（奇偶分区），设置分区
        job.setPartitionerClass(AsciiSortPartitioner.class);
        job.setNumReduceTasks(2);

        // 5.设置 mapper 输出 kv
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // 6.设置 最终结果输出 kv （一个分区一个文件）
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 7.设置输入目录 输出目录
        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        // 8.等待程序运行结束，然后退出
        boolean completion = job.waitForCompletion(true);
        System.exit(completion?0:1);
    }


}
