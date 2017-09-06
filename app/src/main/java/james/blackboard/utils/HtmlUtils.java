package james.blackboard.utils;

import org.jsoup.nodes.Element;

public class HtmlUtils {

    public static String getBasicHtml(Element element) {
        removeUselessElements(element);
        return element.html().trim();
    }

    private static void removeUselessElements(Element element) {
        for (Element child : element.children()) {
            if (child.children().size() > 0)
                removeUselessElements(child);
            else {
                switch (child.tagName()) {
                    case "img":
                        if (child.hasAttr("src") && !child.attr("src").startsWith("/"))
                            break;
                    case "br":
                    case "a":
                    case "p":
                    case "h1":
                    case "h2":
                    case "h3":
                    case "h4":
                    case "span":
                    case "ul":
                    case "li":
                        break;
                    default:
                        Element parent = child.parent();
                        child.remove();
                        parent.insertChildren(0, child.children());
                        break;
                }
            }
        }
    }

    public static void removeUselessAttributes(Element element) {
        for (Element child : element.getAllElements()) {
            if (child.hasAttr("style"))
                child.removeAttr("style");
        }
    }

}
