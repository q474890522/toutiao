package com.nowcoder.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.HashSet;
import java.util.Set;

@Component
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool jedisPool = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        jedisPool = new JedisPool("localhost", 6379);
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("Jedis添加异常" + e.getMessage());
            return 0;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("Jedis删除异常" + e.getMessage());
            return 0;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("判断Jedis set是否存在value异常" + e.getMessage());
            return false;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("获取Jedis set长度异常" + e.getMessage());
            return 0;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public static void print(int index, Object object) {
        System.out.println(String.format("%d, %s", index, object.toString()));
    }
    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.flushAll();

        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newhello");
        print(1, jedis.get("newhello"));
        jedis.setex("hello2", 15, "world");

        jedis.set("pv", "100");
        jedis.incr("pv");
        print(2, jedis.get("pv"));
        jedis.incrBy("pv", 5);
        print(2, jedis.get("pv"));

        String listName = "listA";
        for(int i=0; i < 10; i++) {
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
        print(3, jedis.lrange(listName, 0, -1));
        print(4, jedis.llen(listName));
        print(5, jedis.lpop(listName));
        print(5, jedis.lrange(listName, 0, -1));
        print(6, jedis.lindex(listName, 3));
        print(7, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a8", "xxx"));
        print(7, jedis.lrange(listName, 0, -1));
        print(8, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a8", "yyy"));
        print(8, jedis.lrange(listName, 0, -1));

        String userKey = "user12";
        jedis.hset(userKey, "name", "xiaoming");
        jedis.hset(userKey, "phone", "18810018300");
        jedis.hset(userKey, "age", "12");

        print(9, jedis.hget(userKey, "name"));
        print(10, jedis.hgetAll(userKey));
        print(11, jedis.hincrBy(userKey, "age", 2));
        print(12, jedis.hdel(userKey, "phone"));
        print(13, jedis.hexists(userKey, "name"));
        print(14, jedis.hlen(userKey));
        print(15, jedis.hvals(userKey));
        print(16, jedis.hkeys(userKey));
        jedis.hsetnx(userKey, "school", "bupt");
        jedis.hsetnx(userKey, "name", "aaaa");
        print(17, jedis.hkeys(userKey));
        print(18, jedis.hvals(userKey));

        String likeKey1 = "newLike1";
        String likeKey2 = "newLike2";
        for(int i = 0; i < 10; i++) {
            jedis.sadd(likeKey1, String.valueOf(i));
            jedis.sadd(likeKey2, String.valueOf(i * 2));
        }
        print(19, jedis.smembers(likeKey1));
        print(20, jedis.smembers(likeKey2));
        print(21, jedis.sunion(likeKey1, likeKey2));
        print(22, jedis.sinter(likeKey1, likeKey2));
        print(23, jedis.scard(likeKey1));
        print(24, jedis.sdiff(likeKey1, likeKey2));
        String likeKey3 = "newLikeKey3";
        jedis.sdiffstore(likeKey3, likeKey2, likeKey1);
        print(25, jedis.smembers(likeKey3));
        jedis.srem("newLikeKey3", "10");
        print(26, jedis.smembers(likeKey3));
        jedis.smove(likeKey1, likeKey2, "1");
        print(27, jedis.smembers(likeKey1));
        print(28, jedis.smembers(likeKey2));

        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "a");
        jedis.zadd(rankKey, 60, "b");
        jedis.zadd(rankKey, 75, "c");
        jedis.zadd(rankKey, 90, "d");
        jedis.zadd(rankKey, 80, "e");
        print(29, jedis.zcard(rankKey));
        print(30, jedis.zcount(rankKey, 60, 90));
        print(31, jedis.zscore(rankKey, "e"));
        jedis.zincrby(rankKey, 2, "d");
        print(32, jedis.zscore(rankKey, "d"));
        jedis.zincrby(rankKey, 4, "f");
        print(33, jedis.zrange(rankKey, 0, -1));
        print(34, jedis.zrevrange(rankKey, 0, -1));
        Set<Tuple> tupleSet = jedis.zrangeByScoreWithScores(rankKey, "0", "100");
        for(Tuple tuple : tupleSet) {
            print(35, tuple.getElement() + ":" + tuple.getScore());
        }
        print(36, jedis.zrank(rankKey, "d"));
        print(37, jedis.zrevrank(rankKey, "d"));

        JedisPool jedisPool = new JedisPool();
        for(int i = 0; i < 100; i++) {
            Jedis j = jedisPool.getResource();
            System.out.println("Pool:" + i);
            j.close();
        }

    }
}
