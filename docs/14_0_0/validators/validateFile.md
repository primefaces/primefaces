# validateFile

`p:validateFile` is a validator which can be used to validate single and mulitple files, either of `p:fileUpload` or `h:inputFile`.

## Info

| Name             | Value                                         |
|------------------|-----------------------------------------------|
| Tag              | validateFile                                  |
| Validator Class  | org.primefaces.validate.FileValidator         |
| Validator Id     | primefaces.File                               |

## Attributes

| Name           | Default | Type    | Description     |
|----------------| ------- |---------| ----------------- |
| allowTypes     | null    | String  | Javascript regular expression for accepted file types, e.g. /(\.|\/)(gif|jpe?g|png)$/
| fileLimit      | null    | Integer | Maximum number of files to be uploaded.
| sizeLimit      | null    | Long    | Individual file size limit in bytes. Default is unlimited.
| contentType    | null    | Boolean | Whether the contentType should be validated based on the accept attribute of the attached component. Default is false.
| virusScan      | null    | Boolean | Whether virus scan should be performed. Default is false.

## Getting Started
Either attach it to `p:fileUpload` or `h:inputFile`. Even CSV is supported:

```html
<h:form enctype="multipart/form-data">
    <p:fileUpload value="#{fileUploadView.file}" mode="simple" skinSimple="true">
        <p:validateFile allowTypes="/(\.|\/)(pdf)$/" />
    </p:fileUpload>

    <p:commandButton value="Submit" action="#{fileUploadView.upload}"
                     validateClient="true" process="@form" update="messages"/>
</h:form>
```
