# Flash support for Spark Java ⚡

![Run tests](https://github.com/mthmulders/spark-flash/workflows/Run%20tests/badge.svg)
[![SonarCloud quality gate](https://sonarcloud.io/api/project_badges/measure?project=mthmulders_spark-flash&metric=alert_status)](https://sonarcloud.io/dashboard?id=mthmulders_spark-flash)
[![SonarCloud vulnerability count](https://sonarcloud.io/api/project_badges/measure?project=mthmulders_spark-flash&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=mthmulders_spark-flash)
[![SonarCloud technical debt](https://sonarcloud.io/api/project_badges/measure?project=mthmulders_spark-flash&metric=sqale_index)](https://sonarcloud.io/dashboard?id=mthmulders_spark-flash)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=mthmulders/spark-flash)](https://dependabot.com)
[![Mutation testing badge](https://img.shields.io/endpoint?style=plastic&url=https%3A%2F%2Fbadge-api.stryker-mutator.io%2Fgithub.com%2Fmthmulders%2Fspark-flash%2Fmaster)](https://dashboard.stryker-mutator.io/reports/github.com/mthmulders/spark-flash/master)
[![Maven Central](https://img.shields.io/maven-central/v/it.mulders.spark-flash/spark-flash.svg?color=brightgreen&label=Maven%20Central)](https://search.maven.org/artifact/it.mulders.spark-flash/spark-flash)
[![](https://img.shields.io/github/license/mthmulders/spark-flash.svg)](./LICENSE)

## TL;DR

This module adds a "flash scope" to [Spark Java](http://sparkjava.com/).

## Flash scope?
Imagine a server-rendered web application.
When a user submits a form with some data - typically using HTTP POST - and hits the "refresh" button, the form shouldn't be submitted twice.
The [Post-Redirect-Get pattern](https://en.wikipedia.org/wiki/Post/Redirect/Get) makes this possible: it answers the POST with a redirect.
The browser will follow that redirect, and perform a GET on the specified location.

The question is: how to convey information about the form processing to that new page?
**That's where the "flash scope" comes in.**
It allows the developer to temporarily store some information and retrieve it in the next request.

So when form processing is done, you store a bit of information in the flash scope.
It can be a simple message, like "order placed" or "validation failed", or even be more complex.
You redirect the user, and in the next page, you access the flash scope again to retrieve that information you stored earlier.

## How to use?
First, add this module to your POM:

```xml
<dependency>
    <groupId>it.mulders.spark-flash</groupId>
    <artifactId>spark-flash</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```

**Important:** register the cleanup filter.
If you omit this step, your application will use a lot more memory and users may see odd results because the flash scope is kept as long as their session lives.
 
```java
import spark.flash.CleanFlashScopeFilter;
import static spark.Spark.after;

public class App {
  public static void main(final String... args){
    after(new CleanFlashScopeFilter());    
  }
}
```

Finally, use the flash scope in your routes to implement user interface logic:

```java
import static spark.Spark.get;
import static spark.Spark.post;

public class App {
  public static void main(final String... args){
    post("/order", (req, res) -> {
      // Do complex order processing
      flash(req, "status", "Order placed successfully");
      redirect("/confirmation");
      return null;
    });
    get("/confirmation", (req, res) -> {
      return "Your order status is " + flash(req, "status");
    });
  }
}
```

## License
This module is licensed to you under the terms of the Apache License, version 2.0.
See the file LICENSE for the full text of this license. 