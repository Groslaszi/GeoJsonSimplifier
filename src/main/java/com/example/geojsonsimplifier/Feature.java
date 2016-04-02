package com.example.geojsonsimplifier;

import java.util.HashMap;
import java.util.Map;

//The java bean that composes each feature of a GeoJson object
public class Feature {
  public String type;
  public Geometry geometry;
  public Object properties;

  public Feature() {};
}
