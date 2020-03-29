#!/bin/sh

./burn --disable-keepalives -c 100 -d 120s http://localhost:8080/bounded-cycle-time-nonblocking
