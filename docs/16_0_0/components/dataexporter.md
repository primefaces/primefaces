# DataExporter

DataExporter is handy for exporting data listed using a PrimeFaces Datatable to various formats
such as excel, pdf, csv and xml.

## Info

| Name | Value |
| --- | --- |
| Tag | dataExporter
| Tag Class | org.primefaces.component.export.DataExporterTag
| ActionListener Class | org.primefaces.component.export.DataExporter

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
type | null | String | Export type: "xls", "xlsx", "xlsxstream", "pdf", "csv", "csvmemory", "xml", "xmlmemory".
target | null | String | Search expression to resolve one or multiple target components.
fileName | null | String | Filename of the generated export file, defaults to target component id.
pageOnly | false | Boolean | Exports only current page instead of whole dataset.
preProcessor | null | MethodExpr | PreProcessor for the exported document.
postProcessor | null | MethodExpr | PostProcessor for the exported document.
encoding | UTF-8 | String | Character encoding to use.
selectionOnly | false | Boolean | When enabled, only selection would be exported.
visibleOnly | false | Boolean | When enabled, only visible data would be exported.
exportHeader | true | Boolean | When enabled, the header will be exported.
exportFooter | true | Boolean | When enabled, the footer will be exported.
options | null | ExporterOptions | Options object to customize document.
exporter | null | Object | Custom `org.primefaces.component.export.Exporter` to be used instead of built-in exporters.
onTableRender | null | MethodExpression | OnTableRender to be used to set the options of exported table.
onRowExport | null | MethodExpression | Callback every time a row is being exported
bufferSize | null | ValueExpression  | Control how many items are fetched at a time when DataTable#lazy is enabled

## Getting Started with DataExporter

DataExporter is nested in a UICommand component such as commandButton or commandLink.
For PDF exporting **Libre OpenPDF** and for XLS exporting **Apache POI** libraries are required in the classpath. As of today, supported targets are
PrimeFaces `Datatable` and `TreeTable`. Supported types are:
- xls
- pdf
- csv
- xml
- xlsx
- xlsxstream

Assume the table to be exported is defined as:

```xhtml
<p:dataTable id="tableId">
    //columns
</p:dataTable>
```
Excel export (type="xls | xlsx | xlsxstream")

```xhtml
<p:commandButton value="Export as Excel">
    <p:dataExporter type="xls" target="tableId" fileName="cars"/>
</p:commandButton>
```

## PageOnly
By default `<p:dataExporter />` works on whole dataset, if you'd like to export only the data displayed on
current page, set `pageOnly` to true.

```xhtml
<p:dataExporter type="pdf" target="tableId" fileName="cars" pageOnly="true"/>
```
## Excluding Columns
In case you need one or more columns to be ignored set `exportable` option of column to false.

```xhtml
<p:column exportable="false">
    //...
</p:column>
```

## AJAX downloading
Before PrimeFaces 11, you had to disable AJAX with `DataExporter`. As of version 11 that's no longer needed.

## Monitor Status
When `DataExporter` is used without AJAX, ajaxStatus cannot apply. See FileDownload
Monitor Status section to find out how monitor export process. Same solution applies to data export
as well.

## Custom Export
### Column

If you need to customize column export, you can do so by using either `exportValue` or `exportFunction` from `<p:column />`,
`exportFunction` takes the column instance and should return a `String`.

### Custom format

If you need to define a new exporter (e.g. txt, yml etc.), register your exporter as follows:
- implement `DataTableExporter` or `TreeTableExporter`
- register exporter using `DataExporters#register()` specifying the type of component (e.g. `DataTable` or `TreeTable`) and the type with which your exporter is associated.
- A good place to register your exporter could be in a `SystemEventListener` as follows _(to register in faces-config.xml)_:

```java
public class MyAppSystemEventListener implements SystemEventListener {

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        DataExporters.register(DataTable.class, TextExporter.class, "txt");
    }

    @Override
    public boolean isListenerForSource(Object source) {
        return source instanceof Application;
    }
}
```

Here is an example of a custom exporter for DataTable that will export data in txt format:

```java
public class TextExporter extends DataTableExporter<PrintWriter, ExporterOptions> {

    public TextExporter() {
        super(null, Collections.emptySet(), false);
    }

    @Override
    protected PrintWriter createDocument(FacesContext context) throws IOException {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(os(), exportConfiguration.getEncodingType());
            return new PrintWriter(osw);
        }
        catch (UnsupportedEncodingException e) {
            throw new FacesException(e);
        }
    }

    @Override
    protected void exportTable(FacesContext context, DataTable table, int index) throws IOException {
        document.append("").append(table.getId()).append("\n");

        super.exportTable(context, table, index);

        document.append("").append(table.getId());
    }

    @Override
    protected void preRowExport(FacesContext context, DataTable table) {
        (document).append("\t").append(table.getVar()).append("\n");
    }

    @Override
    protected void exportCellValue(FacesContext context, DataTable table, UIColumn col, String text, int index) {
        String columnTag = ExporterUtils.getColumnExportTag(context, col);
        document.append("\t\t")
                .append(columnTag)
                .append(": ")
                .append(EscapeUtils.forXml(text))
                .append("\n");
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    @Override
    public String getFileExtension() {
        return ".txt";
    }
}
```

Or, by using `DataExporter#exporter` attribute:

> :warning: **DataExporter#exporter is deprecated in 13.0.0 and will be removed in 14.0.0**


```xhtml
<p:commandButton value="Export as Text">
    <p:dataExporter type="text" target="tableId" fileName="cars" exporter="#{dataExporterView.textExporter}"/>
</p:commandLink>
```

```java
public class DataExporterView implements Serializable {
    
    private Exporter<DataTable> textExporter;
        
    @PostConstruct
    public void init() {
        textExporter = new TextExporter();
    }

	public Exporter<DataTable> getTextExporter() {
        return textExporter;
    }

    public void setTextExporter(Exporter<DataTable> textExporter) {
        this.textExporter = textExporter;
    }
}    
```

## Pre and Post Processors
Processors are handy to customize the exported document (e.g. add logo, caption ...). PreProcessors
are executed before the data is exported and PostProcessors are processed after data is included in
the document. Processors are simple java methods taking the document as a parameter.

#### Change Excel Table Header

First example of processors changes the background color of the exported excel’s headers.

```xhtml
<h:commandButton value="Export as XLS">
<p:dataExporter type="xls" target="tableId" fileName="cars" postProcessor="#{bean.postProcessXLS}"/>
</h:commandButton>
```
```java
public void postProcessXLS(Object document) {
    HSSFWorkbook wb = (HSSFWorkbook) document;
    HSSFSheet sheet = wb.getSheetAt(0);
    HSSFRow header = sheet.getRow(0);
    HSSFCellStyle cellStyle = wb.createCellStyle();
    cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

    for(int i=0; i < header.getPhysicalNumberOfCells();i++) {
        header.getCell(i).setCellStyle(cellStyle);
    }
}
```
#### Add Logo to PDF

This example adds a logo to the PDF before exporting begins.

```xhtml
<h:commandButton value="Export as PDF">
    <p:dataExporter type="pdf" target="tableId" fileName="cars" preProcessor="#{bean.preProcessPDF}"/>
</h:commandButton>
```
```java
public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
    Document pdf = (Document) document;
    ServletContext servletContext = (ServletContext)
    FacesContext.getCurrentInstance().getExternalContext().getContext();
    String logo = servletContext.getRealPath("") + File.separator + "images" +
    File.separator + "prime_logo.png";
    pdf.add(Image.getInstance(logo));
}
```
#### Escape Formulas in a CSV

This example escapes formulas in a CSV, please note this requires the `type` of `csvmemory`.

```xhtml
<h:commandButton value="Export as CSV">
    <p:dataExporter type="csvmemory" target="tableId" fileName="cars" postProcessor="#{bean.postProcessCSV}"/>
</h:commandButton>
```
```java
public void postProcessCSV(Object document) {
    StringBuilder csv = (StringBuilder) document;
    String content = csv.toString();
    String newContent = content.replace("\"=", "\"'=");
    csv.replace(0, csv.length(), newContent);
}
```
## Customization
Excel and PDF documents can be further customized using exporterOptions property that takes a
configuration object that implements _ExporterOptions_.

```xhtml
<h:commandButton value="Export as XLS">
    <p:dataExporter type="xls" target="tableId" fileName="cars" options="#{customizedDocumentsView.excelOpt}"/>
</h:commandButton>
```
```java
public class CustomizedDocumentsView implements Serializable {
    private ExcelOptions excelOpt;

    @PostConstruct
    public void init() {
        excelOpt = new ExcelOptions();
        excelOpt.setFacetBgColor("#F88017");
        excelOpt.setFacetFontSize("10");
        excelOpt.setFacetFontColor("#0000ff");
        excelOpt.setFacetFontStyle("BOLD");
        excelOpt.setCellFontColor("#00ff00");
        excelOpt.setCellFontSize("8");
        excelOpt.setStronglyTypedCells(true);
        excelOpt.setNumberFormat(new DecimalFormat("#,##0.00"));
        excelOpt.setCurrencyFormat(DecimalFormat.getCurrencyInstance(new Locale("es", "US")));
    }

    public ExcelOptions getExcelOpt() {
        return excelOpt;
    }
}
```
