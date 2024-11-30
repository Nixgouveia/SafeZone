package com.example.tc_projeto.model;

public class WeatherWarning {
    private String text;
    private String awarenessTypeName;
    private String idAreaAviso;
    private String startTime;
    private String awarenessLevelID;
    private String endTime;
    private String latitude; // Adicionado
    private String longitude; // Adicionado

    // Getters and Setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getAwarenessTypeName() { return awarenessTypeName; }
    public void setAwarenessTypeName(String awarenessTypeName) { this.awarenessTypeName = awarenessTypeName; }

    public String getIdAreaAviso() { return idAreaAviso; }
    public void setIdAreaAviso(String idAreaAviso) { this.idAreaAviso = idAreaAviso; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getAwarenessLevelID() { return awarenessLevelID; }
    public void setAwarenessLevelID(String awarenessLevelID) { this.awarenessLevelID = awarenessLevelID; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getLatitude() { return latitude; } // Adicionado
    public void setLatitude(String latitude) { this.latitude = latitude; } // Adicionado

    public String getLongitude() { return longitude; } // Adicionado
    public void setLongitude(String longitude) { this.longitude = longitude; } // Adicionado
}