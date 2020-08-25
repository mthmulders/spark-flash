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

import spark.Request;
import spark.Session;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class adds "flash" scope support to Spark.
 */
public class Flash {
    static final String FLASH_ATTRIBUTE = "spark.flash.data";
    static final String COUNT_ATTRIBUTE = "spark.flash.count";

    private static Map<String, Object> ensureFlashScopeExists(final Request request) {
        final Session session = request.session(true);
        if (session.attribute(FLASH_ATTRIBUTE) == null) {
            session.attribute(FLASH_ATTRIBUTE, new HashMap<String, Object>());
            session.attribute(COUNT_ATTRIBUTE, 0);
        }
        return session.attribute(FLASH_ATTRIBUTE);
    }

    /**
     * Returns the entire flash scope. This can be an empty one if it wasn't used before of if there is none.
     * @param request The Spark request.
     * @return The flash-scoped map, never <code>null</code>.
     */
    public static Map<String, Object> flash(final Request request) {
        final Session session = request.session(false);
        if (session == null) {
            return new HashMap<>();
        }
        final Map<String, Object> scope = session.attribute(FLASH_ATTRIBUTE);
        return scope != null ? scope : new HashMap<>();
    }

    /**
     * Returns a variable from the flash scope. This can be null if the flash scope or the variable doesn't exist.
     * @param request The Spark request.
     * @param key Name of the flash-scoped variable.
     * @param <T> Expected type of the flash-scoped variable.
     * @return The flash-scoped variable.
     */
    public static <T> Optional<T> flash(final Request request, final String key) {
        final Map<String, Object> scope = flash(request);
        final T value = (T) scope.get(key);
        return Optional.ofNullable(value);
    }

    /**
     * Store a value in the flash scope.
     * @param request The Spark request.
     * @param key Name of the flash-scoped variable.
     * @param value Value to store
     * @param <T> Expected type of the flash-scoped value.
     * @return The value itself.
     */
    public static <T> T flash(final Request request, final String key, final T value) {
        final Map<String, Object> scope = ensureFlashScopeExists(request);
        scope.put(key, value);
        request.session().attribute(FLASH_ATTRIBUTE, scope);
        return value;
    }
}
