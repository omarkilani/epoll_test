#!/bin/sh

ab -c 100 -n 10000000 http://localhost:8080/bounded-cycle-time-nonblocking
