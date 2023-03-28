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

        if (type.equals("#text"))
            addChildren(((TextNode) node).text(), "html-text");
        else if (type.equals("p")) {
            for (Node child : node.childNodes())
                parseNodeToText(child);

            getChildren().add(new Text("\n\n"));
        } else if (type.equals("br"))
            getChildren().add(new Text("\n"));
        else if (type.equals("a")) {
            if (node.hasAttr("rel") && node.attr("rel").equals("tag"))
                addChildren("#" + node.childNode(1).firstChild(), "html-tag");
            else if (node.attr("class").equals("u-url mention"))
                addChildren("@" + node.childNode(1).firstChild(), "html-mention");
        } else if (type.equals("span") && node.attr("class").equals("h-card"))
            for (Node child : node.childNodes())
                parseNodeToText(child);
        else
            System.err.println("Unknown node type: " + node.outerHtml());
    }

    private void addChildren(String text, String cssClass) {
        Text t = new Text(text);
        t.getStyleClass().add(cssClass);
        getChildren().add(t);
    }
}
