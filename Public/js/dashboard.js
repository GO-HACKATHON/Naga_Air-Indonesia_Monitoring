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

  	// Login Elements
  	const btnLogout = document.getElementById('btnLogout');

  	btnLogout.addEventListener('click', e => {
      console.log('clicked');
      
      firebase.auth().signOut().then(function() {
        window.location.href = 'index.html';
      }).catch(function(error) {
        // If there's an error 
      });
    });

    firebase.auth().onAuthStateChanged(function(user) {
      if (user) {
        console.log('user');
        // User is signed in.
        var displayName = user.displayName;
        var email = user.email;
        var emailVerified = user.emailVerified;
        var photoURL = user.photoURL;
        var uid = user.uid;
        var providerData = user.providerData;
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