/*
 * Copyright © 2008-2009  Esko Luontola, www.orfjackal.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.orfjackal.pommac;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
public class MatchingFileNotFoundException extends RuntimeException {

    public MatchingFileNotFoundException(String query) {
        super(toMessage(query));
    }

    public MatchingFileNotFoundException(String query, Throwable cause) {
        super(toMessage(query), cause);
    }

    private static String toMessage(String query) {
        return "No file matching: " + query;
    }
}
