package com.example.geojsonsimplifier;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.users.User;

import java.util.ArrayList;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import javax.inject.Named;

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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.google.appengine.api.taskqueue.*;
import com.owlike.genson.*;
import com.google.appengine.api.datastore.PreparedQuery;
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
import java.lang.Thread;

/**
 * Defines v1 of a helloworld API, which provides simple "greeting" methods.
 */
@Api(
 name = "helloworld",
 version = "v1",
 scopes = {
  Constants.EMAIL_SCOPE
 },
 clientIds = {
  Constants.WEB_CLIENT_ID,
  Constants.ANDROID_CLIENT_ID,
  Constants.IOS_CLIENT_ID,
  Constants.API_EXPLORER_CLIENT_ID
 },
 audiences = {
  Constants.ANDROID_AUDIENCE
 }

)
public class Greetings {

 public static ArrayList < HelloGreeting > greetings = new ArrayList < HelloGreeting > ();

 static {
  greetings.add(new HelloGreeting("hello world!"));
  greetings.add(new HelloGreeting("goodbye world!"));
 }


 public HelloGreeting listDataSets(@Named("username") String username) throws NotFoundException {
  HelloGreeting response = new HelloGreeting();
  response.setMessage("nothing");
  List < String > datasetNames = new ArrayList < String > ();
  try {

   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

   //Retrives the entity saved under the key specfied during the insertion
   Key featureKey = KeyFactory.createKey("GeoJsonText", username);

  
   Query query = new Query("GeoJsonText", featureKey);
   PreparedQuery pq = datastore.prepare(query);

   //Iterates through the Entities returned by the query to retrive each feature
   for (Entity result: pq.asIterable()) {
    datasetNames.add(result.getProperty("grouptag").toString());
   }
  } catch (Exception e) {
   response.setMessage(e + " Error");
   throw new NotFoundException("Greeting not found with an index: ");
  }

  if (datasetNames.size() > 0) {
   response.setMessage(datasetNames);
  }
  return response;
 }




/*
 *
 * GET GEOJSONS
 *
 */




 public HelloGreeting getGreeting(@Named("group") String group, @Named("zoom") int zoom, @Named("username") String username) throws NotFoundException {
  HelloGreeting response = new HelloGreeting();

  try {

   //The object that will be returned by the endpoint
   FeatureCollection newGeojson = new FeatureCollection();
   newGeojson.type = "FeatureCollection";
   List < Feature > featureList = new ArrayList < Feature > ();

   //Services required by the method
   Gson gson = new Gson();
   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

   //Retrives the entity saved under the key specfied during the insertion
   Key featureKey = KeyFactory.createKey("GeoJsonText", username);

   Filter propertyFilter = new FilterPredicate("grouptag",
    FilterOperator.EQUAL,
    group);
   Query query = new Query("GeoJsonText", featureKey).setFilter(propertyFilter);
   PreparedQuery pq = datastore.prepare(query);

   int levelToFetch = zoom;
   //Iterates through the Entities returned by the query to retrive each feature
   for (Entity result: pq.asIterable()) {

    //All the data to extract
    Text geoje = (Text) result.getProperty("geometryCoordinateSimplified" + levelToFetch);
    List < double[][] > vertices = gson.fromJson(geoje.getValue(), List.class);
    String type = result.getProperty("geometryType").toString();

    //Assembladge of the Geometries
    com.example.geojsonsimplifier.Geometry geom = new com.example.geojsonsimplifier.Geometry();
    geom.type = type;
    geom.coordinates = vertices;

    Map < String, Object > propertiesGeometry = new HashMap < String, Object > ();
    propertiesGeometry.put("Property", result.getProperty("properties"));

    //Assembladge of the Feature
    Feature feature = new Feature();
    feature.type = "Feature";
    feature.geometry = geom;
    feature.properties = propertiesGeometry;
    featureList.add(feature);

   }
   newGeojson.features = featureList;
   // response.setMessage(gson.toJson(newGeojson,FeatureCollection.class));
   response.setMessage(newGeojson);
  } catch (Exception e) {
   response.setMessage(e + " Error");
   throw new NotFoundException("Greeting not found with an index: ");
  }

  return response;
 }





/*
 *
 * STORE and POST GEOJSONS
 *
 */




 @ApiMethod(name = "greetings.multiply", httpMethod = "post")
 public HelloGreeting insertGreeting(@Named("times") String times, @Named("username") String username, HelloGreeting greeting) {
  String group = times;
  HelloGreeting response = new HelloGreeting();

  FeatureCollection jsonString = new FeatureCollection();
  Gson gson = new Gson();
  jsonString.type = "Well stored in backend";
  try {

   //Datastore specification of the Entity to be saved
   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
   jsonString = gson.fromJson(greeting.getMessage().toString(), FeatureCollection.class);
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
  } catch (Exception e) {

   StackTraceElement[] stackTrace = e.getStackTrace();
   String fullClassName = stackTrace[stackTrace.length - 1].getClassName();
   String className = fullClassName.substring(fullClassName
    .lastIndexOf(".") + 1);
   String methodName = stackTrace[stackTrace.length - 1].getMethodName();
   int lineNumber = stackTrace[stackTrace.length - 1].getLineNumber();
   System.out.println("Error " + e.toString() + " lineNumber --> " + fullClassName + "--" + className + "--" + methodName + "--" + lineNumber);
   jsonString.type = "Error" + e;

  }
  response.setMessage("This is a random test lalala" + jsonString.type);
  return response;
 }






 @ApiMethod(name = "store.geojson", httpMethod = "post")
 public FeatureCollection storeGeoJson(@Named("Geojson") String geojson, @Named("Group") String group, @Named("ID") long id) {
  //Creates the JSON object that will be returned by the endpoint
  FeatureCollection jsonString = new FeatureCollection();
  Gson gson = new Gson();
  jsonString.type = "Well stored in backend";
  try {

   //Datastore specification of the Entity to be saved
   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
   jsonString = gson.fromJson(geojson, FeatureCollection.class);
   Key featureKey = KeyFactory.createKey("GeoJsonText", group);
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
  } catch (Exception e) {

   StackTraceElement[] stackTrace = e.getStackTrace();
   String fullClassName = stackTrace[stackTrace.length - 1].getClassName();
   String className = fullClassName.substring(fullClassName
    .lastIndexOf(".") + 1);
   String methodName = stackTrace[stackTrace.length - 1].getMethodName();
   int lineNumber = stackTrace[stackTrace.length - 1].getLineNumber();
   System.out.println("Error " + e.toString() + " lineNumber --> " + fullClassName + "--" + className + "--" + methodName + "--" + lineNumber);
   jsonString.type = "Error" + e;

  }

  //Geojson returned either with success message or error message 
  return jsonString;
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


/*
 *
 * LIST and GET GEOJSONS
 *
 */

 private int levelSimplification(int zoom, int screen) {

  int levelToFetch = zoom * 100;

  switch (levelToFetch) {
   case 100:
    levelToFetch = 1;
    break;
   case 200:
    levelToFetch = 2;
    break;
   case 300:
    levelToFetch = 3;
    break;
   case 400:
    levelToFetch = 4;
    break;
   case 500:
    levelToFetch = 5;
    break;
   case 600:
    levelToFetch = 6;
    break;
   case 700:
    levelToFetch = 7;
    break;
   case 800:
    levelToFetch = 8;
    break;

  }

  return levelToFetch;
 }


 //This method returns the appropriate geographical dataset depending on the zoom and size of the map
 @ApiMethod(name = "list", httpMethod = "get")
 public FeatureCollection list(@Named("Group") String group, @Named("Zoom") int zoom, @Named("Screen") int screen) {

  //The object that will be returned by the endpoint
  FeatureCollection newGeojson = new FeatureCollection();
  newGeojson.type = "FeatureCollection";
  List < Feature > featureList = new ArrayList < Feature > ();

  //Services required by the method
  Gson gson = new Gson();
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  //Retrives the entity saved under the key specfied during the insertion
  Key featureKey = KeyFactory.createKey("GeoJsonText", group);
  Query query = new Query("GeoJsonText", featureKey);
  PreparedQuery pq = datastore.prepare(query);

  int levelToFetch = levelSimplification(zoom, screen);
  //Iterates through the Entities returned by the query to retrive each feature
  for (Entity result: pq.asIterable()) {

   //All the data to extract
   Text geoje = (Text) result.getProperty("geometryCoordinateSimplified" + levelToFetch);
   List < double[][] > vertices = gson.fromJson(geoje.getValue(), List.class);
   String type = result.getProperty("geometryType").toString();

   //Assembladge of the Geometries
   com.example.geojsonsimplifier.Geometry geom = new com.example.geojsonsimplifier.Geometry();
   geom.type = type;
   geom.coordinates = vertices;

   Map < String, Object > propertiesGeometry = new HashMap < String, Object > ();
   propertiesGeometry.put("Property", result.getProperty("properties"));

   //Assembladge of the Feature
   Feature feature = new Feature();
   feature.type = "Feature";
   feature.geometry = geom;
   feature.properties = propertiesGeometry;
   featureList.add(feature);

  }
  newGeojson.features = featureList;

  return newGeojson;
 }











 @ApiMethod(name = "greetings.authed", path = "hellogreeting/authed")
 public HelloGreeting authedGreeting(User user) {
  HelloGreeting response = new HelloGreeting("hello " + user.getEmail());
  return response;
 }


 public ArrayList < HelloGreeting > listGreeting() {
  return greetings;
 }

 @ApiMethod(name = "getJsontestget", httpMethod = "post")
 public FeatureCollection getJsontestget(@Named("id") Integer id) {
  FeatureCollection jsonString = new FeatureCollection();
  jsonString.type = "Well stored in backend";

  return jsonString;
 }



 public HelloGreeting savesignin(@Named("account") String account) {
  HelloGreeting response = new HelloGreeting();
  response.setMessage(account + "Has beeen saved");
  return response;
 }



}