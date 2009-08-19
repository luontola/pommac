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

/**
 * @author Esko Luontola
 * @since 28.2.2008
 */
public class Artifact {

    public String groupId;
    public String artifactId;
    public String version;

    public String jar;
    public String[] sources = new String[0];
    public String[] resources = new String[0];
    public String javadoc;

    public String[] depends = new String[0];

    public void replaceAll(String find, String replace) {
        if (version != null) {
            version = version.replaceAll(find, replace);
        }
        if (jar != null) {
            jar = jar.replaceAll(find, replace);
        }
        for (int i = 0; i < sources.length; i++) {
            sources[i] = sources[i].replaceAll(find, replace);
        }
        for (int i = 0; i < resources.length; i++) {
            resources[i] = resources[i].replaceAll(find, replace);
        }
        if (javadoc != null) {
            javadoc = javadoc.replaceAll(find, replace);
        }
    }

    public void calculateVersion(File workDir) {
        version = ExpressionInterpreter.evaluate(workDir, version);
    }

    public void locateFiles(FileLocator locator) {
        if (jar != null) {
            jar = locator.findFile(jar).getAbsolutePath();
        }
        for (int i = 0; i < sources.length; i++) {
            sources[i] = locator.findFile(sources[i]).getAbsolutePath();
        }
        for (int i = 0; i < resources.length; i++) {
            resources[i] = locator.findFile(resources[i]).getAbsolutePath();
        }
        if (javadoc != null) {
            javadoc = locator.findFile(javadoc).getAbsolutePath();
        }
    }
}
