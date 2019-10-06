package com.bigdata.hadoop.bean.partitioner;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class ProvincePartitioner extends Partitioner<Text,FlowBean> {
    @Override
    public int getPartition(Text text, FlowBean flowBean, int i) {
        // 按手机号段分区
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
