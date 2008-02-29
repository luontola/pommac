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
        List<String> words = splitWords(version);

        String pathQuery = words.get(0);
        String op1 = words.get(1);
        String regex = words.get(2);
        String op2 = words.get(3);
        String format = words.get(4);

        assert op1.equals("|");
        assert op2.equals(">>");

        System.out.println(pathQuery);
        System.out.println(op1);
        System.out.println(regex);
        System.out.println(op2);
        System.out.println(format);

        FileLocator locator = new FileLocator();
        try {
            File file = locator.findFile(workDir, pathQuery);
            String[] groups = matchingGroups(regex, file.getName());
            version = String.format(format, (Object[]) groups);
        } finally {
            locator.dispose();
        }
    }

    private static List<String> splitWords(String s) {
        List<String> words = new ArrayList<String>();
        Scanner in = new Scanner(s);
        while (in.hasNext()) {
            words.add(in.next());
        }
        return words;
    }

    private static String[] matchingGroups(String regex, String s) {
        Matcher m = Pattern.compile(regex).matcher(s);
        if (m.matches()) {
            return groupsToArray(m);
        }
        throw new IllegalArgumentException(regex + " does not match " + s);
    }

    private static String[] groupsToArray(Matcher m) {
        String[] groups = new String[m.groupCount()];
        for (int i = 0; i < groups.length; i++) {
            groups[i] = m.group(i + 1);
        }
        return groups;
    }
}
