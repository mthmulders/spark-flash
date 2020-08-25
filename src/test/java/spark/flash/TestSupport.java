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

import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import spark.Request;
import spark.RequestResponseFactory;
import spark.Response;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class TestSupport {

    /**
     * Work-around for https://github.com/mockrunner/mockrunner/issues/77,
     * which says that request.getSession(false) does *not* return the Session tha
     * was earlier set using request.setSession(...) unless getSession(true) was called...
     */
    public static class MockHttpServletRequest extends com.mockrunner.mock.web.MockHttpServletRequest {
        private HttpSession session;

        MockHttpServletRequest() {
            this(null);
        }

        MockHttpServletRequest(final HttpSession session) {
            this.session = session;
        }

        @Override
        public HttpSession getSession(boolean create) {
            if (session != null) {
                return session;
            }
            if (create) {
                this.session = new MockHttpSession();
                return this.session;
            }
            return null;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }
    }

    public static Request request() {
        return RequestResponseFactory.create(new MockHttpServletRequest());
    }

    public static Request request(final Map<String, Object> sessionAttributes) {
        final MockHttpSession session = new MockHttpSession();
        sessionAttributes.forEach(session::setAttribute);

        final MockHttpServletRequest request = new MockHttpServletRequest(session);
        return RequestResponseFactory.create(request);
    }

    public static Response response() {
        return RequestResponseFactory.create(new MockHttpServletResponse());
    }
}
