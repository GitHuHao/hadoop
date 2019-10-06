package com.bigdata.hadoop.bean.globalsort;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;


public class GlobalSortDriver {
    /**
     * 全排序
     * map 以 bean 为 key 输出
     * bean 实现 WritableComparable 接口，序列化同时，还会进行排序
     * reduce 收集到后直接输出，迭代器中只有一个对象
     * mapper reducer 中日志会记录到 yarn
     * driver 中日志 直接打印在控制台
     *
     */


    static class SortMapper extends Mapper<LongWritable, Text,FlowBean,Text> {
        private Log logger = LogFactory.getLog(SortMapper.class);
        private Text k = new Text();
        private FlowBean bean = new FlowBean();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            logger.info(line);
            String[] data = line.split("\t");
            String phone = data[1];
            k.set(phone);
            long up = Long.parseLong(data[data.length-3]);
            long down = Long.parseLong(data[data.length-2]);
            bean.setUp(up);
            bean.setDown(down);
            bean.setSum(up + down);
            // 以 bean 为 key 执行全排序
            context.write(bean,k);
        }
    }

    static class SortReducer extends Reducer<FlowBean,Text,Text,FlowBean> {
        private Log logger = LogFactory.getLog(SortReducer.class);

        @Override
        protected void reduce(FlowBean key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            // values 只有一条
            logger.info(key.toString());
            context.write(values.iterator().next(),key);
        }
    }

    private static final Log logger = LogFactory.getLog(GlobalSortDriver.class);

    public static void main(String[] args) throws Exception{
        if(args.length==0){
//            args = new String[]{"hdfs-practice/data/phone/in","hdfs-practice/data/phone/out"};
            args = new String[]{"/apps/mr/sort/in","/apps/mr/sort/out"};
        }

//        Runtime runtime = Runtime.getRuntime();
//        Process process = runtime.exec(String.format("rm -rf %s", args[1]));
//        int code = process.waitFor();
//        if(code!=0){
//            System.out.println(String.format("delete %s failed.",args[1]));
//        }

        Configuration conf = new Configuration();

        FileSystem fs = FileSystem.get(new URI("hdfs://hadoop01:9000"),conf);

        if(fs.exists(new Path(args[1]))){
            fs.delete(new Path(args[1]),true);
            logger.info(String.format("delete %s",args[1]));
        }

        Job job = Job.getInstance(conf);

        job.setJarByClass(GlobalSortDriver.class);

        job.setMapperClass(SortMapper.class);
        job.setReducerClass(SortReducer.class);

        job.setMapOutputKeyClass(FlowBean.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        boolean completion = job.waitForCompletion(true);
        int code = completion?0:1;
        logger.info(String.format("exit %s",code));
        System.exit(code);
    }



}
