

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

/**
 * Whether or not the user is signed in.
 * @type {boolean}
 */


/**
 * Loads the application UI after the user has completed auth.
 */
google.devrel.samples.hello.userAuthed = function() {
  var request = gapi.client.oauth2.userinfo.get().execute(function(resp) {
    if (!resp.code) {
     
     
    }
  });
};

/**
 * Handles the auth flow, with the given value for immediate mode.
 * @param {boolean} mode Whether or not to use immediate mode.
 * @param {Function} callback Callback to call on completion.
 */


/**
 * Presents the user with the authorization popup.
 */


/**
 * Prints a dataset to the dataset log.
 * param {Object} dataset Dataset to print.
 */
google.devrel.samples.hello.print = function(dataset) {
  var element = document.createElement('div');
  element.classList.add('row');
  element.innerHTML = dataset.message;
  document.getElementById('outputLog').appendChild(element);
};

/**
 * Gets a numbered dataset via the API.
 * @param {string} id ID of the dataset.
 */
google.devrel.samples.hello.getDataset = function(id,zoominput) {
  gapi.client.helloworld.datasets.getDataset({'group': id,'zoom': zoominput}).execute(
      function(resp) {
        if (!resp.code) {
          var datatoload=resp.message;
           //var geojson = JSON.parse(datatoload);

map.data.addGeoJson(datatoload);

          google.devrel.samples.hello.print(resp);
        }
      });
};
/**
 * Lists datasets via the API.
 */

function onSignIn(googleUser) {
  var profile = googleUser.getBasicProfile();

  window.alert('LOGEED IN');
  console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
  console.log('Name: ' + profile.getName());
  console.log('Image URL: ' + profile.getImageUrl());
  console.log('Email: ' + profile.getEmail());

  gapi.client.helloworld.datasets.savesignin({
      'account': profile.getEmail()
    }).execute(function(resp) {
      if (!resp.code) {
         window.alert(' IN WITH'+resp);
      }
    });
}
 function signOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
      console.log('User signed out.');
    });
  }


/**
 * Gets a dataset a specified number of times.
 * @param {string} dataset Dataset to repeat.
 * @param {string} count Number of times to repeat it.
 */
google.devrel.samples.hello.multiplyDataset = function(
    dataset, times) {
  gapi.client.helloworld.datasets.multiply({
      'message': dataset,
      'times': times
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
/**
 * Greets the current user via the API.
 */


/**
 * Enables the button callbacks in the UI.
 */
google.devrel.samples.hello.enableButtons = function() {

  document.getElementById('getDataset').onclick = function() {
    google.devrel.samples.hello.getDataset(
        document.getElementById('id').value,document.getElementById('zoominput').value);
  }


  document.getElementById('multiplyDatasets').onclick = function() {
          google.devrel.samples.hello.multiplyDataset(
        document.getElementById('dataset').value,
        document.getElementById('count').value);
  }

/*document.getElementById('submitSubscruption').onclick = function() {
          google.devrel.samples.hello.submitSubscruptions(
        document.getElementById('nameAccount').value,
        document.getElementById('passwordAccount').value);
  }*/
  
  
};

/**
 * Initializes the application.
 * @param {string} apiRoot Root of the API's path.
 */



google.devrel.samples.hello.init = function(apiRoot) {
  // Loads the OAuth and helloworld APIs asynchronously, and triggers login
  // when they have completed.
   map = new google.maps.Map(document.getElementById('map'), {
    zoom: 0,
    center: {lat: 0, lng: 0}
  });
   //var dataset ="{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[101.0,0.0],[101.0,1.0],[100.0,1.0],[100.0,0.0]]]},\"properties\":{\"Property\":\"{prop0\u003dvalue0, prop1\u003d{this\u003dthat}}\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[102.0,2.0],[103.0,2.0],[103.0,3.0],[102.0,3.0],[102.0,2.0]]],[[[100.0,0.0],[101.0,0.0],[101.0,1.0],[100.0,1.0],[100.0,0.0]],[[100.2,0.2],[100.8,0.2],[100.8,0.8],[100.2,0.8],[100.2,0.2]]]]},\"properties\":{\"Property\":\"{prop0\u003dvalue0, prop1\u003d{this\u003dthat}}\"}}]}"
   //map.data.addGeoJson(dataset);
  var apisToLoad;
  var callback = function() {
    if (--apisToLoad == 0) {
      google.devrel.samples.hello.enableButtons();
      
    }
  }

  apisToLoad = 1; // must match number of calls to gapi.client.load()
  gapi.client.load('helloworld', 'v1', callback, apiRoot);
  
};

