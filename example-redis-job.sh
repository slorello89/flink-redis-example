#!/bin/bash

./gradlew clean shadowJar
docker cp build/libs/example-redis-job-all-1.0-SNAPSHOT.jar jobmanager:/opt/flink/examples/streaming
docker exec -it jobmanager ./bin/flink run examples/streaming/example-redis-job-all-1.0-SNAPSHOT.jar