package idm3.project.gallery.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@ValidEmployer
//works? but error due dependency version
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private long userId;

    // database has firstname and surname as fields, however
    // our website does not require that, and as such, the two variables
    // are just initialised without being used (optional).
    private String firstName;
    private String surname;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Column(name = "emailaddress", length = 100)
    private String emailAddress;

    @NotBlank(message = "User type is required")
    @Column(name = "usertype", length = 50)
    private String userType;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3–50 characters")
    @Column(name = "username", length = 50)
    private String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must be at least 5 characters long")
    @Column(name = "password", length = 225)
    private String password;

    @Size(max = 225, message = "Organization cannot exceed 225 characters")
    @Column(name = "organization", length = 225)
    private String organization;

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
