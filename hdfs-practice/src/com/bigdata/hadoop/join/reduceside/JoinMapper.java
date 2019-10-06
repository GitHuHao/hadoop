package com.bigdata.hadoop.join.reduceside;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class JoinMapper extends Mapper<LongWritable, Text,Text, TableBean> {

    private Text k = new Text();
    private TableBean bean = null;

    private Log logger = LogFactory.getLog(JoinMapper.class);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] data = value.toString().split("\t");
        bean = new TableBean();

        int type = 1;

        if(type==0) {
            // 方案 1：基于内容识别数据类型
            if(data.length==3){
                bean.setOrderId(data[0]);
                bean.setProductId(data[1]);
                bean.setCount(Integer.parseInt(data[2]));
                logger.info(String.format("write order: %s",bean));
            }else{
                bean.setProductId(data[0]);
                bean.setName(data[1]);
                logger.info(String.format("write product: %s",bean));
            }
        }else {
            // 方案 2：基于文件名称识别数据类型
            FileSplit split = (FileSplit) context.getInputSplit();
            String name = split.getPath().getName();

            if (name.startsWith("order.txt")) {
                bean.setOrderId(data[0]);
                bean.setProductId(data[1]);
                bean.setCount(Integer.parseInt(data[2]));
                logger.info(String.format("write order: %s", bean));
            } else {
                bean.setProductId(data[0]);
                bean.setName(data[1]);
                logger.info(String.format("write product: %s", bean));
            }

        }
        k.set(bean.getProductId());

        context.write(k,bean);
    }
}
