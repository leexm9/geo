package com.leexm.demo.geo.dal.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author leexm
 * @date 2019-09-01 17:20
 */
public class JedisClient {

    private JedisPool jedisPool;

    private String host;
    private int port = 6379;

    public JedisClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void init() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(poolConfig, host, port);
    }

    public Jedis getClient() {
        return jedisPool.getResource();
    }

}
