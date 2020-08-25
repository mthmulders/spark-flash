/*
 * Copyright 2020 - Maarten Mulders
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.flash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static spark.Service.ignite;
import static spark.flash.Flash.flash;

public class FlashIT implements WithAssertions {
    private static final Logger log = LoggerFactory.getLogger(FlashIT.class);

    private static final String PATH = "/flash";

    private final HttpClient client = HttpClient.newBuilder().cookieHandler(new CookieManager()).build();
    private final Service http = ignite();

    @BeforeEach
    void setup() {
        http.after(new CleanFlashScopeFilter());
        http.get(PATH, (req, res) -> flash(req, "key").orElse(""));
        http.post(PATH, (req, res) -> flash(req, "key", "value"));
        http.awaitInitialization();
        log.info("Spark started");
    }

    @AfterEach
    void shutdown() {
        log.info("Stopping Spark");
        http.stop();
    }

    private String performRequest(final String method) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .method(method, noBody())
                .uri(URI.create("http://localhost:4567/" + PATH))
                .build();
        final HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        return response.body();
    }

    private String performGet() throws IOException, InterruptedException {
        log.info("Performing GET to /flash");
        return performRequest("GET");
    }

    private String performPost() throws IOException, InterruptedException {
        log.info("Performing POST to /flash");
        return performRequest("POST");
    }

    @Test
    void scope_is_initially_empty() throws IOException, InterruptedException {
        assertThat(performGet()).isEmpty();
    }

    @Test
    void reads_previously_filled_scope() throws IOException, InterruptedException {
        performPost();
        assertThat(performGet()).isEqualTo("value");
    }
}
