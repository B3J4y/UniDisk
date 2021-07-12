package de.unidisk.rest.authentication;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import de.unidisk.contracts.services.IAuthenticationService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FirebaseAuthenticationService implements IAuthenticationService {

    private FileInputStream getDefaultServiceAccountFile() throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        return new FileInputStream(classLoader.getResource("firebase-sa.json").getFile());
    }

    private void setup() throws IOException {
        if(FirebaseApp.getApps().size() > 0)
            return;

        FileInputStream serviceAccount = getDefaultServiceAccountFile();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }

    @Override
    public ContextUser verifyToken(String jwt)  {
        try {
            setup();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            final FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(jwt);
            return new ContextUser(token.getUid(), token.getEmail());
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
        return null;
    }
}
