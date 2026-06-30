
# FullCalendar

This builds the FullCalendar script that exposes the library as a browser global.

Based on https://github.com/fullcalendar/fullcalendar-example-projects/tree/v5/webpack

## Installation

```bash
cd 
npm install
```

Note: When you run the above command, npm will log the following warning:

```
npm WARN @fullcalendar/moment@5.1.0 requires a peer of moment@^2.24.0 but none is installed. You must install peer dependencies yourself.
npm WARN @fullcalendar/moment-timezone@5.1.0 requires a peer of moment@^2.24.0 but none is installed. You must install peer dependencies yourself.
```

This is OK, because PrimeFaces already includes a version of moment that is
put in the global window scope. The webpack build below just aliases the
moment module to the corresponding browser global.

## Build Commands

```bash
npm run build
```