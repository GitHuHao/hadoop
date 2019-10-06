package com.bigdata.hadoop.join.mapside;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;



import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class JoinMapper extends Mapper<LongWritable, Text,TableBean, NullWritable> {

    private Map<String,String> products = new HashMap<>();
    private TableBean bean = new TableBean();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // 从缓存中读取文件，预加载到内存
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("hdfs-practice/data/join/in/product.txt")));
        String line = null;
        while(StringUtils.isNotEmpty(line = reader.readLine())){
            String[] data = line.split("\t");
            products.put(data[0],data[1]);
        }
        reader.close();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // map 端 join
        String[] data = value.toString().split("\t");
        bean.setOrderId(data[0]);
        bean.setProductId(data[1]);
        bean.setCount(Integer.parseInt(data[2]));
        bean.setProductName(products.get(data[1]));
        context.write(bean,NullWritable.get());
    }
}
