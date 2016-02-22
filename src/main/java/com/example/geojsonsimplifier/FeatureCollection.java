package com.example.geojsonsimplifier;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
public class FeatureCollection {
  public String type;
  public List<Feature> features;
  public Map<String, Object> properties;
  public FeatureCollection() {};

}


/*


package com.example.helloworld;
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
import java.util.Arrays;



/** An endpoint class we are exposing @Api(name = "myApi",
version = "v1",
namespace = @ApiNamespace(ownerDomain = "helloworld.example.com",
ownerName = "helloworld.example.com",
packagePath = ""))
public class YourFirstAPI {



	@ApiMethod(name = "insert", path = "insert", httpMethod = "post")
	public FeatureCollection loadGeoJSON(@Named("Geojson") String geojson, @Named("Group") String group, @Named("ID") long id) {

		//Creates the JSON object that will be returned by the endpoint
		FeatureCollection jsonString = new FeatureCollection();
		Gson gson = new Gson();
		jsonString.type = "Well stored in backend";
		try {

			//Datastore specification of the Entity to be saved
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			jsonString = gson.fromJson(geojson, FeatureCollection.class);
			Key featureKey = KeyFactory.createKey("GeoJsonText", group);

			//Iterates through each feature of the GeoJSON file seperating them between coordinates, type and metadata
			for (int i = 0; i < jsonString.features.size(); i++) {
				if (jsonString.features.get(i).geometry != null) {

					String featureType = jsonString.features.get(i).geometry.type.toString();
					String stringCoordinate = jsonString.features.get(i).geometry.coordinates.toString();
					Text jsonText = new Text(stringCoordinate);

					Entity geoJsonEntity = new Entity("GeoJsonText", featureKey);
					geoJsonEntity.setProperty("id", jsonString.features.get(i).toString());
					geoJsonEntity.setProperty("geometryType", featureType);
					for(int n=1;n<8;n++){
					System.out.println("Beofre "+n);
					geoJsonEntity.setProperty("geometryCoordinateSimplified"+n, simplifys(stringCoordinate, n, featureType));

				}
					/*System.out.println("Beofre 2");
					geoJsonEntity.setProperty("geometryCoordinateSimplified"+2, simplify(stringCoordinate, 2, featureType));
					System.out.println("Beofre 3");
					geoJsonEntity.setProperty("geometryCoordinateSimplified"+3, simplify(stringCoordinate, 3, featureType));
					System.out.println("Beofre 4");
					geoJsonEntity.setProperty("geometryCoordinateSimplified"+4, simplify(stringCoordinate, 4, featureType));
					System.out.println("Beofre 5");
					geoJsonEntity.setProperty("geometryCoordinateSimplified"+5, simplify(stringCoordinate, 5, featureType));
					System.out.println("Beofre 6");
					geoJsonEntity.setProperty("geometryCoordinateSimplified"+6, simplify(stringCoordinate, 6, featureType));
					System.out.println("Beofre 7");
					geoJsonEntity.setProperty("geometryCoordinateSimplified"+7, simplify(stringCoordinate, 7, featureType));
					System.out.println("Beofre 8");
					geoJsonEntity.setProperty("geometryCoordinateSimplified"+8, simplify(stringCoordinate, 8, featureType));
					geoJsonEntity.setProperty("properties", jsonString.features.get(i).properties.toString());

					datastore.put(geoJsonEntity);
				}
			}
		} catch (Exception e) {
System.out.println("Error "+e.toString());
			jsonString.type = "Error" + e;

		}

		return jsonString;
	}



	private Text simplifys(String featureCoordinates, int nSimplifications, String type) {

		Gson gson = new Gson();
		Text simplejsonText = null;

		if (type.equals("Polygon")) {

			simplejsonText = new Text(gson.toJson(simplifyPolygon(featureCoordinates, nSimplifications), List.class));

		} else if (type.equals("MultiPolygon")) {

			List < ArrayList < double[][] > > verticesList = gson.fromJson(featureCoordinates, List.class);
			List < List < double[][] > > simplejsonVertices = new ArrayList < List < double[][] > > ();

			for (int e = 0; e < verticesList.size(); e++) {

				List vertices = gson.fromJson(verticesList.get(e) + "", ArrayList.class);
				simplejsonVertices.add(simplifyPolygon(vertices.toString(), nSimplifications));
			}

			simplejsonText = new Text(gson.toJson(simplejsonVertices, List.class));

		}

//System.out.println("Simplification "+nSimplifications+simplejsonText.getValue());
		return simplejsonText;
	}






	private List < double[][] > simplifyPolygon(String featureCoordinates, int nSimplifications) {

		Gson gson = new Gson();
		List < double[][] > verticesList = gson.fromJson(featureCoordinates, List.class);
		List < double[][] > finalVerticesList = new ArrayList < double[][] > ();
//System.out.println("Simplification received "+nSimplifications+"tobeSimplified "+featureCoordinates);
		for (int r = 0; r < verticesList.size(); r++) {

			double[][] vertices = gson.fromJson(verticesList.get(r) + "", double[][].class);
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
			geometryJTS = TopologyPreservingSimplifier.simplify(polygon, 0.1 * 30);
			Coordinate[] simplified = geometryJTS.getCoordinates();

			j = 0;
			vertices = new double[simplified.length][2];
			for (Coordinate coordinatepair: simplified) {

				vertices[j][0] = coordinatepair.x;
				vertices[j][1] = coordinatepair.y;
				j++;

			}

			finalVerticesList.add(vertices);

		}
		System.out.println("FINAL "+gson.toJson(finalVerticesList,List.class));
		return finalVerticesList;

	}




	@ApiMethod(name = "list", path = "list", httpMethod = "get")
	public FeatureCollection list(@Named("Group") String group, @Named("Zoom") String zoom) {

		FeatureCollection newGeojson = new FeatureCollection();
		newGeojson.type = "FeatureCollection";
		List < Feature > featureList = new ArrayList < Feature > ();

		Gson gson = new Gson();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Key featureKey = KeyFactory.createKey("GeoJsonText", group);
		Query query = new Query("GeoJsonText", featureKey);
		PreparedQuery pq = datastore.prepare(query);

		for (Entity result: pq.asIterable()) {


			Text geoje = (Text) result.getProperty("geometryCoordinateSimplified"+zoom);
			List < double[][] > vertices = gson.fromJson(geoje.getValue(), List.class);
			String type = result.getProperty("geometryType").toString();

			com.example.helloworld.Geometry geom = new com.example.helloworld.Geometry();
			geom.type = type;
			geom.coordinates = vertices;

			Map < String, Object > propertiesGeometry = new HashMap < String, Object > ();
			propertiesGeometry.put("Property", result.getProperty("properties"));

			Feature feature = new Feature();
			feature.type = "Feature";
			feature.geometry = geom;
			feature.properties = propertiesGeometry;
			featureList.add(feature);

		}
		newGeojson.features = featureList;

		return newGeojson;
	}

}


*/