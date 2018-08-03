#!/bin/bash

java -cp "../lib/*" \
-Dlogback.configurationFile=../logs/logback.xml \
com.squill.og.crawler.app.Startup -f ../conf/crawlers-config.xml > /dev/null &

echo $! > ~/squill-crawler.pid

