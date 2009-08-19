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

import java.io.File;
import java.util.Arrays;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
public class UnambiguousQueryException extends RuntimeException {

    public UnambiguousQueryException(String query, File[] found) {
        super(toMessage(query, found));
    }

    private static String toMessage(String query, File[] found) {
        return "Unambiguous query: " + query + "\n" +
                "More than one match: " + Arrays.asList(found);
    }
}
