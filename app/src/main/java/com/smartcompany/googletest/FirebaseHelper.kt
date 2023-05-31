package com.smartcompany.googletest
import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient

class FirebaseHelper(private val context: Context) {

    private var oneTapClient: SignInClient = Identity.getSignInClient(context);
    private var signUpRequest: BeginSignInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(context.getString(R.string.web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build())
        .build();

    fun getoneTapClient(): SignInClient {
        return oneTapClient;
    }

    fun getsignUpRequest(): BeginSignInRequest {
        return signUpRequest;
    }


}