import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final String ROOT = "root_url";

    public static void main(String[] args) throws IOException {
        Logger logger = LogManager.getLogger();
        long start = System.currentTimeMillis();

        Node root = new Node(ROOT);
        new ForkJoinPool().invoke(new SitemapRecursiveAction(root, logger));

        String result = createStringBuilder(root, 0);

        FileWriter writer = new FileWriter("src/main/resources/sitemap.txt");

        writer.write(result);
        writer.close();

        System.out.println("Duration: " + (System.currentTimeMillis() - start) + " ms");
    }

    public @NonNull
    static String createStringBuilder(@NonNull Node node, int depth) {
        String tabs = String.join("", Collections.nCopies(depth, "\t"));
        StringBuilder result = new StringBuilder(tabs + node.getUrl());
        node.getChildren().forEach(child -> result
                .append("\n")
                .append(createStringBuilder(child, depth + 1)));

        return result.toString();
    }
}