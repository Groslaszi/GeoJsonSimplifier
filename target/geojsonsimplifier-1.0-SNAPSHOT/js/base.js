/**
 * @fileoverview
 * Provides methods for the Hello Endpoints sample UI and interaction with the
 * Hello Endpoints API.
 *
 * @author danielholevoet@google.com (Dan Holevoet)
 */
/** google global namespace for Google projects. */
var google = google || {};
var map;

/** devrel namespace for Google Developer Relations projects. */
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




google.devrel.samples.hello.print = function(greeting) {
    
};


google.devrel.samples.hello.getGreeting = function(id, zoominput, username) {
    gapi.client.helloworld.greetings.getGreeting({
        'group': id,
        'zoom': zoominput,
        'username': username
    }).execute(
        function(resp) {
            if (!resp.code) {
                var datatoload = resp.message;
                map.data.addGeoJson(datatoload);

                google.devrel.samples.hello.print(resp);
            }
        });
};

google.devrel.samples.hello.multiplyGreeting = function(
    greeting, times, username) {
    document.getElementById("multiplyGreetings").style.display = "none";
        document.getElementById("img-spinner").style.display = "block";
    gapi.client.helloworld.greetings.multiply({
        'message': greeting,
        'times': times,
        'username': username
    }).execute(function(resp) {
        if (!resp.code) {
            window.alert(JSON.stringify(resp));
            google.devrel.samples.hello.print(resp);
            window.location = "tutorialpage.html";
        }
    });
};

google.devrel.samples.hello.submitSubscruptions = function(
    nameAccount, passwordAccount) {
    window.alert('ButtonClicked');
};

function fetchDatasetsList() {

    var xmlhttp = new XMLHttpRequest();
    var url = "https://geojsonsimplifier.appspot.com/_ah/api/helloworld/v1/listDataSets/" + sessionStorage.userinfo;

    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            var myArr = JSON.parse(xmlhttp.responseText);
            myFunction(myArr);
        }
    };

    xmlhttp.open("GET", url, true);
    xmlhttp.send();

}


function onSignIn(googleUser) {
    var profile = googleUser.getBasicProfile();
    var userInfo = '' + profile.getId();
    sessionStorage.userinfo = userInfo;
    sessionStorage.accountName = profile.getName();
    window.location = "profilePage.html";

}

function signOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function() {
        console.log('User signed out.');
    });
}

function updateMap() {
    var mapdataupdate = JSON.parse(document.getElementById('greeting').value);
    map.data.addGeoJson(mapdataupdate);
}


google.devrel.samples.hello.enableButtons = function() {

    document.getElementById('multiplyGreetings').onclick = function() {
if(geojsontoUpload==null){
  geojsontoUpload=document.getElementById('greeting').value;
}

        google.devrel.samples.hello.multiplyGreeting(
            geojsontoUpload,
            document.getElementById('count').value, sessionStorage.userinfo);

        sessionStorage.datauploadedname= document.getElementById('count').value;
        
         
    }


};


function httpGet(theUrl) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", theUrl, false);
    xmlHttp.send(null);
    window.alert('FirstONe' + xmlHttp.responseText.message);
    var datatoload = xmlHttp.responseText.message;
    var georesponse = JSON.parse(xmlHttp.responseText);
    window.alert(JSON.stringify(georesponse.message));
    return georesponse;
}


function myFunction(arr) {
    var listdata = arr.message;
    var element = document.getElementById("datalist");
    if (listdata == 'nothing') {
        
    } else {
        
for (i = 0; i < listdata.length; i++) { 

    var button = document.createElement("button");
    button.type = "button";
    button.id = listdata[i];
    button.innerHTML = listdata[i];
    button.className = "list-group-item";
    button.addEventListener('click', clickFunc);

element.appendChild(button);
}
    }

var button = document.createElement("button");
    button.type = "button";
    button.id = "createNewDataSet";
    button.innerHTML = "Create a new dataset";
    button.className = "list-group-item";
    button.addEventListener('click', clickFunc);

element.appendChild(button);

document.getElementById("img-spinner").style.display = "none";

}

var clickedDataset=null;

function clickFunc() {

if(features!=null){
  for (var i = 0; i < features.length; i++)
              map.data.remove(features[i]);
}

if(this.id!="createNewDataSet"){
    document.getElementById("urldataset").innerHTML = 'The corresponding URL is: https://geojsonsimplifier.appspot.com/_ah/api/helloworld/v1/hellogreeting/'+this.id+'/INSERT_ZOOM_LEVEL'+'/'+sessionStorage.userinfo;
    clickedDataset=this.id;
 var xmlhttp = new XMLHttpRequest();
 document.getElementById("deleteButton").style.visibility = "visible";
var url =  'https://geojsonsimplifier.appspot.com/_ah/api/helloworld/v1/hellogreeting/'+this.id+'/'+map.getZoom()+'/'+sessionStorage.userinfo;

    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            var myArr = JSON.parse(xmlhttp.responseText);
            dataDisplayonMap(myArr);
        }
    };

    xmlhttp.open("GET", url, true);
    xmlhttp.send();
}else{
  window.location = "index.html";
}
}

var datamap;
var features=null;
function dataDisplayonMap(myArr){

features = map.data.addGeoJson(myArr.message);

}


var geojsontoUpload=null;
$(document).ready(function() {
    $('#file_input').on('change', function(e) {

        function updateProgress(evt) {
            if (evt.lengthComputable) {
                // evt.loaded and evt.total are ProgressEvent properties
                var loaded = (evt.loaded / evt.total);
                if (loaded < 1) {
                    // Increase the prog bar length
                    style.width = (loaded * 200) + "px";
                }
            }
        }

        function loaded(evt) {
            // Obtain the read file data    
            var fileString = evt.target.result;
            // Handle UTF-16 file dump
             map.data.addGeoJson(JSON.parse(fileString));
            geojsontoUpload=fileString;
            
        }
        var res = readFile(this.files[0]);

        var reader = new FileReader();

        reader.readAsText(this.files[0], "UTF-8");

        reader.onprogress = updateProgress;
        reader.onload = loaded;


    });
});

function readFile(file) {
    var reader = new FileReader(),
        result = 'empty';

    reader.onload = function(e) {
        result = e.target.result;
    };

    reader.readAsText(file);

    return result;
}














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

    apisToLoad = 1; // must match number of calls to gapi.client.load()
    gapi.client.load('helloworld', 'v1', callback, apiRoot);

};