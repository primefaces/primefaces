# validateFile

`p:validateFile` is a validator which can be used to validate single and multiple files, either of `p:fileUpload` or `h:inputFile`.
By default it will validate on the server side but you can enable [client side validation (CSV)](/core/csv.md) to avoid roundtrips to the server.

## Info

| Name             | Value                                         |
|------------------|-----------------------------------------------|
| Tag              | validateFile                                  |
| Validator Class  | org.primefaces.validate.FileValidator         |
| Validator Id     | primefaces.File                               |

## Attributes

| Name            | Default | Type    | Description                                                                                                            |
|-----------------|---------|---------|------------------------------------------------------------------------------------------------------------------------|
| allowTypes      | null    | String  | Javascript regular expression for accepted file types, e.g., /(\.|\/)(gif|jpeg|jpg|png)$/                              |
| fileLimit       | null    | Integer | Maximum number of files to be uploaded.                                                                                |
| sizeLimit       | null    | Long    | Individual file size limit in bytes. Default is unlimited.                                                             |
| contentType     | null    | Boolean | Whether the contentType should be validated based on the accept attribute of the attached component. Default is false. |
| virusScan       | null    | Boolean | Whether virus scan should be performed. Default is false.                                                              |
| allowMediaTypes | null    | String  | Comma-separated list of allowed media types for content type validation (takes precedence over the accept attribute),  |
|                 |         |         | e.g. application/pdf,image/jpeg,image/png                                                                              |

## Getting Started
Either attach it to `p:fileUpload` or `h:inputFile`. Even [client side validation (CSV)](/core/csv.md) is supported:

```html
<h:form enctype="multipart/form-data">
    <p:fileUpload value="#{fileUploadView.file}" mode="simple" skinSimple="true">
        <p:validateFile allowTypes="/(\.|\/)(pdf)$/" />
    </p:fileUpload>

    <p:commandButton value="Submit" action="#{fileUploadView.upload}"
                     validateClient="true" process="@form" update="messages"/>
</h:form>
```
