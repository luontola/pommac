package net.orfjackal.pommac;

import org.ho.yaml.Yaml;

import java.util.List;
import java.util.Map;

/**
 * @author Esko Luontola
 * @since 28.2.2008
 */
public class TestLoadExamples {
    public static void main(String[] args) {
        Object data = Yaml.load(TestLoadExamples.class.getResourceAsStream("/examples/sgs.pommac"));
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            for (Object obj : list) {
                System.out.println(obj);
            }
        } else if (data instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) data;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                System.out.println(entry.getKey());
                System.out.println("\t" + entry.getValue());
            }
        } else {
            System.out.println(data);
        }
    }
}
