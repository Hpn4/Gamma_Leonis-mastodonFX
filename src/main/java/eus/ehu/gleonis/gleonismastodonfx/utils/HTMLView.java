package eus.ehu.gleonis.gleonismastodonfx.utils;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.StatusMention;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class HTMLView extends TextFlow {

    private Status status;

    public HTMLView(Status status, String html) {
        super();
        this.status = status;

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
            else if (node.attr("class").equals("u-url mention")) {
                String user = node.childNode(1).firstChild().toString();
                String accId = getIDOfUser(user);

                addChildren("@" + user, "html-mention").setOnMouseClicked(
                        e -> MainApplication.getInstance().requestShowAccount(accId)
                );
            }
        } else if (type.equals("span") && node.attr("class").equals("h-card"))
            for (Node child : node.childNodes())
                parseNodeToText(child);
        else
            System.err.println("Unknown node type: " + node.outerHtml());
    }

    private String getIDOfUser(String user) {
        for(StatusMention mention : status.getMentions()) {
            if(mention.getUsername().equals(user))
                return mention.getId();
        }

        return null;
    }

    private Text addChildren(String text, String cssClass) {
        Text t = new Text(text);
        t.getStyleClass().add(cssClass);
        getChildren().add(t);

        return t;
    }
}
