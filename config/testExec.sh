#!/bin/sh

bin/crawl -i -D solr.server.url=http://localhost:8983/solr/uni/ qpl/uni.txt testcrawls/uni 8 > output.txt

exitValue=$?

echo '------------------------------------------------'
echo $exitValue
