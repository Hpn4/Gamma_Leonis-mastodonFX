package eus.ehu.gleonis.gleonismastodonfx.utils;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class HTMLView extends TextFlow {

    public HTMLView() {
        super();
    }

    public HTMLView(String html) {
        super();
        setHtml(html);
    }

    public void setHtml(String html) {
        getChildren().clear();

        Document doc = Jsoup.parseBodyFragment(html);
        Element body = doc.body();

        for (Node node : body.childNodes())
            parseNodeToText(node);
    }

    private void parseNodeToText(Node node) {
        String type = node.nodeName();

        if (type.equals("#text")) {
            Text text = new Text(((TextNode) node).text());
            text.getStyleClass().add("html-text");
            getChildren().add(text);
        } else if (type.equals("p")) {
            for (Node child : node.childNodes())
                parseNodeToText(child);

            getChildren().add(new Text("\n\n"));
        } else if (type.equals("br"))
            getChildren().add(new Text("\n"));
        else if (type.equals("a")) {
            if (node.hasAttr("rel") && node.attr("rel").equals("tag")) {
                Text text = new Text("#" + node.childNode(1).firstChild());
                text.getStyleClass().add("html-tag");
                getChildren().add(text);
            } else if (node.attr("class").equals("u-url mention")) {
                Text text = new Text("@" + node.childNode(1).firstChild());
                text.getStyleClass().add("html-mention");
                getChildren().add(text);
            }
        } else if (type.equals("span") && node.attr("class").equals("h-card"))
            for (Node child : node.childNodes())
                parseNodeToText(child);
        else
            System.err.println("Unknown node type: " + node.outerHtml());
    }
}
