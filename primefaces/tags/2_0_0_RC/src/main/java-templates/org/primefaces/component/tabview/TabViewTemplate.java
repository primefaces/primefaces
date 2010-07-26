
	public boolean isClientToggling() {
		String toggleMode = getToggleMode();
		
		return (toggleMode != null && toggleMode.equalsIgnoreCase("client"));
	}
	
	public boolean isAsyncToggling() {
		String toggleMode = getToggleMode();
		
		return (toggleMode != null && toggleMode.equalsIgnoreCase("async"));
	}