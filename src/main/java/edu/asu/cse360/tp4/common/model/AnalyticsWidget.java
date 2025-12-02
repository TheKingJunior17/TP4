package edu.asu.cse360.tp4.common.model;

import java.util.Map;

/**
 * Dashboard widget model for interactive analytics display.
 * 
 * @author TP4 Team
 * @version 2.0.0
 * @since TP4 Implementation Phase
 */
public class AnalyticsWidget {
    
    private final String id;
    private final String title;
    private final Map<String, Object> data;
    private final WidgetType type;
    
    public AnalyticsWidget(String id, String title, Map<String, Object> data, WidgetType type) {
        this.id = id;
        this.title = title;
        this.data = data;
        this.type = type;
    }
    
    public String getId() { return id; }
    public String getTitle() { return title; }
    public Map<String, Object> getData() { return data; }
    public WidgetType getType() { return type; }
    
    public enum WidgetType { CHART, PROGRESS_BAR, LIST, TREND_CHART, ALERT }
}