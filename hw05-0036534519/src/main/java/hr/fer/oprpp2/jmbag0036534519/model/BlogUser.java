package hr.fer.oprpp2.jmbag0036534519.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(
                name = "findBlogUserByNickname",
                query = "SELECT user FROM BlogUser AS user WHERE user.nickname = :userNickname"
        )
})
@Entity
@Table(name="blog_users")
@Cacheable(true)
public class BlogUser {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, length = 100)
    private String firstName;
    @Column(nullable = false, length = 100)
    private String lastName;
    @Column(nullable = false, length = 100, unique = true)
    private String nickname;
    @Column(nullable = false, length = 100)
    private String email;
    @Column(nullable = false, length = 40)
    private String passwordHash;
    @OneToMany(mappedBy="creator",fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, orphanRemoval=true)
    @OrderBy("title")
    private List<BlogEntry> blogs = new ArrayList<>();

    public Long getId() {
        return id;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public List<BlogEntry> getBlogs() {
        return blogs;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setBlogs(List<BlogEntry> blogs) {
        this.blogs = blogs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlogUser blogUser = (BlogUser) o;
        return Objects.equals(id, blogUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
