package com.example

import io.netty.util.ResourceLeakDetector
import io.netty.util.ResourceLeakDetector.Level

/** embedded server */
object Server {
  ResourceLeakDetector.setLevel(Level.DISABLED)

  def main(args: Array[String]) {
    unfiltered.netty.Http(8080)
      .handler(FutureTime)
      .handler(SyncCycleTime)
      .handler(DeferredCycleTime)
//      .handler(NettyDeferredCycleTime)
      .handler(BoundedCycleTime)
      .run { s =>
        println("starting unfiltered app at localhost on port %s"
                    .format(s.port))
      }
  }
}
