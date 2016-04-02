package com.example.geojsonsimplifier;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.utils.SystemProperty;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.NullPointerException;
import javax.inject.Named;
import java.util.List;
import com.owlike.genson.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.google.appengine.api.taskqueue.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MyTask implements DeferredTask {

 public static volatile String gj;
 public String group; 
 public String username; 

 public MyTask(String group,String username) {
  this.gj=gj;
  this.group=group;
  this.username=username;
 
 }
 
 // this method is called once the task has been passed on the Queue and his being processed
 @Override
 public void run() {

FeatureCollection jsonString=null;
Gson gson = new Gson();
try {

  //Datastore specification of the Entity to be saved
   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
   jsonString = gson.fromJson(gj, FeatureCollection.class);
   Key featureKey = KeyFactory.createKey("GeoJsonText", username);
   System.out.println("Before loop");
   //Iterates through each feature of the GeoJSON file seperating them between coordinates, type and metadata
   for (int i = 0; i < jsonString.features.size(); i++) {
    System.out.println("Not null ");
    if (jsonString.features.get(i).geometry != null) {

    	

     //extracts all the information from each Feature: Metadata and vertices
     String featureType = jsonString.features.get(i).geometry.type.toString();
     String stringCoordinate = jsonString.features.get(i).geometry.coordinates.toString();
     Text jsonText = new Text(stringCoordinate);
     System.out.println("Before Entity");
     //The Entity that will be saved to Datastore and it's properties 
     Entity geoJsonEntity = new Entity("GeoJsonText", featureKey);
     geoJsonEntity.setProperty("grouptag", group);
     geoJsonEntity.setProperty("id", jsonString.features.get(i).toString());
     geoJsonEntity.setProperty("geometryType", featureType);
     System.out.println("Before vertices");
     //The vertices are simplified 8 times and saved in seperated propertoes
     for (int n = 0; n < 8; n++) {
      System.out.println("Simplification number " + n);
      geoJsonEntity.setProperty("geometryCoordinateSimplified" + n, simplifys(stringCoordinate, n, featureType));

     }
     System.out.println("After vertices");
     geoJsonEntity.setProperty("properties", jsonString.features.get(i).properties.toString());
     datastore.put(geoJsonEntity);
     System.out.println("Stored");
    
}
   }
  }
 catch (Exception e) {
 jsonString.type="ERROR "+e;
}

 }

public void setGeoJson(String gj){
  this.gj=gj;
}


//This method handles the simplification of different geometry types: Polygon and MultiPolygon
 private Text simplifys(String featureCoordinates, int nSimplifications, String type) {

  //Gson is the desrialiser used
  Gson gson = new Gson();
  //The string of characters of the simplified vertices that will be returned
  Text simplejsonText = null;

  if (type.equals("Polygon")) {

   //Only one Polyon added to the String
   simplejsonText = new Text(gson.toJson(simplifyPolygon(featureCoordinates, nSimplifications), List.class));

  } else if (type.equals("MultiPolygon")) {

   //Deserializes the MultiPolygon into an array so that each seperate Polygon can be simplified
   List < ArrayList < double[][] > > verticesList = gson.fromJson(featureCoordinates, List.class);
   //List that will hold the simplified Polygons to then be transformed into strings
   List < List < double[][] > > simplejsonVertices = new ArrayList < List < double[][] > > ();

   for (int e = 0; e < verticesList.size(); e++) {

    List vertices = gson.fromJson(verticesList.get(e) + "", ArrayList.class);
    simplejsonVertices.add(simplifyPolygon(vertices.toString(), nSimplifications));
   }

   simplejsonText = new Text(gson.toJson(simplejsonVertices, List.class));

  } else if (type.equals("Point")) {
   simplejsonText = new Text(featureCoordinates);
  } else if (type.equals("MultiPoint")) {
   simplejsonText = new Text(featureCoordinates);
  } else if (type.equals("LineString")) {
   simplejsonText = new Text(featureCoordinates);
  } else if (type.equals("MultiLineString")) {
   simplejsonText = new Text(featureCoordinates);
  }

  //System.out.println("Simplification "+nSimplifications+simplejsonText.getValue());
  return simplejsonText;
 }





 // This method simplifies the vertices of a given Polygon to the specified degree
 private List < double[][] > simplifyPolygon(String featureCoordinates, int nSimplifications) {

  Gson gson = new Gson();
  //This List holds the dat of the vertices
  List < double[][] > verticesList = gson.fromJson(featureCoordinates, List.class);
  //This List will be the one returned by the method 
  List < double[][] > finalVerticesList = new ArrayList < double[][] > ();

  //Iterates through each set of vertices that includes iner holes
  for (int r = 0; r < verticesList.size(); r++) {

   double[][] vertices = gson.fromJson(verticesList.get(r) + "", double[][].class);

   /*
    *
    *  Java Topology Suit to simplify vertices needs to have a supported special format.
    *  The next section of code will convert an array of coordinates into the data structure suported by JTS
    *
    */

   Coordinate[] coordinateJTS = new Coordinate[vertices.length];
   int j = 0;

   for (double[] point: vertices) {
    Coordinate coordinate = new Coordinate(point[0], point[1]);
    coordinateJTS[j++] = coordinate;
   }

   GeometryFactory geometryFactory = new GeometryFactory();
   LinearRing linearRing = new GeometryFactory().createLinearRing(coordinateJTS);
   Polygon polygon = new Polygon(linearRing, null, geometryFactory);

   com.vividsolutions.jts.geom.Geometry geometryJTS = new GeometryFactory().createLineString(coordinateJTS);
   //The Geometry created is simplified and restored in the same variable
   geometryJTS = TopologyPreservingSimplifier.simplify(polygon, 0.1 * nSimplifications);
   Coordinate[] simplified = geometryJTS.getCoordinates();

   /*
    *
    *  The List of coordinates that will be returned is now reconstructed from the simplified Geometry
    *  The inverse process happens to the one previously conducted 
    *
    */

   j = 0;
   vertices = new double[simplified.length][2];
   for (Coordinate coordinatepair: simplified) {
    vertices[j][0] = coordinatepair.x;
    vertices[j][1] = coordinatepair.y;
    j++;
   }
   finalVerticesList.add(vertices);

  }
  return finalVerticesList;

 }


}
