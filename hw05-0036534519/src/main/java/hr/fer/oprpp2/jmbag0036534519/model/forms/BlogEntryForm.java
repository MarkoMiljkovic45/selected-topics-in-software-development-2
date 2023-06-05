package hr.fer.oprpp2.jmbag0036534519.model.forms;

import hr.fer.oprpp2.jmbag0036534519.model.BlogEntry;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BlogEntryForm {
    private String title;
    private String text;
    private final Map<String, String> errorMessageMap = new HashMap<>();

    public void fillFromHTTPRequest(HttpServletRequest req) {
        title = prepare(req.getParameter("title"));
        text = prepare(req.getParameter("text"));
    }

    private static String prepare(String value) {
        if (value == null ) {
            return "";
        }

        return value.trim();
    }

    public boolean hasErrors() {
        return !errorMessageMap.isEmpty();
    }

    public boolean hasError(String arg) {
        return errorMessageMap.containsKey(arg);
    }

    public String getErrorMessage(String arg) {
        return errorMessageMap.get(arg);
    }

    public void validate() {
        errorMessageMap.clear();

        if (title.isEmpty()) {
            errorMessageMap.put("title", "Please provide a title");
        }

        if (text.isEmpty()) {
            errorMessageMap.put("text", "Pleas provide blog entry content");
        }
    }

    public void fillBlogEntry(BlogEntry blogEntry) {
        blogEntry.setTitle(title);
        blogEntry.setText(text);
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }
}
