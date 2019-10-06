package com.bigdata.hadoop.reversed.index2;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GatherReducer extends Reducer<WordBean, WordBean, Text, NullWritable> {

    private Log logger = LogFactory.getLog(GatherReducer.class);

    private Text k = new Text();

    @Override
    protected void reduce(WordBean key, Iterable<WordBean> values, Context context) throws IOException, InterruptedException {
        List<String> list = new ArrayList<>();
        list.add(String.format("%s",key.getWord()));

//        List<WordBean> beans = new ArrayList<>();
//
//        for(WordBean value:values){
//            WordBean bean = new WordBean();
//            try {
//                BeanUtils.copyProperties(bean,value);
//            } catch (Exception e)  {
//                e.printStackTrace();
//            }
//            beans.add(bean);
//        }
//        Collections.sort(beans, new Comparator<WordBean>() {
//            @Override
//            public int compare(WordBean o1, WordBean o2) {
//                return o2.getCount()-o1.getCount();
//            }
//        });
//
//        for(WordBean value:beans){
//            list.add(String.format(" %s-->%s",value.getPath(),value.getCount()));
//        }

        for(WordBean value:values){
            list.add(String.format(" %s-->%s",value.getPath(),value.getCount()));
        }

        String info = StringUtils.join(list," ");
        k.set(info);
        context.write(k,NullWritable.get());
    }
}
