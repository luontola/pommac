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

import org.jvyaml.YAML;

import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Esko Luontola
 * @since 28.2.2008
 */
public class TestLoadExamples {

    public static void main(String[] args) {
        Object jvyaml = YAML.load(new InputStreamReader(TestLoadExamples.class.getResourceAsStream("/examples/sgs.pommac")));
        printObject(0, jvyaml);
    }

    private static void printObject(int indent, Object data) {
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            for (Object obj : list) {
                printObject(indent, obj);
            }
        } else if (data instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) data;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                println(indent, entry.getKey());
                printObject(indent + 1, entry.getValue());
            }
        } else {
            println(indent, data);
        }
    }

    private static void println(int indent, Object obj) {
        for (int i = 0; i < indent; i++) {
            System.out.print("\t");
        }
        System.out.println(obj);
    }
}
