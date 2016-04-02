package com.example.geojsonsimplifier;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

//Java bean that decomposes a string into the different components of a GeoJSON
public class FeatureCollection {
	
  public String type;
  public List<Feature> features;
  public Map<String, Object> properties;
  public FeatureCollection() {};

}
