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
    var element = document.createElement('div');
    element.classList.add('row');
    element.innerHTML = greeting.message;
    document.getElementById('outputLog').appendChild(element);
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
    gapi.client.helloworld.greetings.multiply({
        'message': greeting,
        'times': times,
        'username': username
    }).execute(function(resp) {
        if (!resp.code) {
            google.devrel.samples.hello.print(resp);
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

    console.log('ID: ' + profile.getId());
    console.log('Name: ' + profile.getName());
    console.log('Image URL: ' + profile.getImageUrl());
    console.log('Email: ' + profile.getEmail());

    var userInfo = '' + profile.getId() + profile.getName() + profile.getEmail();
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

    document.getElementById('getGreeting').onclick = function() {
        google.devrel.samples.hello.getGreeting(
            document.getElementById('id').value, document.getElementById('zoominput').value, sessionStorage.userinfo);
    }


    document.getElementById('multiplyGreetings').onclick = function() {

        google.devrel.samples.hello.multiplyGreeting(
            document.getElementById('greeting').value,
            document.getElementById('count').value, sessionStorage.userinfo);
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
    if (listdata == 'nothing') {
        window.alert('NOTHING');
    } else {
        var element = document.getElementById("datalist");
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
}

function clickFunc() {
  alert(this.id); 

 var xmlhttp = new XMLHttpRequest();
    var url =  'https://geojsonsimplifier.appspot.com/_ah/api/helloworld/v1/hellogreeting/'+this.id+'/'+map.getZoom()+'/'+sessionStorage.userinfo;

    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            var myArr = JSON.parse(xmlhttp.responseText);
            dataDisplayonMap(myArr);
        }
    };

    xmlhttp.open("GET", url, true);
    xmlhttp.send();

}

function dataDisplayonMap(myArr){
map.data.addGeoJson(myArr.message);
}

google.devrel.samples.hello.init = function(apiRoot) {

    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 0,
        center: {
            lat: 0,
            lng: 0
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