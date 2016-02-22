package com.example.geojsonsimplifier;

import java.util.HashMap;
import java.util.Map;

public class Feature {
  public String type;
  public Geometry geometry;
  public Object properties;

  public Feature() {};
}


/*

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

					Entity geoJsonEntity = new Entity("GeoJsonText", featureKey);
					geoJsonEntity.setProperty("id", jsonString.features.get(i).toString());
					geoJsonEntity.setProperty("geometryType", jsonString.features.get(i).geometry.type.toString());

if(jsonString.features.get(i).geometry.type.toString().equals("Polygon"))
geoJsonEntity = simplifyAndStore(geoJsonEntity, jsonString.features.get(i).geometry.coordinates.toString(),2,"Polygon");

					Text jsonText = new Text(jsonString.features.get(i).geometry.coordinates.toString());
					geoJsonEntity.setProperty("geometryCoordinate", jsonText);
					geoJsonEntity.setProperty("properties", jsonString.features.get(i).properties.toString());

					datastore.put(geoJsonEntity);

				}
			}

		} catch (Exception e) {

			jsonString.type = "Error" + e;

		}

		return jsonString;
	}



private Entity simplifyAndStore(Entity geoJsonEntity, String featureCoordinates,int nSimplifications,String type){

if(type.equals("Polygon")){

					Text jsonText = new Text(simplifyPolygon( geoJsonEntity,  featureCoordinates, nSimplifications).toString());
					geoJsonEntity.setProperty("geometryCoordinateSimplified", jsonText);
}
return geoJsonEntity;

}






private  List<double[][]> simplifyPolygon(Entity geoJsonEntity, String featureCoordinates,int nSimplifications){

            Gson gson = new Gson();
            List<double[][]> verticesList =  gson.fromJson(featureCoordinates, List.class);
           List<double[][]> finalVerticesList = new ArrayList<double[][]>();
            for(int r =0; r< verticesList.size();r++){

              double [][] vertices =gson.fromJson(verticesList.get(r)+"", double[][].class);
              System.out.println("original vertices are "+ vertices);
              Coordinate [] coordinateJTS = new Coordinate[vertices.length];
              int j = 0;


              for(double[] point : vertices){
                Coordinate coordinate = new Coordinate(point[0],point[1]);
                coordinateJTS[j++] = coordinate;
              }


              GeometryFactory geometryFactory = new GeometryFactory();
              LinearRing linearRing = new GeometryFactory().createLinearRing(coordinateJTS);
              Polygon polygon = new Polygon (linearRing, null, geometryFactory); 

              com.vividsolutions.jts.geom.Geometry geometryJTS = new GeometryFactory().createLineString(coordinateJTS);
              geometryJTS=TopologyPreservingSimplifier.simplify(polygon,0.1*nSimplifications);
              Coordinate[] simplified =geometryJTS.getCoordinates();

j = 0;
vertices =new double[simplified.length][2];
              for(Coordinate coordinatepair:simplified){
              	System.out.println(coordinatepair.x+ "  second is "+coordinatepair.y);
              	vertices[j][0]=coordinatepair.x;
              	vertices[j][1]=coordinatepair.y;
              	j++;
              }
finalVerticesList.add(vertices);

 //ystem.out.println("vertices are "+ vertices);
              System.out.println("vertices are "+ gson.toJson(finalVerticesList,List.class));

       }
       return finalVerticesList;

}




	@ApiMethod(name = "list", path = "list", httpMethod = "get")
	public FeatureCollection list(@Named("Group") String group) {

		FeatureCollection newGeojson = new FeatureCollection();
		newGeojson.type = "FeatureCollection";
		List < Feature > featureList = new ArrayList < Feature > ();

		Gson gson = new Gson();
		Genson genson = new Genson();

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Key featureKey = KeyFactory.createKey("GeoJsonText", group);
		Query query = new Query("GeoJsonText", featureKey);
		//Query q = new Query("GeoJsonText");
		PreparedQuery pq = datastore.prepare(query);


		for (Entity result: pq.asIterable()) {

			Feature feature = new Feature();
			feature.type = "Feature";
Text geoje;
List < Double[][] > vertices;
if(result.getProperty("geometryType").toString().equals("Polygon")){

geoje = (Text) result.getProperty("geometryCoordinateSimplified");
System.out.println("simplified version "+ geoje.getValue());
vertices = gson.fromJson(geoje.getValue(), List.class);
}else{
geoje = (Text) result.getProperty("geometryCoordinate");
vertices = gson.fromJson(geoje.getValue(), List.class);
}
	
			com.example.helloworld.Geometry geom = new com.example.helloworld.Geometry();
			geom.type = result.getProperty("geometryType").toString();


			geom.coordinates = vertices;
			feature.geometry = geom;

			Map < String, Object > propertiesGeometry = new HashMap < String, Object > ();
			propertiesGeometry.put("Property", result.getProperty("properties"));

			feature.properties = propertiesGeometry;
			featureList.add(feature);


		}
		newGeojson.features = featureList;

		return newGeojson;
	}

*/