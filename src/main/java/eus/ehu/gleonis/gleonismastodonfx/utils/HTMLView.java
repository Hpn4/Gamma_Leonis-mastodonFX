package eus.ehu.gleonis.gleonismastodonfx.utils;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.StatusMention;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class HTMLView extends TextFlow {

    private static final Logger logger = LogManager.getLogger("HTMLView");

    private final Status status;

    private final boolean context;

    public HTMLView(Status status, String html, boolean context) {
        super();
        this.status = status;
        this.context = context;

        setHtml(html);
    }

    public void setHtml(String html) {
        getChildren().clear();

        Element body = Jsoup.parseBodyFragment(html).body();

        for (Node node : body.childNodes())
            parseNodeToText(node);
    }

    private void parseNodeToText(Node node) {
        String type = node.nodeName();

        switch (type) {
            case "#text" -> addChildren(((TextNode) node).text(), "html-text");
            case "p" -> {
                for (Node child : node.childNodes())
                    parseNodeToText(child);
                getChildren().add(new Text("\n\n"));
            }
            case "br" -> getChildren().add(new Text("\n"));
            case "a" -> {
                String t = node.childNode(0).toString();

                // Tag mention
                if (t.startsWith("#")) {
                    String tag;
                    if (node.childNodes().size() > 1)
                        tag = "#" + node.childNode(1).firstChild().toString();
                    else
                        tag = t;

                    addChildren(tag, "html-tag").setOnMouseClicked(
                            e -> MainApplication.getInstance().requestShowToots(MainApplication.getInstance().getAPI().getHashTagTimelines(tag.substring(1), 10), 10)
                    );
                }

                // Account mention
                else if (t.startsWith("@") && node.childNodeSize() > 1) {
                    String user = node.childNode(1).firstChild().toString();
                    String accId = getIDOfUser(user);

                    addChildren("@" + user, "html-mention").setOnMouseClicked(
                            e -> MainApplication.getInstance().requestShowAccount(accId)
                    );
                }

                // Weblink
                else if (node.childNodeSize() > 1) {
                    addChildren(node.childNode(1).firstChild() + "...", "html-link").setOnMouseClicked(
                            e -> MainApplication.getInstance().getHostServices().showDocument(node.attr("href"))
                    );
                }
            }
            case "span" -> {
                for (Node child : node.childNodes())
                    parseNodeToText(child);
            }
            default -> logger.error("Unknown node type: " + node.outerHtml());
        }
    }

    private String getIDOfUser(String user) {
        for (StatusMention mention : status.getMentions()) {
            if (mention.getUsername().equals(user))
                return mention.getId();
        }

        return null;
    }

    private Text addChildren(String text, String cssClass) {
        Text t = new Text(text);
        t.getStyleClass().add(cssClass);
        if(context)
            t.getStyleClass().add("html-text-master-context");
        else
            t.getStyleClass().add("html-text-master");

        getChildren().add(t);

        return t;
    }
}
