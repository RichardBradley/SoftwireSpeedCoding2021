package q0;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Q0ExampleMain {

    final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            new Q0ExampleMain().run();
        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private void run() throws Exception {
        // You can also ask Jackson to deserialise straight from the resource InputStream
        // I've expanded this here in case you need the input as a String
        String input = Resources.toString(Resources.getResource("genericSample.json"), StandardCharsets.UTF_8);
        MenuRoot menu = objectMapper.readValue(input, MenuRoot.class);
        System.out.println("Today's menu:");
        System.out.println("----");
        for (MenuItem menuItem : menu.menu) {
            System.out.printf("%1$-30s £%s\n", menuItem.dishName, menuItem.cost);
        }
    }

    @Value
    static class MenuRoot {
        List<MenuItem> menu;
    }

    @Value
    static class MenuItem {
        String dishName;
        int cost;
    }
}
