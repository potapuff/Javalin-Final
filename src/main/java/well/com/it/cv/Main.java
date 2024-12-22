package well.com.it.cv;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.javalin.security.BasicAuthCredentials;
import well.com.it.cv.dao.ContentDao;
import well.com.it.cv.util.MarkdownRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.util.HashMap;
import java.util.Optional;

public class Main {
    private static final String USERNAME = getRequiredEnv("CV_ADMIN_USERNAME");
    private static final String PASSWORD = getRequiredEnv("CV_ADMIN_PASSWORD");
    private static final MarkdownRenderer markdownRenderer = MarkdownRenderer.getInstance();

    private static String getRequiredEnv(String name) {
        return Optional.ofNullable(System.getenv(name))
            .orElseThrow(() -> new RuntimeException(
                "Environment variable " + name + " is required but not set. " +
                "Please set it before starting the application."
            ));
    }

    public static void main(String[] args) {
        try {
            startApplication();
        } catch (RuntimeException e) {
            System.err.println("Failed to start application: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void startApplication() {
        ContentDao contentDao = new ContentDao();
        contentDao.initializeDb();

        // Configure Thymeleaf
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.fileRenderer(new JavalinThymeleaf(templateEngine));
        }).start(7070);

        // Add common model attributes before any render
        app.before(ctx -> {
            if (ctx.method().equals(HandlerType.GET)) {
                var model = getCommonModel(ctx);
                model.put("showEdit", ctx.queryParam("edit") != null);
                ctx.attribute("commonModel", model);
            }
        });

        // Basic authentication for admin routes
        app.before("/admin/*", ctx -> {
            BasicAuthCredentials credentials = ctx.basicAuthCredentials();
            if (credentials == null || 
                !credentials.getUsername().equals(USERNAME) ||
                !credentials.getPassword().equals(PASSWORD)) {
                ctx.header("WWW-Authenticate", "Basic");
                ctx.status(401);
            }
        });

        // Routes
        app.get("/", ctx -> {
            var model = getCommonModel(ctx);
            var contents = contentDao.getAllContent();
            var renderedContents = contents.stream()
                .map(content -> new HashMap<String, Object>() {{
                    put("id", content.getId());
                    put("section", content.getSection());
                    put("content", content.getContent());
                    put("renderedContent", markdownRenderer.render(content.getContent()));
                }})
                .toList();
            model.put("contents", renderedContents);
            model.put("title", "My CV");
            ctx.render("layout.html", model);
        });

        app.get("/admin/edit/{id}", ctx -> {
            var model = getCommonModel(ctx);
            var content = contentDao.getContent(ctx.pathParam("id"));
            if (content != null) {
                model.put("content", content);
                model.put("renderedContent", markdownRenderer.render(content.getContent()));
            }
            model.put("editing", true);
            ctx.render("layout.html", model);
        });

        app.post("/admin/update/{id}", ctx -> {
            contentDao.updateContent(
                ctx.pathParam("id"), 
                ctx.formParam("content")
            );
            ctx.redirect("/");
        });

        // Error handlers
        app.error(404, ctx -> {
            var model = getCommonModel(ctx);
            model.put("error", "Page not found");
            model.put("errorImage", "/images/404.png");
            ctx.render("layout.html", model);
        });
        
        app.error(500, ctx -> {
            var model = getCommonModel(ctx);
            model.put("error", "Internal server error");
            model.put("errorImage", "/images/500.png");
            ctx.render("layout.html", model);
        });
    }

    private static boolean isAdmin(Context ctx) {
        BasicAuthCredentials credentials = ctx.basicAuthCredentials();
        return credentials != null && 
               credentials.getUsername().equals(USERNAME) && 
               credentials.getPassword().equals(PASSWORD);
    }

    @SuppressWarnings("unchecked")
    private static HashMap<String, Object> getCommonModel(Context ctx) {
        if (ctx.attribute("commonModel") == null) {
            ctx.attribute("commonModel", new HashMap<String, Object>());
        }
        return ctx.attribute("commonModel");
    }
} 