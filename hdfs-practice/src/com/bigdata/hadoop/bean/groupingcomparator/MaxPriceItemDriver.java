package com.bigdata.hadoop.bean.groupingcomparator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class MaxPriceItemDriver {
    /**
     * 筛选各订单中单价最高项目（分区，取各分区最大值）
     * map (bean,null) -> paraitioned by id -> map side sorted by id and price -> grouping comparator by id (keyed by id) -> reduce (receive keyed group flush max price item)
     * OrderBeanPartitioner：决定 map 端分组
     * GroupingComparator：决定 reduce 端接收一组 key的依据 (继承 WriteableComparator)
     * OrderBean：继承 WriteableComparable，排序中基于 id price 二次排序
     */

    static class OrderBeanMapper extends Mapper<LongWritable, Text,OrderBean, NullWritable>{
        private OrderBean bean = new OrderBean();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] data = value.toString().split("\t");
            String id = data[0];
            float price = Float.parseFloat(data[2]);

            bean.setId(id);
            bean.setPrice(price);

            context.write(bean,NullWritable.get());
        }
    }

    static class OrderBeanReducer extends Reducer<OrderBean,NullWritable,OrderBean,NullWritable> {
        @Override
        protected void reduce(OrderBean key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
            context.write(key,NullWritable.get());
        }
    }

    static class GroupingComparator extends WritableComparator {
        public GroupingComparator() {
            super(OrderBean.class,true);
        }

        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            OrderBean a1 = (OrderBean) a;
            OrderBean b1 = (OrderBean) b;
            return a1.getId().compareTo(b1.getId());
        }
    }

    static class OrderBeanPartitioner extends Partitioner<OrderBean,NullWritable>{
        @Override
        public int getPartition(OrderBean orderBean, NullWritable nullWritable, int i) {
            return (orderBean.getId().hashCode() & Integer.MAX_VALUE) % i;
        }
    }

    public static void main(String[] args) throws Exception {
        if(args.length==0){
            args = new String[]{"hdfs-practice/data/order/in","hdfs-practice/data/order/out"};
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(String.format("rm -rf %s", args[1]));
        process.waitFor();

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(MaxPriceItemDriver.class);

        job.setMapperClass(OrderBeanMapper.class);
        job.setReducerClass(OrderBeanReducer.class);

        job.setMapOutputKeyClass(OrderBean.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setOutputKeyClass(OrderBean.class);
        job.setOutputValueClass(NullWritable.class);

        job.setGroupingComparatorClass(GroupingComparator.class);
        job.setPartitionerClass(OrderBeanPartitioner.class);

        job.setNumReduceTasks(3);

        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        boolean completion = job.waitForCompletion(true);

        System.exit(completion?0:1);

    }



}
