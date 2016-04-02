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
import javax.jdo.*;

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

//this API method uploads a given GeoJSON object to App Engine Datastore
 @ApiMethod(name = "greetings.multiply", httpMethod = "post")
 public HelloGreeting insertGreeting(@Named("times") String times, @Named("username") String username, HelloGreeting greeting) {
  
  String group = times;
  HelloGreeting response = new HelloGreeting();

// the string passed onto the method is decomposed in its various components by using the Java bean FeatureCollection
FeatureCollection jsonString = new FeatureCollection();
jsonString.type="Response";
try {

//the geojson object is passed onto the task that will then be passed on a push queue where it will then be processed
 MyTask myTask = new MyTask(group,username);
 myTask.setGeoJson(greeting.getMessage().toString());
 //a pause allows the data to be properly passsed onto the task
 Thread.sleep(500);
 Queue myQueue = QueueFactory.getDefaultQueue();
 myQueue.add(TaskOptions.Builder.withPayload(myTask));
 }catch (Exception e) {
 response.setMessage("Error " + e);
 return response;
}

//if everything was well executed the method sends back a confirmation message 
  response.setMessage("Well Passed Onto Task Queue " + jsonString.type);
  return response;
 }


//the method returns all the dataset that have been uploaded by a user 
 public HelloGreeting listDataSets(@Named("username") String username) throws NotFoundException {

  HelloGreeting response = new HelloGreeting();
  //if no datasets have been previosuly uploaded
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
    //each name is added once
    if(!datasetNames.contains(result.getProperty("grouptag").toString()))
    datasetNames.add(result.getProperty("grouptag").toString());
   }
  } catch (Exception e) {
   response.setMessage(e + " Error");
  }
// returns the list of dataset names if some have been found
  if (datasetNames.size() > 0) {
   response.setMessage(datasetNames);
  }
  return response;
 }

//this API method is responsible for deleting a gievn dataset from the platform
public HelloGreeting takeoutdataset (@Named("username") String username,@Named("group") String group){
HelloGreeting response = new HelloGreeting();
  response.setMessage("well deleted");
  try{
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
   Key featureKey = KeyFactory.createKey("GeoJsonText", username);
   Filter propertyFilter = new FilterPredicate("grouptag",
    FilterOperator.EQUAL,
    group);
//retrieves the dataset that corresponds to the group name provided
 Query query = new Query("GeoJsonText", featureKey).setFilter(propertyFilter);
PreparedQuery pq = datastore.prepare(query);

//each entity that composes a GeoJSON object is deleted 
for (Entity result: pq.asIterable()) {
datastore.delete( result.getKey());
}

}catch(Exception e){
   response.setMessage("Error "+e);
}
return response;
}




//this method fetched the most appropriate version of the dataset that has been requested
 public HelloGreeting getGreeting(@Named("group") String group, @Named("zoom") int zoom, @Named("username") String username, @Named("size") int size) throws NotFoundException {
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

   int levelToFetch = levelSimplification(zoom,size);
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
   //throw new NotFoundException("Greeting not found with an index: ");
  }

  return response;
 }
 @ApiMethod(name = "greetings.authed", path = "hellogreeting/authed")
 public HelloGreeting authedGreeting(User user) {
  HelloGreeting response = new HelloGreeting("hello " + user.getEmail());
  return response;
 }


//this method calculates which level of simplification has to be fetched depending on the zoom and size of the map in use
private int levelSimplification(int zoom, int screen) {

  int levelToFetch = zoom ;

  switch (levelToFetch) {
    case 8:
    levelToFetch = 0;
    break;
   case 7:
    levelToFetch = 1;
    break;
   case 6:
    levelToFetch = 2;
    break;
   case 5:
    levelToFetch = 3;
    break;
   case 4:
    levelToFetch = 4;
    break;
   case 3:
    levelToFetch = 5;
    break;
   case 2:
    levelToFetch = 6;
    break;
   case 1:
    levelToFetch = 7;
    break;
   case 0:
    levelToFetch = 8;
    break;

  }

  return levelToFetch;
 }



}