# Example Flink -> Redis Job

This repository contains sample code for sinking Flink -> Redis.

## How to Run the Example

1. Spin up docker containers: `docker-compose up -d`
2. Run the job `./example-redis-job.sh`

## How it works

### The Code

The example job creates a `RedisTokenizer` which is just a `RichFlatMapFunction` for processing
the input stream in Flink. the `open` and `close` methods are used to open and close the Redis connection
based off the global job configuration. This examples is a classic word-count so we use the `INCR` command
to track the number of times a word occurs in the example.

### Packaging

Because the example job requires Jedis (and all it's dependencies), we use the `shadow` plugin to create a fat jar.
