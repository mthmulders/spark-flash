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
import spark.Response;
import spark.Session;

import java.util.HashMap;
import java.util.Map;

import static spark.flash.Flash.COUNT_ATTRIBUTE;
import static spark.flash.Flash.FLASH_ATTRIBUTE;
import static spark.flash.TestSupport.request;
import static spark.flash.TestSupport.response;

class CleanFlashScopeFilterTest implements WithAssertions {
    private final CleanFlashScopeFilter filter = new CleanFlashScopeFilter();

    @Test
    void should_not_modify_request_without_flash_scoped_data() {
        final Request request = request();
        final Response response = response();

        filter.handle(request, response);

        final Session session = request.session(false);
        assertThat(session).isNull();
    }

    @Test
    void should_increase_request_counter() {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(COUNT_ATTRIBUTE, 0);
        attributes.put(FLASH_ATTRIBUTE, new HashMap<>());
        final Request request = request(attributes);
        final Response response = response();

        filter.handle(request, response);

        final Session session = request.session(false);
        assertThat((Object) session.attribute(COUNT_ATTRIBUTE)).isEqualTo(1);
        assertThat((Object) session.attribute(FLASH_ATTRIBUTE)).isNotNull();
    }

    @Test
    void should_clear_flash_scope_after_request() {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(COUNT_ATTRIBUTE, 1);
        attributes.put(FLASH_ATTRIBUTE, new HashMap<>());
        final Request request = request(attributes);
        final Response response = response();

        filter.handle(request, response);

        final Session session = request.session(false);
        assertThat((Object) session.attribute(COUNT_ATTRIBUTE)).isNull();
        assertThat((Object) session.attribute(FLASH_ATTRIBUTE)).isNull();
    }
}