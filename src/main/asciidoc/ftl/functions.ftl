|===
| Name | Description

<#list functions as func>
    | ${func.functionSignature?replace("\\w+\\.", "", "r")}
    | ${func.description}
</#list>
|===