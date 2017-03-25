(function() {
 	// Initializae Firebase
 	const config = {
	    apiKey: "AIzaSyCRoNH6xszGFW6eVacQX8qE8NOP9ErwavY",
	    authDomain: "data-visualization-system.firebaseapp.com",
	    databaseURL: "https://data-visualization-system.firebaseio.com",
	    storageBucket: "data-visualization-system.appspot.com",
	    messagingSenderId: "40626504608"
	};
	firebase.initializeApp(config);

  // USER INFORMATIONS
  firebase.auth().onAuthStateChanged(function(user) {
    if (user) {
      console.log('user');
      // User is signed in.
      const displayName = user.displayName;
      const email = user.email;
      const emailVerified = user.emailVerified;
      const photoURL = user.photoURL;
      const uid = user.uid;
      const providerData = user.providerData;
      user.getToken().then(function(accessToken) {
        JSON.stringify({
          displayName: displayName,
          email: email,
          emailVerified: emailVerified,
          photoURL: photoURL,
          uid: uid,
          accessToken: accessToken,
          providerData: providerData
        }, null, '  ');
      });
      document.getElementById('account-name').innerHTML = email;

    } else {
      console.log('no user');
      window.location.href = 'index.html';
    }
  }, function(error) {
    console.log(error);
  });

  var downloadURL = "";
  const btnReportUpload = document.getElementById('btnReportUpload');
  btnReportUpload.addEventListener('change', function(e) {
    var file = e.target.files[0];
    var storageRef = firebase.storage().ref('photos/' + file.name);
    var task = storageRef.put(file);

    task.on('state_changed',
      function progress(snapshot) {
        var percentage = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
        document.getElementById('txtUploadProgress').innerHTML = percentage + "%";
        $('.progress-bar').css('width', percentage+'%').attr('aria-valuenow', percentage); 
      }, function error(err) {
      }, function() {
        downloadURL = task.snapshot.downloadURL;
        document.getElementById('reportPhoto').src = downloadURL;
      }
    );
  });

  /* SUBMIT NEW REPORT */
  const reportLatitude = document.getElementById('reportLatitude');
  const reportLongitude = document.getElementById('reportLongitude');
  const reportOpinion = document.getElementById('reportOpinion');
  const reportScore = document.getElementById('reportScore');
  const reportCategory = document.getElementById('reportCategory');
  const btnReportSubmit = document.getElementById('btnReportSubmit');

  btnReportSubmit.addEventListener('click', e => {

    const latitude = reportLatitude.value;
    const longitude = reportLongitude.value;
    const opinion = reportOpinion.value;
    const score = reportScore.value;
    const category = reportCategory.value;

    writeNewReport(latitude, longitude, category, score, opinion, downloadURL);    
  });

  function writeNewReport(lat, lng, category, score, opinion, imageUrl) {
    var features = firebase.database().ref('geofireTest/features/').push();
    var geometry = features.child('geometry');
    var properties = features.child('properties');
    features.set({
      type: 'Feature'
    });
    geometry.set({
      type: 'Point'
    });
    var coordinates = geometry.child('coordinates');
    coordinates.set({
      0: parseFloat(lng),
      1: parseFloat(lat)
    });
    properties.set({
      category: category,
      score: parseInt(score),
      opinion: opinion,
      imageUrl: imageUrl
    });
  }

	// LOGOUT ELEMENTS
	const btnLogout = document.getElementById('btnLogout');

	btnLogout.addEventListener('click', e => {
    firebase.auth().signOut().then(function() {
      window.location.href = 'index.html';
    }).catch(function(error) {
      // If there's an error 
    });
  });

}());

  function getLocation() {
      if (navigator.geolocation) {
          navigator.geolocation.getCurrentPosition(showPosition);
      } else { 
          reportLatitude.value = "Geolocation is not supported by this browser.";
          reportLongitude.value = "Geolocation is not supported by this browser.";
      }
  }

  function showPosition(position) {
      reportLatitude.value = position.coords.latitude;
      reportLongitude.value = position.coords.longitude;
  }