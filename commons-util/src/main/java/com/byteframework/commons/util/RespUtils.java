package com.byteframework.commons.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RespUtils {

    private static final Log log = LogFactory.getLog(RespUtils.class);

    //将对象列表转化为前端所需的数组结构
    @SuppressWarnings("unchecked")
    public static Map<String, Object> convertToArrays(List<?> list, String[][] fields) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        for (int i = 0; i < fields.length; i++) {
            String mapKey = fields[i][0] + "s";
            if (fields[i].length == 2 && StringUtils.isNotBlank(fields[i][1])) {
                mapKey = fields[i][1];
            }
            List<Object> currList = (List<Object>) result.get(mapKey);
            if (currList == null) {
                currList = new ArrayList<Object>();
                result.put(mapKey, currList);
            }
            for (Object obj : list) {
                log.info("convertToArrays: " + obj.getClass().getName() + ", field: " + fields[i][0]);
                Object val = ReflectionUtils.getFieldValue(obj, fields[i][0]);
                if (val != null) {
                    if (val instanceof Long) {
                        currList.add((Long) val);
                    } else if (val instanceof Double) {
                        currList.add((Double) val);
                    } else if (val instanceof Integer) {
                        currList.add((Integer) val);
                    } else if (val instanceof Float) {
                        currList.add((Float) val);
                    } else {
                        currList.add(val);
                    }
                } else {
                    currList.add(val);
                }
            }
        }
        return result;
    }

}
