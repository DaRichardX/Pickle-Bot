import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.*;
import java.util.logging.Level;

public abstract class Command {

    public final String NAME;

    public final String USAGE;

    public String description;

    public PermissionType[] permissions;

    protected String permissionErrorMessage = "You don't have permission to run this command!";

    protected String genericErrorMessage = "An error occurred while attempting to run the command.";

    protected MessageCreateEvent event;

    /**
     * Constructs the command.
     *
     * @param name        the command name
     * @param usage       the command usage
     * @param description the command description
     * @param permissions the command permissions; can be null if anyone can use the command
     */
    public Command(String name, String usage, String description, PermissionType[] permissions) {
        Main.log(Level.INFO, "Initializing command " + name);
        NAME = name;
        if (usage == null) {
            USAGE = Main.PREFIX + NAME;
        } else {
            USAGE = Main.PREFIX + NAME + ' ' + usage;
        }
        this.description = description;
        this.permissions = permissions;
        if (this.permissions != null && this.permissions.length == 0) {
            this.permissions = null;
        }
        Main.log(Level.INFO, "Done initializing " + name);
    }

    /**
     * Checks the required permissions for a user before running the command.
     *
     * @param event the message create event
     */
    public void checkPermissions(MessageCreateEvent event) {
        String userName = event.getMessageAuthor().getDisplayName() + "/" + event.getMessageAuthor().getDiscriminatedName();
        Main.log(Level.INFO, NAME + " called");

        this.event = event;
        if (permissions != null) {
            Optional<User> userOptional = event.getMessageAuthor().asUser();
            Optional<Server> serverOptional = event.getServer();
            if (!userOptional.isPresent() || !serverOptional.isPresent()) {
                Main.log(Level.SEVERE, "userOptional or serverOptional is empty; " +
                        "empty optionals: userOptional=" + !userOptional.isPresent() +
                        ", serverOptional=" + !serverOptional.isPresent());
                event.getMessage().reply(genericErrorMessage);
                return;
            }
            User user = userOptional.get();
            Server server = serverOptional.get();

            List<Role> roles = user.getRoles(server);
            Set<PermissionType> userPermissions = new HashSet<>();
            for (Role role : roles) {
                userPermissions.addAll(role.getAllowedPermissions());
            }

            if (!userPermissions.containsAll(Arrays.asList(permissions)) && !userPermissions.contains(PermissionType.ADMINISTRATOR)) {
                Main.log(Level.WARNING, "Permission check failed for " + userName);
                event.getMessage().reply(permissionErrorMessage);
                return;
            }
        }

        Main.log(Level.INFO, "Permission check passed for " + userName + "; now running " + NAME);
        onCommand();
        Main.log(Level.INFO, NAME + " has finished running");
    }

    /**
     * Called when a command is sent with the sufficient permissions.
     */
    abstract void onCommand();

    /**
     * Checks to see if a user has provided enough arguments.
     * @param parts the split message to check for the arguments
     * @param argumentCount the required total number of arguments
     * @return {@code true} if the user has not provided enough arguments
     */
    protected final boolean notEnoughArguments(String[] parts, int argumentCount) {
        if (parts.length - 1 < argumentCount) {
            event.getMessage().reply("Expected " + argumentCount + " arguments but got " + (parts.length - 1) + " instead.");
            return true;
        }

        return false;
    }
}