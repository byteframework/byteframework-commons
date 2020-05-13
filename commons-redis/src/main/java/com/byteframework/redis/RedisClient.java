//  ==================================================================
//  文件名:RedisClient
//  简要功能描述:
//
//  Created by sa on 2019-08-06.
//  Copyright (c) sa. All rights reserved.
//  ==================================================================
package com.byteframework.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.byteframework.commons.config.ConfigUtils;
import com.byteframework.commons.domain.BaseEntity;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import java.util.*;

@Service
public class RedisClient {

    private Log _log = LogFactory.getLog(RedisClient.class);

    /**
     * 缓存redis服务器ip及port变量属性
     */
    private final String REDIS_SERVER_IP = "redis.server.ip";
    private final String REDIS_SERVER_PORT = "redis.server.port";
    private final String REDIS_SERVER_PASS = "redis.server.pass";
    /**
     * redis 缓冲池相关配置
     */
    private final String REDIS_POOL_ACTIVE = "redis.pool.maxActive";
    private final String REDIS_POOL_MAXIDLE = "redis.pool.maxIdle";
    private final String REDIS_POOL_MAXWAIT = "redis.pool.maxWait";
    private final String REDIS_POOL_TIMEOUT = "redis.pool.timeout";

    private JedisPool jedisPool = null;
    private String host;
    private int port;

    public RedisClient() throws Exception {
        this._init();
    }

    private void _init() throws Exception {
        // Redis服务器IP
        host = this.getProperty(REDIS_SERVER_IP, String.class, "localhost");
        // Redis的端口号
        port = this.getProperty(REDIS_SERVER_PORT, Integer.class, 6379);
        // Redis的密码
        String pwd = this.getProperty(REDIS_SERVER_PASS, String.class, null);
        // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
        int maxActive = this.getProperty(REDIS_POOL_ACTIVE, Integer.class, 5);
        // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
        int maxIdle = this.getProperty(REDIS_POOL_MAXIDLE, Integer.class, 1);
        // 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
        int maxWait = this.getProperty(REDIS_POOL_MAXWAIT, Integer.class, 10000);
        //超时时长
        int timeout = this.getProperty(REDIS_POOL_TIMEOUT, Integer.class, 10000);
        try {
            _log.info("connect to redis server: " + host + ":" + port);
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(maxActive);
            config.setMaxIdle(maxIdle);
            config.setMaxWaitMillis(maxWait);
            config.setTestOnBorrow(true);
            if (StringUtils.isBlank(pwd)) {
                jedisPool = new JedisPool(config, host, port, timeout);
            } else {
                jedisPool = new JedisPool(config, host, port, timeout, pwd);
            }
        } catch (Exception e) {
            _log.error(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getProperty(String propertyName, Class<T> type, Object defautlValue) {
        String val = ConfigUtils.getProperty(propertyName);
        if (StringUtils.isBlank(val)) {
            if (defautlValue == null) {
                return null;
            }
            return (T) defautlValue;

        }
        if (type.equals(Integer.class)) {
            return (T) Integer.valueOf(val);
        }
        return (T) val;
    }


    /**
     * 获取Jedis实例
     *
     * @return
     */
    private synchronized Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                this.handleException(null);
                return null;
            }
        } catch (Exception e) {
            this.handleException(null);
            return null;
        }
    }

    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    private void returnResource(final Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }


    public void del(String key) {
        Jedis jedis = getJedis();
        try {
            jedis.del(getKey(key));
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
    }

    public boolean exists(String key) {
        Jedis jedis = getJedis();
        try {
            boolean exists = jedis.exists(getKey(key));
            _log.info("redis exists, key: " + key + ", exists: " + exists);
            return exists;
        } catch (Exception e) {
            _log.error(e.getMessage(), e);
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    public boolean expire(String key, int expSeconds) {
        Jedis jedis = getJedis();
        try {
            return jedis.expire(getKey(key), expSeconds) == 1;
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    /**
     * 将数据key value seconds 缓存进redis缓存中
     *
     * @param key
     * @param value
     * @param seconds
     */
    public void putString(String key, String value, int seconds) {
        Jedis jedis = getJedis();
        try {
            jedis.setex(getKey(key), seconds, value);
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 往redis加入字符串值
     *
     * @param key
     * @param value
     */
    public void putString(String key, String value) {
        Jedis jedis = getJedis();
        try {
            jedis.set(getKey(key), value);
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 根据指定的key获取redis缓存中的字符串数据value
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        Jedis jedis = getJedis();
        try {
            String value = null;
            value = jedis.get(getKey(key));
            return value;
        } finally {
            returnResource(jedis);
        }
    }


    public <T> void setToJson(String key, T obj) {
        if (obj == null) {
            return;
        }
        try {
            String json = JSONObject.toJSONString(obj);
            putString(key, json);
        } catch (Exception ex) {
            _log.error(ex.getMessage(), ex);
        }
    }

    public <T> void setToJson(String key, T obj, int seconds) {
        if (obj == null) {
            return;
        }
        try {
            String json = JSONObject.toJSONString(obj);
            putString(key, json, seconds);
        } catch (Exception ex) {
            _log.error(ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getFromJson(String key, Class<T> clazz) {
        try {
            String objJson = getString(key);
            _log.info("redis getFromJson ---  key: " + key + ", value: " + objJson);
            if (StringUtils.isBlank(objJson)) {
                return null;
            }
            if (clazz.equals(List.class)) {
                return (T) JSONObject.parseArray(objJson, Map.class);
            } else {
                return JSONObject.parseObject(objJson, clazz);
            }
        } catch (Exception e) {
            _log.error(e.getMessage(), e);
        }
        return null;
    }


    public <T> void hsetToJson(String key, String field, T obj) {
        if (obj == null) {
            return;
        }
        Jedis jedis = getJedis();
        try {
            String json = null;
            if (obj instanceof String) {
                json = obj.toString();
            } else {
                json = JSONObject.toJSONString(obj);
            }
            jedis.hset(getKey(key), field.toUpperCase(), json);
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T hgetFromJson(String key, String field, Class<T> clazz) {
        Jedis jedis = getJedis();
        try {
            String objJson = jedis.hget(getKey(key), field.toUpperCase());
            _log.info("redis hgetFromJson ---  key: " + key + ", field: " + field + ", value: " + objJson);
            if (StringUtils.isBlank(objJson)) {
                return null;
            }
            if (clazz.equals(String.class)) {
                return (T) objJson;
            } else {
                return JSONObject.parseObject(objJson, clazz);
            }
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> Map<String, T> hgetAllFromJson(String key, Class<T> clazz) {
        Jedis jedis = getJedis();
        try {
            Map<String, String> map = jedis.hgetAll(getKey(key));
            if (map == null || map.isEmpty()) {
                _log.info("redis hgetAllFromJson ---  key: " + key + ", values: null");
                return null;
            }
            _log.info("redis hgetAllFromJson ---  key: " + key + ", values: " + map);
            Map<String, T> result = new HashMap<String, T>();
            for (String mKey : map.keySet()) {
                String objJson = map.get(mKey);
                if (clazz.equals(String.class)) {
                    result.put(mKey, (T) objJson);
                } else {
                    result.put(mKey, JSONObject.parseObject(objJson, clazz));
                }
            }
            return result;
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public <T> Long hdel(String key, String field) {
        Jedis jedis = getJedis();
        try {
            return jedis.hdel(getKey(key), field);
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public void incr(String key) {
        Jedis jedis = getJedis();
        try {
            jedis.incr(this.getKey(key));
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 将list列表中的对象放入redis hash结构中
     *
     * @param key       redis key
     * @param list      要缓存的对象列表
     * @param fieldName 如果对象是map，此值为map中的一个key, 将此key对应的value做为hash的field
     */
    @SuppressWarnings("rawtypes")
    public <T> void hsetToJsonByTx(String key, List<T> list, String fieldName) {
        Jedis jedis = getJedis();
        try {
            _log.info("redis hsetToJsonByTx ---  key: " + key + ", values: " + list);
            String _key = getKey(key);
            //先删除原有的缓存数据
            jedis.del(_key);
            if (list == null || list.isEmpty()) {
                return;
            }
            //监听key
            jedis.watch(_key);
            //开启事务
            Transaction tx = jedis.multi();
            boolean isString = list.get(0).getClass().equals(String.class);
            //放数据到缓存
            String json = null, field = null;
            for (T obj : list) {
                if (isString) {
                    json = (String) obj;
                    field = json;
                } else {
                    json = JSONObject.toJSONString(obj);

                    if (StringUtils.isNotBlank(fieldName)) {
                        if (obj instanceof Map) {
                            field = ((Map) obj).get(fieldName).toString();
                        } else {
                            field = BeanUtils.getProperty(obj, fieldName);
                        }
                    } else {
                        if (BaseEntity.class.isAssignableFrom(obj.getClass())) {
                            field = key + "_" + ((BaseEntity) obj).getId();
                        } else {
                            field = UUID.randomUUID().toString();
                        }
                    }
                }
                tx.hset(_key, field.toUpperCase(), json);
            }
            //提交事务
            tx.exec();
        } catch (Exception e) {
            try {
                jedis.unwatch();
            } catch (Exception ex) {
            }
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
    }


    public <T> List<T> hvals(String key, Class<T> clazz) {
        Jedis jedis = getJedis();
        try {
            List<String> list = jedis.hvals(getKey(key));
            _log.info("redis hvals ---  key: " + key + ", values: " + list);
            return parseJsonToObj(list, clazz);
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public <T> List<T> hmget(String key, Class<T> clazz, String... fields) {
        Jedis jedis = getJedis();
        try {
            List<String> list = jedis.hmget(getKey(key), fields);
            _log.info("redis hmget ---  key: " + key + ", fields: " + fields + ", values: " + list);
            return parseJsonToObj(list, clazz);
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> parseJsonToObj(List<String> jsonStrs, Class<T> clazz) {
        if (CollectionUtils.isEmpty(jsonStrs)) {
            return null;
        }
        boolean blnStr = clazz.equals(String.class);
        List<T> resultList = new ArrayList<T>();
        for (String objJson : jsonStrs) {
            if (blnStr) {
                resultList.add((T) objJson);
            } else {
                resultList.add(JSONObject.parseObject(objJson, clazz));
            }
        }
        return resultList;
    }

    public <T> List<T> lgetAll(String key, Class<T> clazz) {
        Jedis jedis = getJedis();
        try {
            List<String> list = jedis.lrange(getKey(key), 0, -1);
            _log.info("redis lgetAll ---  key: " + key + ", values: " + list);
            return parseJsonToObj(list, clazz);
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public <T> void rpushAll(String key, List<T> objs) {
        Jedis jedis = getJedis();
        try {
            _log.info("redis rpushAll ---  key: " + key + ", values: " + objs);
            key = this.getKey(key);
            if (CollectionUtils.isEmpty(objs)) {
                jedis.del(key);
                return;
            }
            String[] vals = new String[objs.size()];
            for (int i = 0; i < objs.size(); i++) {
                vals[i] = JSON.toJSONString(objs.get(i));
            }
            jedis.rpush(key, vals);
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<String> keys(String key) {
        Jedis jedis = getJedis();
        try {
            Set<String> keys = jedis.keys(this.getKey(key));
            _log.info("redis keys ---  key: " + key + ", keys: " + keys);
            return keys;
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    /**
     * 将list列表中的对象放入redis hash结构中
     */
    @SuppressWarnings({"unchecked"})
    public void mhsetToJsonByTx(List<Map<String, Object>> datas) {
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }
        Jedis jedis = getJedis();
        try {
            _log.info("redis mhsetToJsonByTx ---  values: " + datas);

            List<String> keys = new ArrayList<String>();
            for (Map<String, Object> data : datas) {
                keys.add(getKey((String) data.get("key")));
            }
            //监听key
            jedis.watch(keys.toArray(new String[0]));
            //开启事务
            Transaction tx = jedis.multi();

            //存入新数据
            for (Map<String, Object> data : datas) {
                String key = getKey((String) data.get("key"));
                String fieldName = (String) data.get("fieldName");
                List<Map<String, Object>> valList = (List<Map<String, Object>>) data.get("datas");
                //先删除原有的缓存数据
                tx.del(key);
                if (valList == null || valList.isEmpty()) {
                    continue;
                }

                for (Map<String, Object> row : valList) {
                    String json = JSONObject.toJSONString(row);
                    String field = row.get(fieldName).toString();
                    tx.hset(key, field.toUpperCase(), json);
                }
            }
            //提交事务
            tx.exec();
        } catch (Exception e) {
            try {
                jedis.unwatch();
            } catch (Exception ex) {
            }
            this.handleException(e);
        } finally {
            returnResource(jedis);
        }
    }


    private void handleException(Exception ex) {
        if (ex != null) {
            _log.error(ex.getMessage(), ex);
        }
    }

    private String getKey(String key) {
        return key;
    }

}
