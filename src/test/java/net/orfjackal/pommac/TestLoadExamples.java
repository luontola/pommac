package net.orfjackal.pommac;

import org.jvyaml.YAML;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

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
