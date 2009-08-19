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

import net.orfjackal.pommac.util.FileUtil;

import java.io.File;
import java.util.*;
import java.util.regex.*;

/**
 * @author Esko Luontola
 * @since 1.3.2008
 */
public final class ExpressionInterpreter {

    private ExpressionInterpreter() {
    }

    public static String evaluate(File workDir, String expression) {
        List<String> words = splitWords(expression);
        if (words.size() == 1) {
            return words.get(0);
        }
        String pathQuery = words.get(0);
        String op1 = words.get(1);
        String regex = words.get(2);
        String op2 = words.get(3);
        String format = words.get(4);

        FileLocator locator = new FileLocator();
        String result;
        try {
            File file = locator.findFile(workDir, pathQuery);
            String haystack = dataForMatching(op1, file);
            String[] groups = findMatches(regex, haystack);
            result = format(op2, format, groups);
        } finally {
            locator.dispose();
        }
        return result;
    }

    private static List<String> splitWords(String s) {
        List<String> words = new ArrayList<String>();
        Scanner in = new Scanner(s);
        while (in.hasNext()) {
            words.add(in.next());
        }
        return words;
    }

    private static String dataForMatching(String op, File file) {
        if (op.equals("|")) {
            return file.getName();
        } else if (op.equals(">>")) {
            return FileUtil.contentsOf(file);
        } else {
            throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }

    private static String[] findMatches(String regex, String s) {
        Matcher m = Pattern.compile(regex).matcher(s);
        if (m.find()) {
            return groupsToArray(m);
        }
        throw new IllegalArgumentException("'" + regex + "' does not match '" + s + "'");
    }

    private static String[] groupsToArray(Matcher m) {
        String[] groups = new String[m.groupCount()];
        for (int i = 0; i < groups.length; i++) {
            groups[i] = m.group(i + 1);
        }
        return groups;
    }

    private static String format(String op, String format, String[] args) {
        if (op.equals(">>")) {
            return String.format(format, (Object[]) args);
        } else {
            throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }
}
