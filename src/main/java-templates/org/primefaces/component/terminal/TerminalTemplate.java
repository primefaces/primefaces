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
	
	AutoCompleteMatches traverseCommandModel(TreeNode commandModel, String baseCommand, String[] args) {
		AutoCompleteMatches matches = new AutoCompleteMatches();

		for (TreeNode commandNode : commandModel.getChildren()) {
			String currentCommand = (String) commandNode.getData();
			
			if (isPartialMatch(currentCommand, baseCommand)) {
				if (isExactMatch(currentCommand, baseCommand) && (commandNode.getChildCount() > 0)) {
					return traverseSubCommands(commandNode, baseCommand, args);
				}
				
				matches.addMatch(currentCommand);
			}
		}

		return matches;
	}
	
	private AutoCompleteMatches traverseSubCommands(TreeNode commandNode, String baseCommand, String[] args) {
		return traverseSubCommands(commandNode, baseCommand, args, 0);
	}

	private AutoCompleteMatches traverseSubCommands(TreeNode commandNode, String baseCommand, String[] args, int level) {
		AutoCompleteMatches matches = new AutoCompleteMatches(baseCommand);

		for (TreeNode subCommandNode : commandNode.getChildren()) {
			String currentSubCommand = (String) subCommandNode.getData();

			if (args.length > level) {
				String currentArgument = args[level];
				if (isPartialMatch(currentSubCommand, currentArgument)) {
					if (isExactMatch(currentSubCommand, currentArgument) && (commandNode.getChildCount() > 0)) {
						String baseCommandToTraverse = baseCommand + " " + currentSubCommand;
						return traverseSubCommands(subCommandNode, baseCommandToTraverse, args, (level + 1));
					}
					
					matches.addMatch(currentSubCommand);
				}
			} else {
				matches.addMatch(currentSubCommand);
			}
		}

		return matches;
	}
	
	private boolean isPartialMatch(String command, String argument) {
		return command.startsWith(argument);
	}
	
	private boolean isExactMatch(String command, String argument) {
		return command.equalsIgnoreCase(argument);
	}
	
	