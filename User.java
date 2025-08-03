
public class User {
    private int id;
    private String name;
    private String email;

    /**
     * No-argument constructor.
     */
    public User() {}

    /**
     * All-argument constructor.
     * @param id The unique identifier for the user.
     * @param name The user's full name.
     * @param email The user's email address.
     */
    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // --- Getters and Setters ---

    /**
     * Gets the user's ID.
     * @return The user's unique identifier.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the user's ID.
     * @param id The new ID for the user.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the user's name.
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     * @param name The new name for the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's email.
     * @return The user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     * @param email The new email for the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Overridden toString() method for easy printing.
     * @return A formatted string representation of the User object.
     */
    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
