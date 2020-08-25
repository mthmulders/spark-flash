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
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Session;

import static spark.flash.Flash.COUNT_ATTRIBUTE;
import static spark.flash.Flash.FLASH_ATTRIBUTE;

/**
 * This filter cleans up old flash-scoped request attributes.
 * It limits the lifetime of any flash-scoped variable to 2 requests.
 */
public class CleanFlashScopeFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(CleanFlashScopeFilter.class);

    @Override
    public void handle(final Request request, final Response response) {
        final Session session = request.session(false);
        if (session == null || session.attribute(COUNT_ATTRIBUTE) == null) {
            return;
        }

        final int count = session.attribute(COUNT_ATTRIBUTE);

        if (count >= 1) {
            log.debug("Cleaning flash scope for session {}", session.id());
            session.attribute(COUNT_ATTRIBUTE, null);
            session.attribute(FLASH_ATTRIBUTE, null);
        } else {
            session.attribute(COUNT_ATTRIBUTE, count + 1);
        }
    }
}
