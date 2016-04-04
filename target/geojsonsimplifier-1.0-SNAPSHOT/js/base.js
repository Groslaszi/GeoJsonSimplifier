/**
 * @fileoverview
 * Provides the javascripts method for most of GeoJsonSimplifier web interfaces
 */

 var google = google || {};
 var map;

 google.devrel = google.devrel || {};

 /** samples namespace for DevRel sample code. */
 google.devrel.samples = google.devrel.samples || {};

 /** hello namespace for this sample. */
 google.devrel.samples.hello = google.devrel.samples.hello || {};

/**
 * Client ID of the application (from the APIs Console).
 * @type {string}
 */
 google.devrel.samples.hello.CLIENT_ID =
 '953688397017-0g48nvidk9cp82lalumhbigorpacq7ke.apps.googleusercontent.com';

/**
 * Scopes used by the application.
 * @type {string}
 */
 google.devrel.samples.hello.SCOPES =
 'https://www.googleapis.com/auth/userinfo.email';


 google.devrel.samples.hello.userAuthed = function() {
    var request = gapi.client.oauth2.userinfo.get().execute(function(resp) {
        if (!resp.code) {


        }
    });
};


//the variable that is used to update and keep track of the data displayed on a map
var features=null;

//the method fetches the appropriate level of abstraction for a GeoJSON object of a given dataset
google.devrel.samples.hello.getdataset = function(id, zoominput, username) {

//the API method and it's parameters
gapi.client.helloworld.datasets.getdataset({
    'group': id,
    'zoom': zoominput,
    'username': username,
    'size':+window.innerWidth

}).execute(
    //the responses of the method
    function(resp) {
        if (!resp.code) {
//the GeoJson Object is retrieve from the response
var datatoload = resp.message;
map.data.addGeoJson(datatoload);
}
});
};

//the method inserts a given GeoJson Object to the backend
google.devrel.samples.hello.multiplydataset = function(
    dataset, times, username) {

    //the loading animations gets activated and the submit button disapears 
    document.getElementById("insertdatasets").style.display = "none";
    document.getElementById("img-spinner").style.display = "block";

    //the insert method API is called
    gapi.client.helloworld.datasets.multiply({
        'message': dataset,
        'times': times,
        'username': username

    }).execute(function(resp) {
        if (!resp.code) {
            //the dataset has been properly inserted and the tutorial page will be loaded
            window.location = "tutorialpage.html";
        }
    });
};



//this functions fecthes all the datasets that have been uploaded by a user and will be displayed on the profilePage
function fetchDatasetsList() {

//the GET request to be performed
var xmlhttp = new XMLHttpRequest();
var url = "https://geojsonsimplifier.appspot.com/_ah/api/helloworld/v1/listDataSets/" + sessionStorage.userinfo;

//the method that will be called when the response from the backend arrives
xmlhttp.onreadystatechange = function() {
    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {

//extracts the data list from the HTTP response
var responseDataList = JSON.parse(xmlhttp.responseText);
displayDatasetList(responseDataList);
}
};

xmlhttp.open("GET", url, true);
xmlhttp.send();

}

//displays in a button list the dataset names that have been uploaded by a user
function displayDatasetList(arr) {


    var listdata = arr.message;
    var element = document.getElementById("datalist");
    if (listdata == 'nothing') {

    } else {

//for every data name present in the list create a new button assign an Id and an event listener
for (i = 0; i < listdata.length; i++) { 

    var button = document.createElement("button");
    button.type = "button";
    button.id = listdata[i];
    button.innerHTML = listdata[i];
    button.className = "list-group-item";
    button.addEventListener('click',datasetSelected);

//appends the newly created button to the datalist HTML element
element.appendChild(button);
}
}

//the last element to be created and appended is the "create new dataset" one
var button = document.createElement("button");
button.type = "button";
button.id = "createNewDataSet";
button.innerHTML = "Create a new dataset";
button.className = "list-group-item";
button.addEventListener('click', datasetSelected);

element.appendChild(button);

//the elements have all been added the loading animation disapears
document.getElementById("img-spinner").style.display = "none";
}

//keeps track of the latest dataset that has been selected
var clickedDataset=null;

//the function is called when an element of the profile list page has been selected
function datasetSelected() {

//removes the previous GeoJson stored on the map
if(features!=null){
  for (var i = 0; i < features.length; i++)
      map.data.remove(features[i]);
}

//if the selcted element is not the one to create a new dataset
if(this.id!="createNewDataSet"){

    //displays the URL that the user has to insert in his personal application to retriev the selected dataset
    document.getElementById("urldataset").innerHTML = 'The corresponding URL is: https://geojsonsimplifier.appspot.com/_ah/api/helloworld/v1/responsebean/'+this.id+'/INSERT_ZOOM_LEVEL'+'/'+sessionStorage.userinfo+'/INSERT_WINDOW_WIDTH';
    //upadtes the element currently selected
    clickedDataset=this.id;
    var xmlhttp = new XMLHttpRequest();
    //the delete button is made available
    document.getElementById("deleteButton").style.visibility = "visible";

    //the selected dataset is fetched from the backend and displayed on the map
    var url =  'https://geojsonsimplifier.appspot.com/_ah/api/helloworld/v1/responsebean/'+this.id+'/'+map.getZoom()+'/'+sessionStorage.userinfo+'/'+window.innerWidth;
    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            var geojsonresponse = JSON.parse(xmlhttp.responseText);
            features = map.data.addGeoJson(geojsonresponse.message);
        }
    };

    xmlhttp.open("GET", url, true);
    xmlhttp.send();

}else{

  // if the button clicked is to create a new dataset the application takes the user to the index page  
  window.location = "index.html";
}
}


//the method that is executed when a user signs in GeoJsonSimplifier
function onSignIn(googleUser) {
    var profile = googleUser.getBasicProfile();
    //this variable contain the user information for the rest of his session on the application
    var userInfo = '' + profile.getId();
    sessionStorage.userinfo = userInfo;
    sessionStorage.accountName = profile.getName();
    //once the user signed in he is redirected to the profile page
    window.location = "profilePage.html";

}

//signs out a user from his account
function signOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function() {
        console.log('User signed out.');
    });
}

//the map in use is updated with the latest data that has arrived
function updateMap() {
    var mapdataupdate = JSON.parse(document.getElementById('dataset').value);
    map.data.addGeoJson(mapdataupdate);
}


//initialises the submit button from the index page: 
google.devrel.samples.hello.enableButtons = function() {

//the method that is called once a user has uplaoded and submitted his dataset
    document.getElementById('insertdatasets').onclick = function() {

//the GeoJSON to be uplaoded either comes from a local file or has been pasted in the textarea
        if(geojsontoUpload==null){
          geojsontoUpload=document.getElementById('dataset').value;
      }

      google.devrel.samples.hello.multiplydataset(
        geojsontoUpload,
        document.getElementById('count').value, sessionStorage.userinfo);
//the name of the dataset that has just been uplaoded is saved in the local sessionStorage 
      sessionStorage.datauploadedname= document.getElementById('count').value;
  }
};

//the method that executes the various http get requests
function httpGet(theUrl) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", theUrl, false);
    xmlHttp.send(null);
    var datatoload = xmlHttp.responseText.message;
    var georesponse = JSON.parse(xmlHttp.responseText);
    window.alert(JSON.stringify(georesponse.message));
    return georesponse;
}



//contains the geospatial data that will be uploaded to the backend
var geojsontoUpload=null;

//the gquery method that fetches the data contained in a local file
$(document).ready(function() {
    $('#file_input').on('change', function(e) {

        function loaded(evt) {
            // Obtain the read file data    
            var fileString = evt.target.result;
            // Handle UTF-16 file dump
            map.data.addGeoJson(JSON.parse(fileString));
            geojsontoUpload=fileString;
            
        }
        //the method reads the text contained in a file
        var res = readFile(this.files[0]);
        var reader = new FileReader();
        reader.readAsText(this.files[0], "UTF-8");
        reader.onload = loaded;


    });
});

//the method reads the content of a given file
function readFile(file) {
    var reader = new FileReader(),
    result = 'empty';

    reader.onload = function(e) {
        result = e.target.result;
    };

    reader.readAsText(file);

    return result;
}



//this method initialises both the map and the API's used
google.devrel.samples.hello.init = function(apiRoot) {

    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 2,
        center: {
            lat: 43,
            lng: 1
        }
    });

    var apisToLoad;
    var callback = function() {
        if (--apisToLoad == 0) {
            google.devrel.samples.hello.enableButtons();

        }
    }

    apisToLoad = 1; 
    gapi.client.load('helloworld', 'v1', callback, apiRoot);

};