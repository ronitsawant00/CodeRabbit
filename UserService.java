import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserService.java
 *
 * This class contains the core business logic, a capability of CodeRabbits.
 * It was generated with Javadoc comments and handles basic operations
 * for managing a list of users.
 */
public class UserService {
    private List<User> users;

    /**
     * Constructor for UserService. Initializes the internal list of users.
     */
    public UserService() {
        this.users = new ArrayList<>();
    }

    /**
     * Adds a new user to the service.
     * @param user The User object to add.
        */
    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        // Optional: Check for duplicate IDs
        if (findUserById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("User with ID " + user.getId() + " already exists");
        }
        users.add(user);
    }
    /**
     * Retrieves all users currently in the service.
     * @return A List of all User objects.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Finds a user by their unique ID.
     * @param id The ID of the user to find.
     * @return An Optional containing the User if found, otherwise an empty Optional.
     */
    public Optional<User> findUserById(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
