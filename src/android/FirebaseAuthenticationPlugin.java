package coffice.cordova.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import by.chemerisuk.cordova.support.CordovaMethod;
import by.chemerisuk.cordova.support.ReflectiveCordovaPlugin;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class FirebaseAuthenticationPlugin extends ReflectiveCordovaPlugin implements OnCompleteListener, AuthStateListener {
    private static final String TAG = "FirebaseAuthentication";

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider phoneAuthProvider;
    private CallbackContext signinCallback;
    private CallbackContext authStateCallback;

    @Override
    protected void pluginInitialize() {
        Log.d(TAG, "[Coffice] Starting Firebase Authentication plugin");
        this.checkFirebaseApp();
    }


    private void checkFirebaseApp() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.phoneAuthProvider = PhoneAuthProvider.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @CordovaMethod
    private void initFirebase(final CallbackContext callbackContext) {
        this.checkFirebaseApp();
        callbackContext.success();
    }

    @CordovaMethod
        private void getDataFromFirebaseRoot(String pathRoot, final CallbackContext callbackContext) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child(pathRoot).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Object val = dataSnapshot.getValue();
                        if(val == null) {
                            callbackContext.error("No Data");
                            return;
                        }
                        if(val instanceof ArrayList) {
                            JSONArray jRes = new JSONArray();
                            for(Map<String, String> v : (ArrayList<Map<String, String>>)val) {
                                jRes.put(new JSONObject(v));
                            }
                            callbackContext.success(jRes);
                        } else {
                            Map<String, String> value = (Map<String, String>)val;
                            callbackContext.success(new JSONObject(value));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callbackContext.error("No Data");
                    }
                });
            
        }

    @CordovaMethod
    private void getDataInFirebaseWithPath(String path, final CallbackContext callbackContext) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            String uid = user.getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child(path).child(uid).keepSynced(true);
            mDatabase.child(path).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Object val = dataSnapshot.getValue();
                    if(val == null) {
                        callbackContext.error("No Data");
                        return;
                    }
                    if(val instanceof ArrayList) {
                        JSONArray jRes = new JSONArray();
                        for(Map<String, String> v : (ArrayList<Map<String, String>>)val) {
                            jRes.put(new JSONObject(v));
                        }
                        callbackContext.success(jRes);
                    } else {
                        Map<String, String> value = (Map<String, String>)val;
                        callbackContext.success(new JSONObject(value));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callbackContext.error("No Data");
                }
            });
        }
    }
    @CordovaMethod
    private void getDataInFirebase(final CallbackContext callbackContext) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String path = "credits";
        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            String uid = user.getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child(path).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Object val = dataSnapshot.getValue();
                    if(val == null) {
                        callbackContext.error("No Data");
                        return;
                    }
                    if(val instanceof ArrayList) {
                        JSONArray jRes = new JSONArray();
                        for(Map<String, String> v : (ArrayList<Map<String, String>>)val) {
                            jRes.put(new JSONObject(v));
                        }
                        callbackContext.success(jRes);
                    } else {
                        Map<String, String> value = (Map<String, String>)val;
                        callbackContext.success(new JSONObject(value));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callbackContext.error("No Data");
                }
            });
        }
    }


    @CordovaMethod
    private void storeDataToFirebaseRoot(JSONObject store, String pathRoot, final CallbackContext callbackContext) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            String uid = user.getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            try {
                mDatabase.child(pathRoot).setValue(jsonToMap(store));
                callbackContext.success("OK");
            } catch (JSONException err) {
                callbackContext.error(err.getMessage());
            }
        }
    }
    @CordovaMethod
    private void storeDataInFirebaseWithPath(JSONObject store, String path, final CallbackContext callbackContext) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            String uid = user.getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            try {
                mDatabase.child(path).child(uid).keepSynced(true);
                mDatabase.child(path).child(uid).setValue(jsonToMap(store));
                callbackContext.success("OK");
            } catch (JSONException err) {
                callbackContext.error(err.getMessage());
            }
        }
    }
    @CordovaMethod
    private void storeDataInFirebase(JSONObject store, final CallbackContext callbackContext) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            String uid = user.getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            try {
                mDatabase.child("credits").child(uid).keepSynced(true);
                mDatabase.child("credits").child(uid).setValue(jsonToMap(store));
                callbackContext.success("OK");
            } catch (JSONException err) {
                callbackContext.error(err.getMessage());
            }
        }
    }

    @CordovaMethod
    private void getCurrentUser(boolean forceRefresh, final CallbackContext callbackContext) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            callbackContext.success(getProfileData(firebaseAuth.getCurrentUser()));
        }
    }

    @CordovaMethod
    private void getIdToken(boolean forceRefresh, final CallbackContext callbackContext) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            user.getIdToken(forceRefresh)
                    .addOnCompleteListener(cordova.getActivity(), new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                callbackContext.success(task.getResult().getToken());
                            } else {
                                callbackContext.error(task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    @CordovaMethod
    private void createUserWithEmailAndPassword(String email, String password, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(cordova.getActivity(), this);
    }

    @CordovaMethod
    private void sendEmailVerification(final CallbackContext callbackContext) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            user.sendEmailVerification()
                    .addOnCompleteListener(cordova.getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                callbackContext.success();
                            } else {
                                callbackContext.error(task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    @CordovaMethod
    private void sendPasswordResetEmail(String email, final CallbackContext callbackContext) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(cordova.getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            callbackContext.success();
                        } else {
                            callbackContext.error(task.getException().getMessage());
                        }
                    }
                });
    }

    @CordovaMethod
    private void signInAnonymously(CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(cordova.getActivity(), this);
    }

    @CordovaMethod
    private void signInWithEmailAndPassword(String email, String password, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(cordova.getActivity(), this);
    }

    @CordovaMethod
    private void signInWithGoogle(String idToken, String accessToken, CallbackContext callbackContext) {
        signInWithCredential(GoogleAuthProvider.getCredential(idToken, accessToken), callbackContext);
    }

    @CordovaMethod
    private void signInWithFacebook(String accessToken, CallbackContext callbackContext) {
        signInWithCredential(FacebookAuthProvider.getCredential(accessToken), callbackContext);
    }

    @CordovaMethod
    private void signInWithTwitter(String token, String secret, CallbackContext callbackContext) {
        signInWithCredential(TwitterAuthProvider.getCredential(token, secret), callbackContext);
    }

    private void signInWithCredential(final AuthCredential credential, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(cordova.getActivity(), this);
    }

    @CordovaMethod
    private void signInWithVerificationId(String verificationId, String code, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        signInWithPhoneCredential(PhoneAuthProvider.getCredential(verificationId, code));
    }

    @CordovaMethod
    private void verifyPhoneNumber(String phoneNumber, long timeoutMillis, final CallbackContext callbackContext) {
        phoneAuthProvider.verifyPhoneNumber(phoneNumber, timeoutMillis, MILLISECONDS, cordova.getActivity(),
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        signInWithPhoneCredential(credential);
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        callbackContext.success(verificationId);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        callbackContext.error(e.getMessage());
                    }
                }
        );
    }

    private void signInWithPhoneCredential(PhoneAuthCredential credential) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(cordova.getActivity(), FirebaseAuthenticationPlugin.this);
        } else {
            user.updatePhoneNumber(credential)
                    .addOnCompleteListener(cordova.getActivity(), FirebaseAuthenticationPlugin.this);
        }
    }

    @CordovaMethod
    private void signOut(CallbackContext callbackContext) {
        firebaseAuth.signOut();

        callbackContext.success();
    }

    @CordovaMethod
    private void setLanguageCode(String languageCode, CallbackContext callbackContext) {
        if (languageCode == null) {
            firebaseAuth.useAppLanguage();
        } else {
            firebaseAuth.setLanguageCode(languageCode);
        }

        callbackContext.success();
    }

    @CordovaMethod
    private void setAuthStateChanged(boolean disable, CallbackContext callbackContext) {
        this.authStateCallback = disable ? null : callbackContext;

        if (disable) {
            firebaseAuth.removeAuthStateListener(this);
        } else {
            firebaseAuth.addAuthStateListener(this);
        }
    }

    @Override
    public void onComplete(Task task) {
        if (this.signinCallback != null) {
            if (task.isSuccessful()) {
                this.signinCallback.success(getProfileData(firebaseAuth.getCurrentUser()));
            } else {
                this.signinCallback.error(task.getException().getMessage());
            }

            this.signinCallback = null;
        }
    }

    @Override
    public void onAuthStateChanged(FirebaseAuth auth) {
        if (this.authStateCallback != null) {
            PluginResult pluginResult;
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                pluginResult = new PluginResult(PluginResult.Status.OK, getProfileData(user));
            } else {
                pluginResult = new PluginResult(PluginResult.Status.OK, "");
            }

            pluginResult.setKeepCallback(true);
            this.authStateCallback.sendPluginResult(pluginResult);
        }
    }

    private static JSONObject getProfileData(FirebaseUser user) {
        JSONObject result = new JSONObject();

        try {
            result.put("uid", user.getUid());
            result.put("displayName", user.getDisplayName());
            result.put("email", user.getEmail());
            result.put("phoneNumber", user.getPhoneNumber());
            result.put("photoURL", user.getPhotoUrl());
            result.put("providerId", user.getProviderId());
        } catch (JSONException e) {
            Log.e(TAG, "Fail to process getProfileData", e);
        }

        return result;
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}