# Portlets

PrimeFaces supports portlet environments based on JSF 2 and Portlet 2 APIs. A portlet bridge is
necessary to run a JSF application as a portlet and we suggest LiferayFaces bridge as the
implementation. Both teams work together time to time to make sure PrimeFaces runs well on
liferay. A kickstart example with necessary configuration is available at LiferayFaces Demos;

http://www.liferay.com/community/liferay-projects/liferay-faces/demos

Demo contains a single "Job Application" portlet within the WAR that demonstrates several of the
key features of JSF 2 and PrimeFaces;

- Uses the PrimeFaces <p:calendar/> tag for a popup date selector
- Uses the JSF 2 <f:ajax /> tag on the postal (zip) code field in order to provide the ability to auto-
fill fields via Ajax
- Uses the JSF 2 <f:ajax /> tag on the show/hide comments links in order to show/hide the
comments field via Ajax
- Model managed-bean is marked with the JSF 2 @ViewScoped annotation in order to support a
rich UI with the <f:ajax /> tag
- Uses the JSF 2 <f:ajax /> tag to show navigation-rules executing without full page refreshes
- File upload capabilities via <h:form enctype="multipart/form-data">
- Managed-beans defined by marking POJOs with the JSF 2 @ManagedBean annotation
- Dependency injection of managed-beans done via the JSF 2 @ManagedProperty annotation
- Uses the PrimeFaces p:fileUpload tag for multi-file Ajax-based file upload
- Uses the PrimeFaces p:dataTable tag to list the uploaded files
- Uses the PrimeFaces p:confirmDialog tag to popup a yes/no dialog to verify file deletion
