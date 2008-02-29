package net.orfjackal.pommac;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        List<String> parts = new ArrayList<String>();
        Scanner in = new Scanner(version);
        while (in.hasNext()) {
            parts.add(in.next());
        }

        String pathQuery = parts.get(0);
        System.out.println(pathQuery);
        String op1 = parts.get(1);
        System.out.println(op1);
        assert op1.equals("|");
        String regex = parts.get(2);
        System.out.println(regex);
        String op2 = parts.get(3);
        System.out.println(op2);
        assert op2.equals(">>");
        String format = parts.get(4);
        System.out.println(format);

        FileLocator locator = new FileLocator();
        try {
            File file = locator.findFile(workDir, pathQuery);
            System.out.println("file = " + file);

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(file.getName());
            System.out.println(m);
            if (!m.matches()) {
                throw new IllegalArgumentException(regex + " does not match " + file);
            }
            String[] groups = new String[m.groupCount()];
            for (int i = 0; i < groups.length; i++) {
                groups[i] = m.group(i + 1);
            }
            version = String.format(format, groups);
        } finally {
            locator.dispose();
        }
    }
}
