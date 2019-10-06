package com.bigdata.hadoop.mr.combiner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CombinerDriver1 {
    /**
     *  预聚合-方案 1
     *  定义与 reducer 相同逻辑的 combiner类
     *  Combine input records=7
     * 	Combine output records=4
     */

    public static void main(String[] args) throws Exception{

        if(args.length==0){
            args = new String[]{"hdfs-practice/data/wordcount/in/data.txt","hdfs-practice/data/wordcount/out"};
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(String.format("rm -rf %s", args[1]));
        int code = process.waitFor();
        if(code!=0){
            System.out.println(String.format("delete %s failed.",args[1]));
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(CombinerDriver1.class);


        job.setMapperClass(CombinerMapper.class);
        job.setReducerClass(CombinerReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setCombinerClass(Combiner.class);

        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        boolean completion = job.waitForCompletion(true);
        System.exit(completion?0:1);

    }

}
