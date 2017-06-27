/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dish;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

/**
 *
 * @author liyou
 */
public class JedisMain {
    //address of your redis server
    private static final String redisHost = "localhost";
    private static final Integer redisPort = 6379;
 
    //the jedis connection pool..
    private static JedisPool pool = null;
 
    public JedisMain() {
        //configure our pool connection
        pool = new JedisPool(redisHost, redisPort);
 
    }
    
    public void build(Map<String, List<Frequency>> summary) {
        //get a jedis connection jedis connection pool
        String key1 = "dishName";
        String key2 = "type";
        String key3 = "count";
        try (Jedis jedis = pool.getResource()) {
            jedis.flushAll();
            for (String key : summary.keySet()) {
                List<Frequency> list = summary.get(key);
                for (int i = 0; i < list.size(); i++) {
                    String mediate = key + "[" + i + "]";
                    Frequency cur = list.get(i);
                    jedis.zadd(key, i, mediate);
                    jedis.hset(mediate, key1, cur.dishName);
                    jedis.hset(mediate, key2, cur.type.name());
                    jedis.hset(mediate, key3, String.valueOf(cur.freq));
                }
            }
        }
    }
    
    public List<Frequency> query(String key, int max) {
        List<Frequency> ans = new ArrayList<>();
        try(Jedis jedis = pool.getResource()) {
            Set<String> mediate = jedis.zrange(key, 0, max);
            for (String m : mediate) {
                Map<String, String> map = jedis.hgetAll(m);
                ans.add(new Frequency(map.get("dishName"), Enum.valueOf(DishType.class, map.get("type")), Integer.parseInt(map.get("count"))));
            }
        }
        return ans;
    }
    
}
