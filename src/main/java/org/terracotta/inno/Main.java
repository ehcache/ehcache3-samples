package org.terracotta.inno;

import spark.ModelAndView;

import static spark.Spark.get;
import static spark.Spark.staticFileLocation;

/**
 * @author Aurelien Broszniowski
 */
public class Main {

  public static void main(String[] args) {

    staticFileLocation("/");

    get("/single/:number", (request, response) -> {
      return "Hello " + request.params(":number");
    });
  }

}
