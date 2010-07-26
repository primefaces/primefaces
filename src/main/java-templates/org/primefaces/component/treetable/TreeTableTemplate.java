import org.primefaces.model.TreeTableModel;
import org.primefaces.model.TreeNode;
import javax.faces.model.DataModel;

	public final static String CONTAINER_CLASS = "ui-treetable ui-widget ui-widget-content";
	public final static String HEADER_CLASS = "ui-treetable-header";
	public final static String HEADER_LABEL_CLASS = "ui-treetable-header-label";
	public final static String DATA_CLASS = "ui-treetable-data";

	public String getEscapedClientId(FacesContext facesContext) {
		return this.getClientId(facesContext).replaceAll(String.valueOf(javax.faces.component.UINamingContainer.getSeparatorChar(facesContext)), "_");
	}
	
	private TreeTableModel model;
	
	@Override
	protected DataModel getDataModel() {
		if(model == null) {
			model = new TreeTableModel((TreeNode) getValue());
		}
		
		return model;
	}
	
	@Override
	protected void setDataModel(DataModel model) {
		this.model = (TreeTableModel) model;
	}