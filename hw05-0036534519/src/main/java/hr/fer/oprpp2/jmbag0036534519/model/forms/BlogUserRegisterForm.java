package hr.fer.oprpp2.jmbag0036534519.model.forms;

import hr.fer.oprpp2.jmbag0036534519.model.BlogUser;
import hr.fer.oprpp2.jmbag0036534519.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BlogUserRegisterForm {
    private String firstName;
    private String lastName;
    private String nickname;
    private String email;
    private String password;
    private final Map<String, String> errorMessageMap = new HashMap<>();

    public void fillFromHTTPRequest(HttpServletRequest req) {
        firstName = prepare(req.getParameter("firstName"));
        lastName = prepare(req.getParameter("lastName"));
        nickname = prepare(req.getParameter("nickname"));
        email = prepare(req.getParameter("email"));
        password = prepare(req.getParameter("password"));
    }

    private static String prepare(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }

    public void validate() {
        errorMessageMap.clear();

        if (firstName.isEmpty()) {
            errorMessageMap.put("firstName", "Please provide first name");
        }

        if (lastName.isEmpty()) {
            errorMessageMap.put("lastName", "Please provide last name");
        }

        if (email.isEmpty()) {
            errorMessageMap.put("email", "Please provide email");
        } else {
            int i = email.indexOf("@");
            int l = email.length();
            if (l < 3 || i == -1 || i == 0 || i == l - 1) {
                errorMessageMap.put("email", "Incorrect email format");
            }
        }

        if (nickname.isEmpty()) {
            errorMessageMap.put("nickname", "Please provide nickname");
        }

        if (password.isEmpty()) {
            errorMessageMap.put("password", "Please provide password");
        }
        else if (password.length() < 8) {
            errorMessageMap.put("password", "Password must be at least 8 characters long");
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

    public void fillUser(BlogUser user) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPasswordHash(Util.getSHA1Digest(password));
    }

    public void setError(String error, String message) {
        errorMessageMap.put(error, message);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
