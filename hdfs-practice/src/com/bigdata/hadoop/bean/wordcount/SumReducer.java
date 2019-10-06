package com.bigdata.hadoop.bean.wordcount;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class SumReducer extends Reducer<Text,FlowBean,Text,FlowBean> {

    private FlowBean bean = new FlowBean();

    @Override
    protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {
        Iterator<FlowBean> iterator = values.iterator();
        long up = 0;
        long down = 0;
        while (iterator.hasNext()){
            FlowBean flowBean = iterator.next();
            up += flowBean.getUp();
            down += flowBean.getDown();
        }
        bean.setUp(up);
        bean.setDown(down);
        bean.setSum(up + down);
        context.write(key,bean);
    }
}
