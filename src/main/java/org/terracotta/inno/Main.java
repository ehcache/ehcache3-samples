package org.terracotta.inno;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.rainfall.statistics.StatisticsPeekHolder;
import org.ehcache.impl.internal.store.offheap.MemorySizeParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;

/**
 * @author Aurelien Broszniowski
 */
public class Main {

  public static void main(String[] args) {
    AtomicReference<Future<StatisticsPeekHolder>> futureRef = new AtomicReference<>();


    ExecutionService executionService = new ExecutionService();

    staticFileLocation("/");

    post("/start", (request, response) -> {
      String body = request.body();
      GsonBuilder builder = new GsonBuilder();
      Gson gson = builder.create();
      Entry[] entries = gson.fromJson(body, Entry[].class);

      long datasetSize = MemorySizeParser.parse(findEntry(entries, "datasetSize"));

      if (futureRef.get() != null) {
        throw new RuntimeException("job already is in progress");
      }

      Future<StatisticsPeekHolder> f = executionService.spawn(new ExecutionService.Config(datasetSize));
      if (!futureRef.compareAndSet(null, f)) {
        throw new RuntimeException("job already is in progress");
      }

      return "Started.";
    });

    get("/waitUntilDone", (request, response) -> {
      Future<StatisticsPeekHolder> future = futureRef.get();
      if (future == null) {
        throw new RuntimeException("no job started");
      }
      future.get();
      futureRef.set(null);
      return "Done.";
    });

    get("/stats", (request, response) -> {
      response.type("application/json");
      List<QueueReporter.Result> data = new ArrayList<>();

      while (true) {
        QueueReporter.Result result = executionService.poll();
        if (result == null) {
          break;
        }
        data.add(result);
      }

      GsonBuilder builder = new GsonBuilder();
      Gson gson = builder.create();

      return gson.toJson(data);
    });
  }

  private static String findEntry(Entry[] entries, String name) {
    for (Entry entry : entries) {
      if (entry.getName().equals(name)) {
        return entry.getValue();
      }
    }
    throw new RuntimeException("Entry not found : " + name);
  }

  static class Entry {

    private String name;
    private String value;

    public Entry() {
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return name + "->" + value;
    }
  }

}
