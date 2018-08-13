#import <Cordova/CDV.h>

@interface FirebaseAuthenticationPlugin : CDVPlugin

-(void)getDataFromFirebaseRoot:(CDVInvokedUrlCommand *)command;
-(void)storeDataToFirebaseRoot:(CDVInvokedUrlCommand *)command;
-(void)getDataInFirebase:(CDVInvokedUrlCommand *)command;
-(void)storeDataInFirebase:(CDVInvokedUrlCommand *)command;
-(void)getDataInFirebaseWithPath:(CDVInvokedUrlCommand *)command;
-(void)storeDataInFirebaseWithPath:(CDVInvokedUrlCommand *)command;
- (void)getCurrentUser:(CDVInvokedUrlCommand*)command;
- (void)getIdToken:(CDVInvokedUrlCommand*)command;
- (void)createUserWithEmailAndPassword:(CDVInvokedUrlCommand*)command;
- (void)sendEmailVerification:(CDVInvokedUrlCommand*)command;
- (void)sendPasswordResetEmail:(CDVInvokedUrlCommand*)command;
- (void)signInWithEmailAndPassword:(CDVInvokedUrlCommand*)command;
- (void)signInAnonymously:(CDVInvokedUrlCommand*)command;
- (void)signInWithGoogle:(CDVInvokedUrlCommand*)command;
- (void)signInWithFacebook:(CDVInvokedUrlCommand*)command;
- (void)signInWithTwitter:(CDVInvokedUrlCommand*)command;
- (void)signInWithVerificationId:(CDVInvokedUrlCommand*)command;
- (void)verifyPhoneNumber:(CDVInvokedUrlCommand*)command;
- (void)signOut:(CDVInvokedUrlCommand*)command;
- (void)setLanguageCode:(CDVInvokedUrlCommand*)command;

@end
