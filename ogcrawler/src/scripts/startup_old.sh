java -cp "../lib/*" \
-DML_API_BASE_URL=http://192.168.35.12:8080/RSS \
-Dlogback.configurationFile=../logs/logback.xml \
-Dredis.history.tracker.uri=redis://192.168.35.15 \
com.squill.og.crawler.app.Startup -f ../conf/crawlers-config.xml > /dev/null &
