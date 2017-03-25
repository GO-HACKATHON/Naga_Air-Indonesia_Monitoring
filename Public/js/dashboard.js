(function() {

  console.log('called');

 	// Initializae Firebase
 	const config = {
	    apiKey: "AIzaSyCRoNH6xszGFW6eVacQX8qE8NOP9ErwavY",
	    authDomain: "data-visualization-system.firebaseapp.com",
	    databaseURL: "https://data-visualization-system.firebaseio.com",
	    storageBucket: "data-visualization-system.appspot.com",
	    messagingSenderId: "40626504608"
	};
	firebase.initializeApp(config);

  const btnReportSubmit = document.getElementById('btnReportSubmit');
  btnReportSubmit.addEventListener('click', e => {
    writeNewReport(99, 100, "Kesehatan", 10, "Sangat kotor sekali");    
  });

  const dbRefObject = firebase.database().ref().child('geofire');
  dbRefObject.on('value', snap => console.log(snap.val()));

  function writeUserData(userId, name, email, imageUrl) {
    firebase.database().ref('geofireTest/features/').set({
      username: name,
      email: email,
      profile_picture : imageUrl
    });
  }

  function writeNewReport(lng, lat, category, score, desc) {
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
      0: lng,
      1: lat
    });
    properties.set({
      category: category,
      score: score,
      description: desc
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

}());