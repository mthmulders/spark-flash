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

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static spark.flash.Flash.COUNT_ATTRIBUTE;
import static spark.flash.Flash.FLASH_ATTRIBUTE;
import static spark.flash.Flash.flash;
import static spark.flash.TestSupport.request;

class FlashTest implements WithAssertions {
    @Test
    void should_expose_flash_scope() {
        final Map<String, Object> scope = new HashMap<>();
        scope.put("key", "value");

        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(COUNT_ATTRIBUTE, 1);
        attributes.put(FLASH_ATTRIBUTE, scope);

        final Request request = request(attributes);

        final Map<String, Object> result = flash(request);

        assertThat(result)
                .containsKey("key")
                .extractingByKey("key").isEqualTo("value");
    }

    @Test
    void should_create_scope_when_requested() {
        final Request request = request();

        final Map<String, Object> result = flash(request);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void should_retrieve_variable_from_scope() {
        final Map<String, Object> scope = new HashMap<>();
        scope.put("key", "value");

        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(FLASH_ATTRIBUTE, scope);

        final Request request = request(attributes);

        final Optional<String> result = flash(request, "key");

        assertThat(result).isNotEmpty().hasValue("value");

    }

    @Test
    void should_count_requests_when_scope_created() {
        final Request request = request();

        flash(request, "key", "value");

        assertThat(request.session().attributes()).contains(COUNT_ATTRIBUTE);
    }
}