package com.bigdata.hadoop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.net.DNSToSwitchMapping;

import java.util.ArrayList;
import java.util.List;

public class AutoTack implements DNSToSwitchMapping {

    private static final Log logger = LogFactory.getLog(AutoTack.class);

    @Override
    public List<String> resolve(List<String> names) {
        List<String> tacks = new ArrayList<>();
        int mark = 0;
        String tackUrl = null;
        for(String name:names){
            if(name.equals("hadoop01") || name.equals("hadoop02")){
                tackUrl = String.format("/tack1/%s",name);
            }else if(name.equals("hadoop03") || name.equals("hadoop04")){
                tackUrl = String.format("/tack2/%s",name);
            }else{
                tackUrl = String.format("/tack3/%s",name);
            }
            tacks.add(tackUrl);
        }

        logger.info(String.format("add %s",tacks));
        return tacks;
    }

    @Override
    public void reloadCachedMappings() {

    }

    @Override
    public void reloadCachedMappings(List<String> names) {

    }

}
