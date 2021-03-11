# Cookies

This a list of Cookies normally used by PrimeFaces and the container.

## Servlet
| Name | HttpOnly | Value | Description | Alternative |
| --- | --- | --- | --- | --- |
| JSESSIONID | true | UID | Id of the current session | Switch to URL tracking or make your application stateless |

## MyFaces
| Name | HttpOnly | Value | Description | Alternative |
| --- | --- | --- | --- | --- |
| oam.Flash* | true |  | Used for the Flash Scope | Disable Flash completely with org.apache.myfaces.FLASH_SCOPE_DISABLED=true |

## Mojarra
| Name | HttpOnly | Value | Description | Alternative |
| --- | --- | --- | --- | --- |
| csfcfc | true | UID | Used for the Flash Scope | - |

## PrimeFaces
| Name | HttpOnly | Value | Description | Alternative |
| --- | --- | --- | --- | --- |
| primefaces.download | false | Boolean | Used by monitor download | - |
| pf.initialredirect-* | false | Boolean | Used by PrimeFaces ClientWindow | - |
