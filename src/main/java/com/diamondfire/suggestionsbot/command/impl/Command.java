package com.diamondfire.suggestionsbot.command.impl;


import com.diamondfire.suggestionsbot.command.argument.ArgumentSet;
import com.diamondfire.suggestionsbot.command.help.HelpContext;
import com.diamondfire.suggestionsbot.command.permissions.Permission;
import com.diamondfire.suggestionsbot.events.CommandEvent;

public abstract class Command {

    public abstract String getName();

    public String[] getAliases() {
        return new String[0];
    }

    public abstract HelpContext getHelpContext();

    public abstract ArgumentSet getArguments();

    public abstract Permission getPermission();

    public abstract void run(CommandEvent event);
}
