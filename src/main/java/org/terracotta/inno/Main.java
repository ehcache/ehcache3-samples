package org.terracotta.inno;

import spark.ModelAndView;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;

/**
 * @author Aurelien Broszniowski
 */
public class Main {

  public static void main(String[] args) {

    staticFileLocation("/");

    post("/start", (request, response) -> {
      return "Hello " + request.body();
    });
  }

}
