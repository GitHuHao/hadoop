package com.bigdata.hadoop.join.reduceside;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JoinReducer extends Reducer<Text, TableBean,TableBean,NullWritable> {

    private Log logger = LogFactory.getLog(JoinReducer.class);

    @Override
    protected void reduce(Text key, Iterable<TableBean> values, Context context) throws IOException, InterruptedException {
        TableBean product = new TableBean();
        List<TableBean> orders = new ArrayList<>();

        for(TableBean bean:values){
            logger.info(String.format("bean:%s",System.identityHashCode(bean)));
            /**  遍历迭代器对象时，对象一致，因此下面必须使用属性拷贝否则会出现覆盖
             * 2019-10-05 21:08:37,445 INFO [com.bigdata.hadoop.join.reduceside.JoinReducer] - bean:705142893
             * 2019-10-05 21:08:37,474 INFO [com.bigdata.hadoop.join.reduceside.JoinReducer] - bean:705142893
             * 2019-10-05 21:08:37,474 INFO [com.bigdata.hadoop.join.reduceside.JoinReducer] - bean:705142893
             * 2019-10-05 21:08:37,474 INFO [com.bigdata.hadoop.join.reduceside.JoinReducer] - bean:705142893
             * 2019-10-05 21:08:37,475 INFO [com.bigdata.hadoop.join.reduceside.JoinReducer] - bean:705142893
             * 2019-10-05 21:08:37,475 INFO [com.bigdata.hadoop.join.reduceside.JoinReducer] - bean:705142893
             * 2019-10-05 21:08:37,475 INFO [com.bigdata.hadoop.join.reduceside.JoinReducer] - bean:705142893
             * 2019-10-05 21:08:37,475 INFO [com.bigdata.hadoop.join.reduceside.JoinReducer] - bean:705142893
             * 2019-10-05 21:08:37,476 INFO [com.bigdata.hadoop.join.reduceside.JoinReducer] - bean:705142893
             */
            TableBean b = new TableBean();
            try {
                if(bean.getName().length()==0){
                    // 此处必须要使用拷贝
                    BeanUtils.copyProperties(b,bean);
                    orders.add(b);
                }else{
                    BeanUtils.copyProperties(product,bean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(TableBean order:orders){
            order.setName(product.getName());
            context.write(order,NullWritable.get());
        }

    }
}
