import org.primefaces.model.TreeNode;
import org.primefaces.model.terminal.AutoCompleteMatches;
	
	public static final String CONTAINER_CLASS = "ui-terminal ui-widget ui-widget-content ui-corner-all";
    public static final String WELCOME_MESSAGE_CLASS = "ui-terminal-welcome";
    public static final String CONTENT_CLASS = "ui-terminal-content";
    public static final String PROMPT_CLASS = "ui-terminal-prompt";
    public static final String INPUT_CLASS = "ui-terminal-input";

    public boolean isCommandRequest() {
        FacesContext context = getFacesContext();
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_command");
    }
    public boolean isAutoCompleteRequest() {
        FacesContext context = getFacesContext();
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_autocomplete");
    }
	
	public AutoCompleteMatches traverseCommandModel(TreeNode commandModel, String command, String[] args) {
		AutoCompleteMatches matches = new AutoCompleteMatches();

		for (TreeNode commandNode : commandModel.getChildren()) {
			String currentCommand = (String) commandNode.getData();
			if (currentCommand.equalsIgnoreCase(command)) {
				if (commandNode.getChildCount() > 0) {
					return traverseSubCommands(commandNode, command, args, 0);
				} else {
					matches.addMatch(currentCommand);
				}
			} else if (currentCommand.startsWith(command)) {
				matches.addMatch(currentCommand);
			}
		}

		return matches;
	}

	private AutoCompleteMatches traverseSubCommands(TreeNode commandNode, String command, String[] args, int level) {
		AutoCompleteMatches matches = new AutoCompleteMatches();

		for (TreeNode subCommandNode : commandNode.getChildren()) {
			String currentCommand = (String) subCommandNode.getData();

			if (args.length > level) {
				if (currentCommand.equalsIgnoreCase(args[level])) {
					if (subCommandNode.getChildCount() > 0) {
						return traverseSubCommands(subCommandNode, command + " " + currentCommand, args, ++level);
					} else {
						matches.setBaseCommand(command);
						matches.addMatch(currentCommand);
					}
				} else if (currentCommand.startsWith(args[level])) {
					matches.setBaseCommand(command);
					matches.addMatch(currentCommand);
				}
			} else {
				matches.setBaseCommand(command);
				matches.addMatch(currentCommand);
			}
		}

		return matches;
	}
