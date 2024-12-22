package well.com.it.cv.util;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownRenderer {
    private static final MarkdownRenderer INSTANCE = new MarkdownRenderer();
    private final Parser parser;
    private final HtmlRenderer renderer;

    private MarkdownRenderer() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
    }

    public static MarkdownRenderer getInstance() {
        return INSTANCE;
    }

    public String render(String markdown) {
        if (markdown == null) return "";
        return renderer.render(parser.parse(markdown));
    }
} 