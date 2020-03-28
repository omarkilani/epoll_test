#!/bin/sh

export JAVA_HOME=/opt/sapmachine-jdk-14
export PATH=$JAVA_HOME/bin:$PATH

java -XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC \
       -XX:+ParallelRefProcEnabled \
       -Xss256k -Xmx4G \
       -XX:+AlwaysPreTouch \
       -XX:+UseTransparentHugePages \
       -XX:+ClassUnloadingWithConcurrentMark \
       -XX:+UseStringDeduplication \
       -XX:+UseNUMA \
       -XX:+UseCondCardMark \
       -XX:+ExitOnOutOfMemoryError \
       -jar $@
