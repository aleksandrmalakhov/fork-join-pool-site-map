import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private volatile Node parent;
    @Getter
    private final String url;
    @Getter
    private final List<Node> children;

    public Node(String url) {
        this.url = url;
        parent = null;
        children = Collections.synchronizedList(new ArrayList<>());
    }

    public synchronized void addChild(@NonNull Node element) {
        Node root = getRootElement();
        if (!root.isContains(element.getUrl())) {
            element.setParent(this);
            children.add(element);
        }
    }

    private boolean isContains(String url) {
        if (this.url.equals(url)) {
            return true;
        }
        for (Node child : children) {
            if (child.isContains(url))
                return true;
        }
        return false;
    }

    private synchronized void setParent(Node node) {
        this.parent = node;
    }

    public Node getRootElement() {
        return parent == null ? this : parent.getRootElement();
    }
}