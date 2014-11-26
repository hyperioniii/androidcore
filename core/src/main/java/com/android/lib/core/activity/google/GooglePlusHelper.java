package com.android.lib.core.activity.google;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;

import com.android.lib.core.util.StringUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;

import java.util.List;

/**
 * Created by tuannx on 11/26/2014.
 */
public class GooglePlusHelper implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 99918;
    private Activity context;

    private GoogleApiClient mGoogleApiClient;

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener;
    private boolean mIntentInProgress;


    public GooglePlusHelper(Activity context) {
        this.context = context;
        mGoogleApiClient = new GoogleApiClient.Builder(this.context)
                .addApi(Plus.API)
                .addOnConnectionFailedListener(this)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    /**
     * @param connectionCallbacks
     * @param onConnectionFailedListener
     */
    public void connect(GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        if (this.connectionCallbacks != null) {
            mGoogleApiClient.unregisterConnectionCallbacks(this.connectionCallbacks);
        }
        this.connectionCallbacks = connectionCallbacks;
        this.onConnectionFailedListener = onConnectionFailedListener;
        mGoogleApiClient.connect();
    }

    /**
     *
     * @param requestCode
     * @param responseCode
     * @param intent
     * @return handle by helper or not
     */
    public boolean onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
            return true;
        }
        return false;
    }

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError(ConnectionResult mConnectionResult) {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                context.startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    public void loadVisible(ResultCallback<People.LoadPeopleResult> callback){
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                .setResultCallback(callback);
    }

    public String  getEmail(){
        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        return email;
    }

    /**
     * When you share media to Google+, you cannot also use the setContentUrl method. If you want to include a URL in the post with the media, you should append the URL to the prefilled text in the setText() method.
     *
     * @param text
     * @param contentUrl
     * @param deepLinkId
     * @param stream
     * @param mimeType
     * @param recipients
     * @param requestCode
     */
    public void share(ActionGooglePlus actionToCall,CharSequence text, Uri contentUrl, String deepLinkId, Uri stream, String mimeType, List<Person> recipients, int requestCode) {
        PlusShare.Builder builder = new PlusShare.Builder(context);
        if(actionToCall!=null){
            builder.addCallToAction(actionToCall.getLabel(),actionToCall.getUri(),actionToCall.getDeepLinkId());
        }

        if (!StringUtil.isEmpty(text))
            builder.setText(text);
        if (contentUrl!=null)
            builder.setContentUrl(contentUrl);
        if (recipients!=null && recipients.size()>0)
            builder.setRecipients(recipients);
        if (stream!=null){
            builder.setStream(stream);
            text = StringUtil.isEmpty(text)?"":text + contentUrl.getPath();
            builder.setText(text);
        }
        if (!StringUtil.isEmpty(deepLinkId))
            builder.setContentDeepLinkId(deepLinkId);
        if (!StringUtil.isEmpty(mimeType))
            builder.setType(mimeType);
        context.startActivityForResult(builder.getIntent(), requestCode);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        resolveSignInError(connectionResult);
        if (onConnectionFailedListener != null) {
            onConnectionFailedListener.onConnectionFailed(connectionResult);
        }
    }

    public void onDestroy() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public static  class ActionToCall{
        public static final String ACCEPT = "ACCEPT";
        public static final String ACCEPT_GIFT = "ACCEPT_GIFT";
        public static final String ADD = "ADD";
        public static final String ADD_FRIEND = "ADD_FRIEND";
        public static final String ADD_ME = "ADD_ME";
        public static final String ADD_TO_CALENDAR = "ADD_TO_CALENDAR";
        public static final String ADD_TO_CART = "ADD_TO_CART";
        public static final String ADD_TO_FAVORITES = "ADD_TO_FAVORITES";
        public static final String ADD_TO_QUEUE = "ADD_TO_QUEUE";
        public static final String ADD_TO_WISH_LIST = "ADD_TO_WISH_LIST";
        public static final String ANSWER = "ANSWER";
        public static final String ANSWER_QUIZ = "ANSWER_QUIZ";
        public static final String APPLY = "APPLY";
        public static final String ASK = "ASK";
        public static final String ATTACK = "ATTACK";
        public static final String BEAT = "BEAT";
        public static final String BID = "BID";
        public static final String BOOK = "BOOK";
        public static final String BOOKMARK = "BOOKMARK";
        public static final String BROWSE = "BROWSE";
        public static final String BUY = "BUY";
        public static final String CAPTURE = "CAPTURE";
        public static final String CHALLENGE = "CHALLENGE";
        public static final String CHANGE = "CHANGE";
        public static final String CHAT = "CHAT";
        public static final String CHECKIN = "CHECKIN";
        public static final String COLLECT = "COLLECT";
        public static final String COMMENT = "COMMENT";
        public static final String COMPARE = "COMPARE";
        public static final String COMPLAIN = "COMPLAIN";
        public static final String CONFIRM = "CONFIRM";
        public static final String CONNECT = "CONNECT";
        public static final String CONTRIBUTE = "CONTRIBUTE";
        public static final String COOK = "COOK";
        public static final String CREATE = "CREATE";
        public static final String DEFEND = "DEFEND";
        public static final String DINE = "DINE";
        public static final String DISCOVER = "DISCOVER";
        public static final String DISCUSS = "DISCUSS";
        public static final String DONATE = "DONATE";
        public static final String DOWNLOAD = "DOWNLOAD";
        public static final String EARN = "EARN";
        public static final String EAT = "EAT";
        public static final String EXPLAIN = "EXPLAIN";
        public static final String FIND = "FIND";
        public static final String FIND_A_TABLE = "FIND_A_TABLE";
        public static final String FOLLOW = "FOLLOW";
        public static final String GET = "GET";
        public static final String GIFT = "GIFT";
        public static final String GIVE = "GIVE";
        public static final String GO = "GO";
        public static final String HELP = "HELP";
        public static final String IDENTIFY = "IDENTIFY";
        public static final String INSTALL = "INSTALL";
        public static final String INSTALL_APP = "INSTALL_APP";
        public static final String INTRODUCE = "INTRODUCE";
        public static final String INVITE = "INVITE";
        public static final String JOIN = "JOIN";
        public static final String JOIN_ME = "JOIN_ME";
        public static final String LEARN = "LEARN";
        public static final String LEARN_MORE = "LEARN_MORE";
        public static final String LISTEN = "LISTEN";
        public static final String MAKE = "MAKE";
        public static final String MATCH = "MATCH";
        public static final String MESSAGE = "MESSAGE";
        public static final String OPEN = "OPEN";
        public static final String OPEN_APP = "OPEN_APP";
        public static final String OWN = "OWN";
        public static final String PAY = "PAY";
        public static final String PIN = "PIN";
        public static final String PIN_IT = "PIN_IT";
        public static final String PLAN = "PLAN";
        public static final String PLAY = "PLAY";
        public static final String PURCHASE = "PURCHASE";
        public static final String RATE = "RATE";
        public static final String READ = "READ";
        public static final String READ_MORE = "READ_MORE";
        public static final String RECOMMEND = "RECOMMEND";
        public static final String RECORD = "RECORD";
        public static final String REDEEM = "REDEEM";
        public static final String REGISTER = "REGISTER";
        public static final String REPLY = "REPLY";
        public static final String RESERVE = "RESERVE";
        public static final String REVIEW = "REVIEW";
        public static final String RSVP = "RSVP";
        public static final String SAVE = "SAVE";
        public static final String SAVE_OFFER = "SAVE_OFFER";
        public static final String SEE_DEMO = "SEE_DEMO";
        public static final String SELL = "SELL";
        public static final String SEND = "SEND";
        public static final String SIGN_IN = "SIGN_IN";
        public static final String SIGN_UP = "SIGN_UP";
        public static final String START = "START";
        public static final String STOP = "STOP";
        public static final String SUBSCRIBE = "SUBSCRIBE";
        public static final String TAKE_QUIZ = "TAKE_QUIZ";
        public static final String TAKE_TEST = "TAKE_TEST";
        public static final String TRY_IT = "TRY_IT";
        public static final String UPVOTE = "UPVOTE";
        public static final String USE = "USE";
        public static final String VIEW = "VIEW";
        public static final String VIEW_ITEM = "VIEW_ITEM";
        public static final String VIEW_MENU = "VIEW_MENU";
        public static final String VIEW_PROFILE = "VIEW_PROFILE";
        public static final String VISIT = "VISIT";
        public static final String VOTE = "VOTE";
        public static final String WANT = "WANT";
        public static final String WANT_TO_SEE = "WANT_TO_SEE";
        public static final String WANT_TO_SEE_IT = "WANT_TO_SEE_IT";
        public static final String WATCH = "WATCH";
        public static final String WATCH_TRAILER = "WATCH_TRAILER";
        public static final String WISH = "WISH";
        public static final String WRITE = "WRITE";
    }
    public static class ActionGooglePlus{
        private String label;
        private Uri uri;
        private String deepLinkId;

        public ActionGooglePlus(String label, Uri uri, String deepLinkId) {
            this.label = label;
            this.uri = uri;
            this.deepLinkId = deepLinkId;
        }

        public String getLabel() {
            return label;
        }

        public Uri getUri() {
            return uri;
        }

        public String getDeepLinkId() {
            return deepLinkId;
        }
    }
}
