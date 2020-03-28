package com.example

import io.netty.channel.ChannelHandler.Sharable

import unfiltered.request._
import unfiltered.response._

import unfiltered.netty._

trait Time {
  def blockingTime() = {
    val delay = scala.util.Random.nextInt(1000)
    Thread.sleep(delay)
    new java.util.Date().toString
  }

  def nonblockingTime() = {
    new java.util.Date().toString
  }

  def view(time: String) = {
    PlainTextContent ~> ResponseString(s"""<html><body>The current time is: ${time}</body></html>""")
  }
}

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.Executors
import unfiltered.netty.cycle.{Plan, DeferralExecutor, DeferredIntent}

object ThreadPoolUtils {
  private def cpus = Runtime.getRuntime.availableProcessors

  def daemonThreadFactory(name: String) = new ThreadFactory {
    private val count = new AtomicInteger()

    override def newThread(r: Runnable) = {
      val thread = new Thread(r)
      thread.setName(s"$name-${count.incrementAndGet}")
      thread.setDaemon(true)
      thread
    }
  }

  def newCoreThreadPool(name: String) = {
    // use direct handoff (SynchronousQueue) + CallerRunsPolicy to avoid deadlocks
    // since tasks may have internal dependencies.
    val pool = new ThreadPoolExecutor(
      /* core size */ cpus,
      /* max size */  10 * cpus,
      /* idle timeout */ 60, TimeUnit.SECONDS,
      new SynchronousQueue[Runnable](),
      Executors.defaultThreadFactory // daemonThreadFactory(name)
    )

    // pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy)
    pool
  }
}

trait BoundedThreadPool extends DeferralExecutor with DeferredIntent {
  self: Plan =>
  def underlying = BoundedThreadPool.executor
}

object BoundedThreadPool {
  lazy val executor = ThreadPoolUtils.newCoreThreadPool("SCALAFICATION")
}

@Sharable
object BoundedCycleTime extends cycle.Plan with Time
  with BoundedThreadPool
  with ServerErrorResponse {

  def intent = {
    case req @ GET(Path("/bounded-cycle-time")) => 
      view(blockingTime())
    case req @ GET(Path("/bounded-cycle-time-nonblocking")) => 
      view(nonblockingTime())
  }
}

@Sharable
object FutureTime extends future.Plan with Time
  with ServerErrorResponse {
  
  implicit def executionContext = scala.concurrent.ExecutionContext.Implicits.global
  def intent = {
    case req @ GET(Path("/future-time")) => 
      scala.concurrent.Future(view(blockingTime()))
  }
}

import cycle.{DeferralExecutor,DeferredIntent,SynchronousExecution}
@Sharable
object DeferredCycleTime extends cycle.Plan with Time
  with cycle.ThreadPool
  with ServerErrorResponse {

  def intent = {
    case req @ GET(Path("/deferred-cycle-time")) => 
      view(blockingTime())
  }
}

/*
@Sharable
object NettyDeferredCycleTime extends cycle.Plan with Time
  with cycle.DeferredIntent
  with cycle.DeferralExecutor
  with ServerErrorResponse {

  def intent = {
    case req @ GET(Path("/netty-deferred-cycle-time")) => 
      view(blockingTime())
  }
}
*/

@Sharable
object SyncCycleTime extends cycle.Plan with Time
  with SynchronousExecution
  with ServerErrorResponse {
  
  def intent = {
    case req @ GET(Path("/sync-cycle-time")) => 
      view(blockingTime())
  }
}
