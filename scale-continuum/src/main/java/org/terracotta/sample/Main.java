package org.terracotta.sample;

import io.rainfall.statistics.StatisticsPeekHolder;
import org.terracotta.sample.collector.Config;
import org.terracotta.sample.collector.Entry;
import org.terracotta.sample.collector.PerformanceMetricsCollector;
import org.terracotta.sample.collector.QueueReporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;

/**
 * @author Aurelien Broszniowski
 */
public class Main {

  public static void main(String[] args) {
    AtomicReference<Future<StatisticsPeekHolder>> futureRef = new AtomicReference<>();

    PerformanceMetricsCollector metricsCollector = new PerformanceMetricsCollector();

    staticFileLocation("/public");

    post("/api/start", (request, response) -> {
      String body = request.body();
      GsonBuilder builder = new GsonBuilder();
      Gson gson = builder.create();
      Entry[] entries = gson.fromJson(body, Entry[].class);

      if (futureRef.get() != null) {
        throw new RuntimeException("job already is in progress");
      }

      Future<StatisticsPeekHolder> f = metricsCollector.start(new Config(entries));
      if (!futureRef.compareAndSet(null, f)) {
        throw new RuntimeException("job already is in progress");
      }

      return "Started.";
    });

    get("/api/waitUntilDone", (request, response) -> {
      Future<StatisticsPeekHolder> future = futureRef.get();
      if (future == null) {
        throw new RuntimeException("no job started");
      }
      future.get();
      futureRef.set(null);
      return "Done.";
    });

    get("/api/cancel", (request, response) -> {
      Future<StatisticsPeekHolder> future = futureRef.get();
      if (future == null) {
        throw new RuntimeException("no job started");
      }
      future.cancel(true);
      futureRef.set(null);
      return "Done.";
    });

    get("/api/cancelNoFail", (request, response) -> {
      Future<StatisticsPeekHolder> future = futureRef.get();
      if (future != null) {
        future.cancel(true);
        futureRef.set(null);
      }
      return "Done.";
    });

    get("/api/stats", (request, response) -> {
      response.type("application/json");
      List<QueueReporter.Result> data = new ArrayList<>();

      while (metricsCollector.isRunning()) {
        QueueReporter.Result result = metricsCollector.pollStats();
        if (result == null) {
          break;
        }
        data.add(result);
      }

      if (data.isEmpty() && !metricsCollector.isRunning()) {
        // marker value to signify the end of the data
        data.add(new QueueReporter.Result(-1));
      }

      GsonBuilder builder = new GsonBuilder();
      Gson gson = builder.create();

      return gson.toJson(data);
    });

    exception(Exception.class, (exception, request, response) -> {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      exception.printStackTrace(pw);
      pw.close();
      String stackTrace = sw.toString();

      response.status(500);
      response.body(stackTrace);
    });

  }

}
