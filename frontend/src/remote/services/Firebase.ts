import firebase from 'firebase';

const firebaseConfig = require('./../../firebase-config.json');

export default function setup() {
  if (firebase.apps.length === 0) {
    // Initialize Firebase
    firebase.initializeApp(firebaseConfig);
    firebase.auth().setPersistence(firebase.auth.Auth.Persistence.LOCAL);
  }
}
