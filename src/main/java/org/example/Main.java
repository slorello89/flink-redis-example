package org.example;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import redis.clients.jedis.JedisPooled;

import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception{
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Configuration globalConfig = new Configuration();

        globalConfig.setString("redis.host", "redis");
        globalConfig.setInteger("redis.port", 6379);

        env.getConfig().setGlobalJobParameters(globalConfig);

        DataStream<String> text = env.fromData("Apache Flink is great", "Flink is fast and powerful");

        DataStream<Tuple2<String, Integer>> wordCounts = text
                .flatMap(new RedisTokenizer())
                .keyBy(value -> value.f0)
                .sum(1);

        wordCounts.print();

        env.execute("word count example");
    }

    public static final class RedisTokenizer extends RichFlatMapFunction<String, Tuple2<String, Integer>> {
        private transient JedisPooled jedis;


        @Override
        public void open(Configuration parameters) {
            ExecutionConfig.GlobalJobParameters globalParams = getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
            Map<String, String> paramMap = globalParams.toMap();

            jedis = new JedisPooled(paramMap.get("redis.host"), Integer.parseInt(paramMap.get( "redis.port")));
        }

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            for (String word : value.split("\\s+")) {
                jedis.incr(word);
                out.collect(new Tuple2<>(word, 1));
            }
        }

        @Override
        public void close() {
            if( jedis != null) {
                jedis.close();
            }
        }
    }
}