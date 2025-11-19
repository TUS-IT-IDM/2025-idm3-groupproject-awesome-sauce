package idm3.project.gallery.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private long userId;

    @Column(name = "FirstName", length = 50)
    private String firstName;

    @Column(name = "surname", length = 225)
    private String surname;

    @Column(name = "emailaddress", length = 100)
    private String emailAddress;

    @Column(name = "usertype", length = 50)
    private String userType;

    @Column(name = "username", length = 50)
    private String userName;

    @Column(name = "password", length = 225)
    private String password;

    @Column(name = "organization", length = 225)
    private String organization;

    // 🖼️ Profile Picture (nullable)
    @Column(name = "ProfilePicture", length = 255)
    private String profilePicture;

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", userType='" + userType + '\'' +
                ", userName='" + userName + '\'' +
                ", organization='" + organization + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                '}';
    }
}
