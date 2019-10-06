package com.bigdata.hadoop.bean.partitionsort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class PartitionSortDriver {
    /**
     * 分区排序
     * 一个输入文件
     * 对 map 输出的 key 进行排序
     * 依据 map 输出的 value 进行分区
     */

    static class SortMapper extends Mapper<LongWritable, Text,FlowBean,Text> {

        private FlowBean k = new FlowBean();
        private Text v = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] data = value.toString().split("\t");
            String phone = data[1];
            v.set(phone);

            int up = Integer.parseInt(data[data.length-3]);
            int down = Integer.parseInt(data[data.length-2]);

            k.setUp(up);
            k.setDown(down);
            k.setSum(up + down);

            context.write(k,v);

        }
    }

    static class SortPartitioner extends Partitioner<FlowBean,Text> {
        @Override
        public int getPartition(FlowBean flowBean, Text text, int i) {
            String phone = text.toString();
            int partitionNum = 4;

            if(phone.startsWith("136")){
                partitionNum = 0;
            }else if (phone.startsWith("137")){
                partitionNum = 1;
            }else if (phone.startsWith("138")){
                partitionNum = 2;
            }else if (phone.startsWith("139")){
                partitionNum = 3;
            }
            return partitionNum;
        }
    }

    static class SortReducer extends Reducer<FlowBean,Text,Text,FlowBean> {
        // reduce 每次调用接收一个 bean，需要收集一组 bean 时，可以定义 GroupingComparator 将部分属性组合作为 bean 分组标准
        @Override
        protected void reduce(FlowBean key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            context.write(values.iterator().next(),key);
        }
    }

    public static void main(String[] args) throws Exception{
        if(args.length==0){
            args = new String[]{"hdfs-practice/data/phone/in","hdfs-practice/data/phone/out"};
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(String.format("rm -rf %s", args[1]));
        process.waitFor();

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(PartitionSortDriver.class);

        job.setMapperClass(SortMapper.class);
        job.setReducerClass(SortReducer.class);

        job.setMapOutputKeyClass(FlowBean.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        job.setPartitionerClass(SortPartitioner.class);
        job.setNumReduceTasks(5);

        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        boolean completion = job.waitForCompletion(true);
        System.exit(completion?0:1);

    }


}
