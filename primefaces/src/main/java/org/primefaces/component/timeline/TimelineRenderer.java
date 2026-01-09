/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.timeline;

import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineGroup;
import org.primefaces.model.timeline.TimelineModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.FastStringWriter;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Timeline.DEFAULT_RENDERER, componentFamily = Timeline.COMPONENT_FAMILY)
public class TimelineRenderer extends CoreRenderer<Timeline> {

    @Override
    public void decode(FacesContext context, Timeline component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Timeline component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, Timeline component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        writer.startElement("div", component);

        writer.writeAttribute("id", clientId, "id");
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        if (component.getStyleClass() != null) {
            writer.writeAttribute("class", component.getStyleClass(), "styleClass");
        }

        UIComponent menuFacet = component.getFacet("menu");
        if (FacetUtils.shouldRenderFacet(menuFacet)) {
            writer.startElement("div", null);
            StringBuilder cssMenu = new StringBuilder("timeline-menu");

            if ("top".equals(component.getOrientationAxis())) {
                cssMenu.append(" timeline-menu-axis-top");
            }
            else if ("both".equals(component.getOrientationAxis())) {
                cssMenu.append(" timeline-menu-axis-both");
            }

            if (ComponentUtils.isRTL(context, component)) {
                cssMenu.append(" timeline-menu-rtl");
            }

            writer.writeAttribute("class", cssMenu.toString(), null);
            //It will be displayed when timeline is displayed
            writer.writeAttribute("style", "display:none;", null);
            menuFacet.encodeAll(context);
            writer.endElement("div");
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Timeline component) throws IOException {
        TimelineModel<Object, Object> model = component.getValue();
        if (model == null) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();

        ZoneId zoneId = CalendarUtils.calculateZoneId(component.getTimeZone());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(zoneId);

        FastStringWriter fsw = new FastStringWriter();
        FastStringWriter fswHtml = new FastStringWriter();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Timeline", component);

        List<TimelineEvent<Object>> events = model.getEvents();
        List<TimelineGroup<Object>> groups = calculateGroupsFromModel(model);
        Map<String, String> groupsContent = null;
        if (!groups.isEmpty()) {
            // buffer for groups' content
            groupsContent = new HashMap<>();
        }

        UIComponent groupFacet = component.getFacet("group");
        int groupsSize = groups.size();
        if (groupFacet != null || groupsSize > 0) {
            if (groupsSize > 0) {
                writer.write(",groups:[");
                for (int i = 0; i < groupsSize; i++) {
                    //If groups was not set in model then order by content.
                    Integer order = model.getGroups() != null ? i : null;
                    //encode groups
                    writer.write(encodeGroup(context, fsw, fswHtml, component, groupFacet, groupsContent, groups.get(i), order));
                    if (i + 1 < groupsSize) {
                        writer.write(",");
                    }
                }
                writer.write("]");
            }
            else {
                writer.write(",groups:[]");
            }
        }

        writer.write(",data:[");
        UIComponent eventTitleFacet = component.getFacet("eventTitle");
        int size = events != null ? events.size() : 0;
        for (int i = 0; i < size; i++) {
            // encode events
            writer.write(encodeEvent(context, fsw, fswHtml, component, eventTitleFacet, zoneId, groups, events.get(i)));
            if (i + 1 < size) {
                writer.write(",");
            }
        }

        writer.write("]");

        if (component.isShowCurrentTime()) {
            wb.nativeAttr("currentTime", encodeDate(dateTimeFormatter, LocalDateTime.now(CalendarUtils.calculateZoneId(component.getClientTimeZone()))));
        }

        if (component.getPreloadFactor() < 0) {
            wb.attr("preloadFactor", 0);
        }
        else {
            wb.attr("preloadFactor", component.getPreloadFactor());
        }

        if (component.getDropHoverStyleClass() != null) {
            wb.attr("hoverClass", component.getDropHoverStyleClass());
        }

        if (component.getDropActiveStyleClass() != null) {
            wb.attr("activeClass", component.getDropActiveStyleClass());
        }

        if (component.getDropAccept() != null) {
            wb.attr("accept", component.getDropAccept());
        }

        if (component.getDropScope() != null) {
            wb.attr("scope", component.getDropScope());
        }

        if (component.getExtender() != null) {
            wb.nativeAttr("extender", component.getExtender());
        }
        UIComponent menuFacet = component.getFacet("menu");
        if (FacetUtils.shouldRenderFacet(menuFacet)) {
            wb.attr("isMenuPresent", Boolean.TRUE);
        }

        writer.write(",opts:{");

        // encode options
        writer.write("autoResize: " + component.isResponsive());
        if (component.getClientTimeZone() != null) {
            ZoneOffset zoneOffset = CalendarUtils.calculateZoneOffset(component.getClientTimeZone());
            if (ZoneOffset.UTC.equals(zoneOffset)) {
                wb.callback("moment", "function(date)", "return moment(date).utc();");
            }
            else {
                wb.callback("moment", "function(date)", "return moment(date).utcOffset('" + EscapeUtils.forJavaScript(zoneOffset.toString()) + "');");
            }
        }
        if (component.getHeight() != null) {
            wb.attr("height", component.getHeight());
        }
        if (component.getMinHeight() != null) {
            wb.attr("minHeight", component.getMinHeight());
        }
        if (component.getMaxHeight() != null) {
            wb.attr("maxHeight", component.getMaxHeight());
        }
        wb.attr("horizontalScroll", component.isHorizontalScroll(), false);
        wb.attr("verticalScroll", component.isVerticalScroll(), false);
        wb.attr("width", component.getWidth());
        wb.nativeAttr("orientation", "{axis:'" + component.getOrientationAxis() + "',"
                + "item:'" + component.getOrientationItem() + "'}" );
        wb.nativeAttr("editable", "{add:" + component.isEditableAdd() + ","
                + "remove:" + component.isEditableRemove() + ","
                + "updateTime:" + component.isEditableTime() + ","
                + "updateGroup:" + component.isEditableGroup() + ","
                + "overrideItems:" + component.isEditableOverrideItems() + "}" );
        wb.attr("selectable", component.isSelectable());

        if (component.getStart() != null) {
            wb.nativeAttr("start", encodeDate(dateTimeFormatter, component.getStart()));
        }

        if (component.getEnd() != null) {
            wb.nativeAttr("end", encodeDate(dateTimeFormatter, component.getEnd()));
        }

        if (component.getMin() != null) {
            wb.nativeAttr("min", encodeDate(dateTimeFormatter, component.getMin()));
        }

        if (component.getMax() != null) {
            wb.nativeAttr("max", encodeDate(dateTimeFormatter, component.getMax()));
        }

        boolean zoomable = component.isZoomable();
        boolean moveable = component.isMoveable();
        wb.attr("zoomable", zoomable);
        wb.attr("moveable", moveable);
        if (zoomable) {
            wb.attr("zoomMin", component.getZoomMin());
            wb.attr("zoomMax", component.getZoomMax());
            if (moveable && LangUtils.isNotBlank(component.getZoomKey())) {
                wb.attr("zoomKey", component.getZoomKey());
            }
        }

        wb.nativeAttr("margin", "{axis:" + component.getEventMarginAxis() + ","
                + "item:{horizontal:" + component.getEventHorizontalMargin() + ","
                + "vertical:" + component.getEventVerticalMargin() + "}}");

        if (component.getEventStyle() != null) {
            wb.attr("type", component.getEventStyle());
        }

        if (component.isGroupsOrder()) {
            //If groups was setted to model then order by order property, else order by content alphabetically
            List<TimelineGroup<Object>> modelGroups = model.getGroups();
            if (modelGroups != null && !modelGroups.isEmpty()) {
                wb.attr("groupOrder", "order");
            }
            else {
                wb.attr("groupOrder", "content");
            }
        }

        if (component.getSnap() != null) {
            wb.nativeAttr("snap", component.getSnap());
        }
        wb.attr("stack", component.isStackEvents());

        wb.attr("showCurrentTime", component.isShowCurrentTime());

        wb.attr("showMajorLabels", component.isShowMajorLabels());
        wb.attr("showMinorLabels", component.isShowMinorLabels());

        wb.attr("locale", component.calculateLocale(context).toString());
        wb.attr("clickToUse", component.isClickToUse());
        wb.attr("showTooltips", component.isShowTooltips());

        wb.nativeAttr("tooltip", "{followMouse:" + component.isTooltipFollowMouse() + ","
                + "overflowMethod:'" + component.getTooltipOverflowMethod() + "',"
                + "delay:" + component.getTooltipDelay() + "}");

        if (ComponentUtils.isRTL(context, component)) {
            wb.attr("rtl", Boolean.TRUE);
        }

        UIComponent loadingFacet = component.getFacet("loading");
        if (FacetUtils.shouldRenderFacet(loadingFacet)) {
            String loading = encodeAllToString(context, writer, fswHtml, loadingFacet);
            // writing facet content's
            wb.nativeAttr("loadingScreenTemplate", "function() { return \"" + EscapeUtils.forJavaScript(loading) + "\";}");
        }

        writer.write("}");
        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected String encodeGroup(FacesContext context, FastStringWriter fsw, FastStringWriter fswHtml, Timeline component, UIComponent groupFacet,
            Map<String, String> groupsContent, TimelineGroup<?> group, Integer order) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        fsw.write("{id: \"" + EscapeUtils.forJavaScriptBlock(group.getId()) + "\"");

        Object data = group.getData();
        if (LangUtils.isNotBlank(component.getVarGroup()) && data != null) {
            context.getExternalContext().getRequestMap().put(component.getVarGroup(), data);
        }
        if (FacetUtils.shouldRenderFacet(groupFacet)) {
            String groupRender = encodeAllToString(context, writer, fswHtml, groupFacet);
            // extract the content of the group, first buffer and then render it
            groupsContent.put(group.getId(), EscapeUtils.forJavaScript(groupRender));
            fsw.write(", content:\"" + groupsContent.get(group.getId()) + "\"");
        }
        else if (data != null) {
            groupsContent.put(group.getId(), EscapeUtils.forJavaScript(data.toString()));
            fsw.write(", content:\"" + groupsContent.get(group.getId()) + "\"");
        }

        if (group.getTreeLevel() != null) {
            fsw.write(", treeLevel:\"" + group.getTreeLevel() + "\"");

            List<String> nestedGroups = group.getNestedGroups();
            if (nestedGroups != null && !nestedGroups.isEmpty()) {
                fsw.write(", nestedGroups: [");

                for (Iterator<String> iter = nestedGroups.iterator(); iter.hasNext(); ) {
                    fsw.write("\"" + EscapeUtils.forJavaScriptBlock(iter.next()) + "\"");

                    if (iter.hasNext()) {
                        fsw.write(",");
                    }
                }

                fsw.write("]");

                if (LangUtils.isNotBlank(group.getShowNested())) {
                    fsw.write(", showNested: " + EscapeUtils.forJavaScript(group.getShowNested()));
                }

                else {
                    fsw.write(", showNested: " + component.isShowNested());
                }
            }
        }

        if (component.getGroupStyle() != null) {
            fsw.write(", style: \"" + component.getGroupStyle() + "\"");
        }

        if (group.getStyleClass() != null) {
            fsw.write(", className: \"" + group.getStyleClass() + "\"");
        }

        if (group.getTitle() != null) {
            fsw.write(", title: \"" + EscapeUtils.forJavaScript(group.getTitle()) + "\"");
        }

        if (order != null) {
            fsw.write(", order: " + order);
        }

        if (LangUtils.isNotBlank(group.getSubgroupOrder())) {
            fsw.write(", subgroupOrder: \"" + EscapeUtils.forJavaScript(group.getSubgroupOrder()) + "\"");
        }

        if (LangUtils.isNotBlank(group.getSubgroupStack())) {
            fsw.write(", subgroupStack: " + EscapeUtils.forJavaScript(group.getSubgroupStack()));
        }

        if (LangUtils.isNotBlank(group.getSubgroupVisibility())) {
            fsw.write(", subgroupVisibility: " + EscapeUtils.forJavaScript(group.getSubgroupVisibility()));
        }

        fsw.write("}");

        String groupJson = fsw.toString();
        fsw.reset();

        return groupJson;
    }

    protected String encodeEvent(FacesContext context, FastStringWriter fsw, FastStringWriter fswHtml, Timeline component,
                              UIComponent eventTitleFacet, ZoneId zoneId, List<TimelineGroup<Object>> groups,
                              TimelineEvent<?> event) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(zoneId);

        fsw.write("{id: \"" + EscapeUtils.forJavaScript(event.getId()) + "\"");

        if (event.getStartDate() != null) {
            fsw.write(", start: " + encodeDate(dateTimeFormatter, event.getStartDate()));
        }
        else {
            fsw.write(", start: null");
        }

        if (event.getEndDate() != null) {
            fsw.write(", end: " + encodeDate(dateTimeFormatter, event.getEndDate()));
        }
        else {
            fsw.write(", end: null");
        }

        if (event.isEditableTime() != null || event.isEditableGroup() != null || event.isEditableRemove() != null) {
            fsw.write(", editable: {");
            boolean isSet = false;
            if (event.isEditableTime() != null) {
                fsw.write("updateTime: " + event.isEditableTime());
                isSet = true;
            }
            if (event.isEditableGroup() != null) {
                if (isSet) {
                    fsw.write(",");
                }
                fsw.write("updateGroup: " + event.isEditableGroup());
                isSet = true;
            }
            if (event.isEditableRemove() != null) {
                if (isSet) {
                    fsw.write(",");
                }
                fsw.write("remove: " + event.isEditableRemove());
            }
            fsw.write("}");
        }

        // there is a list of groups ==> find the group to the event by the group id
        TimelineGroup<?> foundGroup = null;
        if (event.getGroup() != null) {
            for (TimelineGroup<?> group : groups) {
                if (Objects.equals(group.getId(), event.getGroup())) {
                    foundGroup = group;
                    break;
                }
            }
        }

        if (foundGroup != null) {
            fsw.write(", group: \"" + EscapeUtils.forJavaScript(foundGroup.getId()) + "\"");

            if (LangUtils.isNotBlank(event.getSubgroup())) {
                fsw.write(", subgroup: \"" + EscapeUtils.forJavaScript(event.getSubgroup()) + "\"");
            }
        }
        else {
            // no group for the event
            fsw.write(", group: null");
        }

        if (LangUtils.isNotBlank(event.getStyleClass())) {
            fsw.write(", className: \"" + EscapeUtils.forJavaScript(event.getStyleClass()) + "\"");
        }
        else {
            fsw.write(", className: null");
        }

        if (LangUtils.isNotBlank(event.getType())) {
            fsw.write(", type: \"" + EscapeUtils.forJavaScript(event.getType()) + "\"");
        }

        Object data = event.getData();
        if (LangUtils.isNotBlank(component.getVar()) && data != null) {
            context.getExternalContext().getRequestMap().put(component.getVar(), data);
        }

        if (event.getTitle() != null) {
            fsw.write(", title:\"");
            fsw.write(EscapeUtils.forJavaScript(event.getTitle()));
            fsw.write("\"");
        }
        else if (FacetUtils.shouldRenderFacet(eventTitleFacet)) {
            String title = encodeAllToString(context, writer, fswHtml, eventTitleFacet);
            fsw.write(", title:\"");
            fsw.write(EscapeUtils.forJavaScript(title));
            fsw.write("\"");
        }

        fsw.write(", content:\"");
        if (component.getChildCount() > 0) {
            ResponseWriter clonedWriter = writer.cloneWithWriter(fswHtml);
            context.setResponseWriter(clonedWriter);

            renderChildren(context, component);

            // restore writer
            context.setResponseWriter(writer);
            // extract the content of the event
            fsw.write(EscapeUtils.forJavaScript(fswHtml.toString()));
            fswHtml.reset();
        }
        else if (data != null) {
            fsw.write(data.toString());
        }

        fsw.write("\"");
        fsw.write("}");

        String eventJson = fsw.toString();
        fsw.reset();

        return eventJson;
    }

    // convert from UTC to locale date
    private String encodeDate(DateTimeFormatter dateTimeFormatter, LocalDateTime date) {
        String encoded;
        ZonedDateTime zdt = date.atZone(dateTimeFormatter.getZone());
        String formatted = dateTimeFormatter.format(zdt);
        if (formatted.startsWith("-")) {
            // GitHub #6721: B.C. Dates can't use JS constructor with String
            encoded = "new Date(" + zdt.getYear() +
                        ", " + (zdt.getMonthValue() - 1) +
                        ", " + zdt.getDayOfMonth() +
                        ", " + zdt.getHour() +
                        ", " + zdt.getMinute() +
                        ", " + zdt.getSecond() + ", 0)";
        }
        else {
            encoded = "new Date('" + formatted + "')";
        }
        return encoded;
    }

    @Override
    public void encodeChildren(FacesContext context, Timeline component) throws IOException {
        //do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    private String encodeAllToString(FacesContext context, ResponseWriter writer, FastStringWriter fswHtml, UIComponent component) throws IOException {
        ResponseWriter clonedWriter = writer.cloneWithWriter(fswHtml);
        context.setResponseWriter(clonedWriter);

        component.encodeAll(context);
        // restore writer
        context.setResponseWriter(writer);
        String encoded = fswHtml.toString();
        fswHtml.reset();
        return encoded;
    }

    List<TimelineGroup<Object>> calculateGroupsFromModel(TimelineModel<Object, Object> model) {
        List<TimelineGroup<Object>> groups = model.getGroups();
        if (groups != null) {
            return groups;
        }

        return model.getEvents()
                .stream()
                .map(TimelineEvent::getGroup)
                .filter(Objects::nonNull)
                .distinct()
                .map(group -> new TimelineGroup<Object>(group, group))
                .collect(Collectors.toList());
    }
}
