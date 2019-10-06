package com.bigdata.hadoop.reversed.index1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class GatherDriver {
    /**
     *  第 2 个 MR：按从大到小顺序输出倒排索引格式文件
     * spark  data3.txt-->3  data2.txt-->1  data1.txt-->1
     * hive  data1.txt-->4  data3.txt-->3  data2.txt-->1
     * hello  data3.txt-->3  data1.txt-->2  data2.txt-->1
     * hadoop  data1.txt-->3  data3.txt-->2  data2.txt-->1
     *
     * WordBean 中 只基于 word 排序，reduce 内对收集到的 bean 按 count 排序，然后输出
     * 分区 取决于 WordBean 中 comparator()
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception{
        if(args.length==0){
            args = new String[]{"hdfs-practice/data/index/out/part-r-00000","hdfs-practice/data/index/out2"};
        }
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(String.format("rm -rf %s", args[1]));
        process.waitFor();

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(GatherDriver.class);
        job.setMapperClass(GatherMapper.class);
        job.setReducerClass(GatherReducer.class);

        job.setMapOutputKeyClass(WordBean.class);
        job.setMapOutputValueClass(WordBean.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        boolean completion = job.waitForCompletion(true);
        System.exit(completion?0:1);

    }




}
