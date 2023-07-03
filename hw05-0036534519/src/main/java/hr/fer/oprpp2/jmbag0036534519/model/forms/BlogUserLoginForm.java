package hr.fer.oprpp2.jmbag0036534519.model.forms;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BlogUserLoginForm {
    private String nickname;
    private String password;
    private final Map<String, String> errorMessageMap = new HashMap<>();

    public void fillFromHTTPRequest(HttpServletRequest req) {
        nickname = prepare(req.getParameter("nickname"));
        password = prepare(req.getParameter("password"));
    }

    private static String prepare(String value) {
        if (value == null) {
            return "";
        } else {
            return value.trim();
        }
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

        if (nickname.length() > 100) {
            errorMessageMap.put("nickname", "Nickname too long!");
        }

        if (nickname.isEmpty()) {
            errorMessageMap.put("nickname", "Please provide a nickname");
        }

        if (password.isEmpty()) {
            errorMessageMap.put("password", "Please provide a password");
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
