package com.harry2258.Alfred.api;

import org.pircbotx.hooks.events.MessageEvent;

public abstract class Command {
    private final String name;
    private final String description;
    private final String help;

    /**
     * This constructor isn't recommended as it leaves the entry for the command in the help command rather sparse
     *
     * @param name The name of the command
     * @see Command(String name, String description, String help)
     */
    public Command(String name) {
        this.name = name;
        this.description = "No description available for command " + name;
        this.help = "No help available for command " + name;
    }

    /**
     * The help for the command defaults to: No help available for command <command>
     *
     * @param name        The name of the command
     * @param description A brief description of the command
     * @see Command(String name, String description, String help)
     */
    public Command(String name, String description) {
        this.name = name;
        this.description = description;
        this.help = "No help available for command " + name;
    }

    /**
     * @param name        The name of the command
     * @param description A brief description of the command
     * @param help        Help for the command, usually usage information.
     */
    public Command(String name, String description, String help) {
        this.name = name;
        this.description = description;
        this.help = help;
    }

    /**
     * Get the name of the command
     *
     * @return the command's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the description of the command
     *
     * @return the command's description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Get the syntax / help for the command
     *
     * @return the syntax / help for the command
     */
    public String getHelp() {
        return this.help;
    }


    /**
     * Execute the command
     *
     * @param event The MessageEvent to fire the command with
     */
    public abstract boolean execute(MessageEvent event) throws Exception;

    /**
     * Set the config object to be used by the command
     * Used to pass the config to the class.
     *
     * @param config the config object to pass
     */
    public abstract void setConfig(Config config);

    /**
     * Set the PermissionManager object for the command
     * Used to pass the command a permissions manager
     *
     * @param manager the permissionsmanager instance to pass to the class
     */
    public abstract void setManager(PermissionManager manager);

}
