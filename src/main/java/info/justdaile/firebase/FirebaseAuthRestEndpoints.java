package info.justdaile.firebase;

/**
 * Firebase Authentication Endpoints methods for use in Java Firebase Auth api
 */
public class FirebaseAuthRestEndpoints {

    private static String APIKEY;

    private static final String apiEndpointPrefix = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/";

    private static final String signUpNewUser = "signupNewUser?key=";
    private static final String verifyPassword = "verifyPassword?key=";
    private static final String verifyCustomToken = "verifyCustomToken?key=";
    private static final String refreshToken = "token?key=";
    private static final String OAuthSignIn = "verifyAssertion?key=";
    private static final String emailAuthProviders = "createAuthUri?key=";
    private static final String oobConfirmationCode = "getOobConfirmationCode?key=";
    private static final String passwordReset = "resetPassword?key=";
    private static final String setAccountInfo = "setAccountInfo?key=";
    private static final String getAccountInfo = "getAccountInfo?key=";
    private static final String deleteAccount = "deleteAccount?key=";

    private  FirebaseAuthRestEndpoints(){
        // prevent instantiation through constructor
        // create method with a more semantic meaning
    }

    public static void assignApiKey(String APIKEY){
        FirebaseAuthRestEndpoints.APIKEY = APIKEY;
    }

    /**
     * method: POST
     * content-type: application/json
     *
     * --Request Body Payload
     * String email - The email the user is signing in with.
     * String password - The password for the account.
     * Boolean returnSecureToken - Whether or not to return an ID and refresh token. Should always be true.
     *
     * --Response Payload
     * String kind - The request type, always "identitytoolkit#VerifyPasswordResponse".
     * String idToken - A Firebase Auth ID token for the authenticated user.
     * String email - The email for the authenticated user.
     * String refreshToken - A Firebase Auth refresh token for the authenticated user.
     * String expiresIn - The number of seconds in which the ID token expires.
     * String localId - The uid of the authenticated user.
     * Boolean registered - Whether the email is for an existing account.
     *
     * --Common error codes
     * EMAIL_NOT_FOUND: There is no user record corresponding to this identifier. The user may have been deleted.
     * INVALID_PASSWORD: The password is invalid or the user does not have a password.
     * USER_DISABLED: The user account has been disabled by an administrator.
     *
     * @return
     *          A String url for firebase custom token endpoint
     */
    public static final String verifyPasswordEndpoint(){
        return apiEndpointPrefix.concat(verifyPassword.concat(APIKEY));
    }


    /**
     * method: POST
     * content-type: application/json
     *
     * --Request Body Payload
     * String email - The email for the user to create.
     * String password - The password for the user to create.
     * Boolean returnSecureToken - Whether or not to return an ID and refresh token. Should always be true.
     *
     * OR Sign in anonymously with just
     *
     * Boolean returnSecureToken - Whether or not to return an ID and refresh token. Should always be true.
     *
     * --Response Payload
     * String kind - The request type, always "identitytoolkit#SignupNewUserResponse".
     * String idToken - A Firebase Auth ID token for the newly created user.
     * String email - The email for the newly created user.
     * String refreshToken - A Firebase Auth refresh token for the newly created user.
     * String expiresIn - The number of seconds in which the ID token expires.
     * String localId - The uid of the newly created user.
     *
     * --Common error codes
     * EMAIL_EXISTS: The email address is already in use by another account.
     * OPERATION_NOT_ALLOWED: Password sign-in is disabled for this project.
     * TOO_MANY_ATTEMPTS_TRY_LATER: We have blocked all requests from this device due to unusual activity. Try again later.
     *
     * OPERATION_NOT_ALLOWED: Anonymous user sign-in is disabled for this project (on anon sign-in).
     *
     * @return
     *          A String url for firebase custom token endpoint
     */
    public static final String signUpNewUserEndpoint(){
        return apiEndpointPrefix.concat(signUpNewUser.concat(APIKEY));
    }

    /**
     * method: POST
     * content-type: application/x-www-form-urlencoded
     *
     * --Request Body Payload
     * String token - A Firebase Auth custom token from which to create an ID and refresh token pair.
     * Boolean returnSecureToken - Whether or not to return an ID and refresh token. *Should always be true*.
     *
     * --Response Payload
     * String kind - The request type, always "identitytoolkit#VerifyCustomTokenResponse".
     * String idToken - A Firebase Auth ID token generated from the provided custom token.
     * String refreshToken - A Firebase Auth refresh token generated from the provided custom token.
     * String expiresIn - The number of seconds in which the ID token expires.
     *
     * --Common error codes
     * INVALID_CUSTOM_TOKEN: The custom token format is incorrect or the token is invalid for some reason (e.g. expired, invalid signature etc.)
     * CREDENTIAL_MISMATCH: The custom token corresponds to a different Firebase project.
     *
     * @return
     *          A String url for firebase custom token endpoint
     */
    public static final String verifyCustomTokenEndpoint(){
        return apiEndpointPrefix.concat(verifyCustomToken.concat(APIKEY));
    }

    /**
     * method: POST
     * content-type: application/json
     *
     * --Request Body Payload
     * String grant_type - The refresh token's grant type, always "refresh_token".
     * String refresh_token - A Firebase Auth refresh token.
     *
     * --Response Payload
     * String expires_in - The number of seconds in which the ID token expires.
     * String token_type - The type of the refresh token, always "Bearer".
     * String refresh_token - The Firebase Auth refresh token provided in the request or a new refresh token.
     * String id_token - A Firebase Auth ID token.
     * String user_id - The uid corresponding to the provided ID token.
     * String project_id - Your Firebase project ID.
     *
     * --Common error codes
     * TOKEN_EXPIRED: The user's credential is no longer valid. The user must sign in again.
     * USER_DISABLED: The user account has been disabled by an administrator.
     * USER_NOT_FOUND: The user corresponding to the refresh token was not found. It is likely the user was deleted.
     * API key not valid. Please pass a valid API key. (invalid API key provided)
     * INVALID_REFRESH_TOKEN: An invalid refresh token is provided.
     * Invalid JSON payload received. Unknown name \"refresh_tokens\": Cannot bind query parameter. Field 'refresh_tokens' could not be found in request message.
     * INVALID_GRANT_TYPE: the grant type specified is invalid.
     * MISSING_REFRESH_TOKEN: no refresh token provided.
     *
     * @return
     *          A String url for firebase custom token endpoint
     */
    public static final String refreshTokenEndpoint(){
        return apiEndpointPrefix.concat(refreshToken.concat(APIKEY));
    }

    /**
     * method: POST
     * content-type: application/json
     *
     * --Request Body Payload
     * OPTIONAL String idToken - The Firebase ID token of the account you are trying to link the credential to.
     * String requestUri - The URI to which the IDP redirects the user back.
     * String postBody - Contains the OAuth credential (an ID token or access token) and provider ID which issues the credential.
     * Boolean returnSecureToken - Whether or not to return an ID and refresh token. Should always be true.
     * Boolean returnIdpCredential - Whether to force the return of the OAuth credential on the following errors: FEDERATED_USER_ID_ALREADY_LINKED and EMAIL_EXISTS.
     *
     * --Response Payload
     * String kind - The request type, always "identitytoolkit#VerifyAssertionResponse".
     * String federatedId - The unique ID identifies the IdP account.
     * String providerId - The linked provider ID (e.g. "google.com" for the Google provider).
     * String localId - The uid of the authenticated user.
     * Boolean emailVerified - Whether the sign-in email is verified.
     * String email - The email of the account.
     * String oauthIdToken - The OIDC id token if available.
     * String oauthAccessToken - The OAuth access token if available.
     * String oauthTokenSecret - The OAuth 1.0 token secret if available.
     * String rawUserInfo - The stringified JSON response containing all the IdP data corresponding to the provided OAuth credential.
     * String firstName - The first name for the account.
     * String lastName - The last name for the account.
     * String fullName - The full name for the account.
     * String displayName - The display name for the account.
     * String photoUrl - The photo Url for the account.
     * String idToken - A Firebase Auth ID token for the authenticated user.
     * String refreshToken - A Firebase Auth refresh token for the authenticated user.
     * String expiresIn - The number of seconds in which the ID token expires.
     * Boolean needConfirmation - Whether another account with the same credential already exists. The user will need to sign in to the original account and then link the current credential to it.
     *
     * --Common error codes
     * OPERATION_NOT_ALLOWED: The corresponding provider is disabled for this project.
     * INVALID_IDP_RESPONSE: The supplied auth credential is malformed or has expired
     * INVALID_ID_TOKEN:The user's credential is no longer valid. The user must sign in again.
     * EMAIL_EXISTS: The email address is already in use by another account.
     * FEDERATED_USER_ID_ALREADY_LINKED: This credential is already associated with a different user account.
     *
     * @return
     *          A String url for firebase custom token endpoint
     */
    public static final String OAuthSignInEndpoint(){
        return apiEndpointPrefix.concat(OAuthSignIn.concat(APIKEY));
    }

    /**
     * method: POST
     * content-type: application/json
     *
     * --Request Body Payload
     * String identifier - User's email address
     * String continueUri - The URI to which the IDP redirects the user back. For this use case, this is just the current URL.

     * --Response Payload
     * String kind The request type, always "identitytoolkit#CreateAuthUriResponse".
     * String[] allProviders The list of providers that the user has previously signed in with.
     * Boolean registered - Whether the email is for an existing account
     *
     * --Common error codes
     * INVALID_EMAIL: The email address is badly formatted.
     *
     * @return
     *          A String url for firebase custom token endpoint
     */
    public static final String EmailAuthProvidersEndpoint(){
        return apiEndpointPrefix.concat(emailAuthProviders.concat(APIKEY));
    }

    /**
     * method: POST
     * content-type: application/json
     *
     * --Optional Headers
     * X-Firebase-Locale The language code corresponding to the user's locale. Passing this will localize the password reset email sent to the user.
     *
     * --Request Body Payload
     * String requestType - The kind of OOB code to return. "PASSWORD_RESET","VERIFY_EMAIL"
     * OPTIONAL String email - User's email address for password reset.
     * OPTIONAL String idToken - The Firebase ID token of the user to verify for email verification.
     *
     * --Response Payload
     * String kind - The request type, always "identitytoolkit#GetOobConfirmationCodeResponse".
     * String email - User's email address.
     *
     * --Common error codes
     * EMAIL_NOT_FOUND: There is no user record corresponding to this identifier. The user may have been deleted.
     * INVALID_ID_TOKEN: The user's credential is no longer valid. The user must sign in again.
     * USER_NOT_FOUND: There is no user record corresponding to this identifier. The user may have been deleted.
     *
     * @return
     *          A String url for firebase custom token endpoint
     */
    public static final String passwordResetEmailEndpoint(){
        return apiEndpointPrefix.concat(oobConfirmationCode.concat(APIKEY));
    }

    /**
     * method: POST
     * content-type: application/json
     *
     * --Request Body Payload
     * String oobCode - The email action code sent to the user's email for resetting the password.
     * OPTIONAL String newPassword - The user's new password.
     *
     * --Response Payload
     * String kind - The request type, always "identitytoolkit#ResetPasswordResponse".
     * String email - User's email address.
     * String requestType - Type of the email action code. Should be "PASSWORD_RESET".
     *
     * --Common error codes
     * OPERATION_NOT_ALLOWED: Password sign-in is disabled for this project.
     * EXPIRED_OOB_CODE: The action code has expired.
     * INVALID_OOB_CODE: The action code is invalid. This can happen if the code is malformed, expired, or has already been used.
     * USER_DISABLED: The user account has been disabled by an administrator.
     *
     * @return
     *          A String url for firebase custom token endpoint
     */
    public static final String passwordResetEndpoint(){
        return apiEndpointPrefix.concat(passwordReset.concat(APIKEY));
    }

    /**
     * method: POST
     * content-type: application/json
     *
     * --Optional Headers
     * X-Firebase-Locale The language code corresponding to the user's locale. Passing this will localize the password reset email sent to the user.
     *
     * --Request Body Payload
     * String idToken - A Firebase Auth ID token for the user.
     * OPTIONAL String email - The user's new email.
     * OPTIONAL String password - User's new password.
     * OPTIONAL String displayName - User's new display name.
     * OPTIONAL String photoUrl - User's new photo url.
     * OPTIONAL String[] deleteAttribute - List of attributes to delete, "DISPLAY_NAME" or "PHOTO_URL". This will nullify these values.
     * OPTIONAL String[] deleteProvider - The list of provider IDs to unlink, eg: 'google.com', 'password', etc
     * OPTIONAL String oobCode - The action code sent to user's email for email verification.
     * Boolean returnSecureToken - Whether or not to return an ID and refresh token.
     *
     * --Response Payload
     * String kind - The request type, always "identitytoolkit#SetAccountInfoResponse".
     * String localId - The uid of the current user.
     * String email - User's email address.
     * String passwordHash - Hash version of the password.
     * String photoUrl - User's new photo url.
     * List<Object> providerUserInfo - List of all linked provider objects which contain "providerId" and "federatedId".
     * String idToken - New Firebase Auth ID token for user.
     * String refreshToken - A Firebase Auth refresh token.
     * String expiresIn - The number of seconds in which the ID token expires.     *
     *
     * --Common error codes
     * WEAK_PASSWORD: The password must be 6 characters long or more.
     * EMAIL_EXISTS: The email address is already in use by another account.
     * INVALID_ID_TOKEN:The user's credential is no longer valid. The user must sign in again.
     * CREDENTIAL_TOO_OLD_LOGIN_AGAIN: The user's credential is no longer valid. The user must sign in again.
     * TOKEN_EXPIRED: The user's credential is no longer valid. The user must sign in again.
     * EXPIRED_OOB_CODE: The action code has expired.
     * INVALID_OOB_CODE: The action code is invalid. This can happen if the code is malformed, expired, or has already been used.
     * USER_DISABLED: The user account has been disabled by an administrator.
     * EMAIL_NOT_FOUND: There is no user record corresponding to this identifier. The user may have been deleted.
     *
     * @return
     *          A String url for firebase custom token endpoint
     */
    public static final String setAccountInfoEndpoint(){
        return apiEndpointPrefix.concat(setAccountInfo.concat(APIKEY));
    }

    /**
     * method: POST
     * content-type: application/json
     *
     * --Request Body Payload
     * String idToken - A Firebase Auth ID token for the user.
     *
     * --Response Payload
     * String kind - The request type, always "identitytoolkit#DeleteAccountResponse".
     *
     * --Common error codes
     * INVALID_ID_TOKEN:The user's credential is no longer valid. The user must sign in again.
     * USER_NOT_FOUND: There is no user record corresponding to this identifier. The user may have been deleted.
     *
     * @return
     *          A String url for firebase custom token endpoint
     */
    public static final String deleteAccountEndpoint(){
        return apiEndpointPrefix.concat(deleteAccount.concat(APIKEY));
    }

    /**
     * method: POST
     * content-type: application/json
     *
     * --Request Body Payload
     * String idToken - A Firebase Auth ID token for the user.
     *
     * --Response Payload
     * String kind - The request type, always "identitytoolkit#GetAccountInfoResponse".
     * List<Object> users - The account associated with the given Firebase ID token. Check below for more details.
     *      String localId - The uid of the current user.
     *      String email - The email of the account.
     *      Boolean emailVerified - Whether or not the account's email has been verified.
     *      String displayName - The display name for the account.
     *      List<Object> providerUserInfo - List of all linked provider objects which contain "providerId" and "federatedId".
     *      String photoUrl - The photo Url for the account.
     *      String passwordHash - Hash version of password.
     *      Double passwordUpdatedAt - The timestamp, in milliseconds, that the account password was last changed.
     *      String validSince - The timestamp, in seconds, which marks a boundary, before which Firebase ID token are considered revoked.
     *      String disabled - Whether the account is disabled or not.
     *      String lastLoginAt - The timestamp, in milliseconds, that the account last logged in at.
     *      String createdAt - The timestamp, in milliseconds, that the account was created at.
     *      Boolean customAuth - Whether the account is authenticated by the developer.
     *
     * --Common error codes
     * INVALID_ID_TOKEN:The user's credential is no longer valid. The user must sign in again.
     * USER_NOT_FOUND: There is no user record corresponding to this identifier. The user may have been deleted.
     *
     * @return
     *          A String url for firebase custom token endpoint
     */
    public static final String getAccountInfoEndpoint(){
        return apiEndpointPrefix.concat(getAccountInfo.concat(APIKEY));
    }

}
