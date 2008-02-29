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

    public void locateFiles() {
        
    }
}
