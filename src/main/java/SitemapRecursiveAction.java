import lombok.NonNull;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Pattern;

public class SitemapRecursiveAction extends RecursiveAction {
    private final Logger logger;
    private final Node node;

    public SitemapRecursiveAction(Node node, Logger logger) {
        this.node = node;
        this.logger = logger;
    }

    @Override
    protected void compute() {
        try {
            Connection connection = Jsoup.connect(node.getUrl()).maxBodySize(0);

            Thread.sleep(1000);

            connection.get().select("a")
                    .forEach(url -> {
                        String childUrl = url.absUrl("href");

                        if (isCorrectUrl(childUrl)) {
                            childUrl = stripParams(childUrl);
                            node.addChild(new Node(childUrl));
                        }
                    });
        } catch (IOException | InterruptedException e) {
            logger.error(e.toString());
        }

        List<SitemapRecursiveAction> tasksList = new ArrayList<>();

        node.getChildren().forEach(child -> {
            SitemapRecursiveAction task = new SitemapRecursiveAction(child, logger);

            tasksList.add(task);
        });

        invokeAll(tasksList);
    }

    private @NonNull String stripParams(@NonNull String url) {
        return url.replaceAll("\\?.+", "");
    }

    private boolean isCorrectUrl(@NonNull String url) {
        Pattern patternNotFile = Pattern.compile("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|pdf|php))$)");
        Pattern patternNotAnchor = Pattern.compile("#([\\w\\-]+)?$");

        return url.startsWith(node.getUrl())
                && !patternNotFile.matcher(url).find()
                && !patternNotAnchor.matcher(url).find()
                && !url.equals(node.getUrl());
    }
}