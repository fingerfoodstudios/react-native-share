package cl.json.social;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cl.json.ShareFile;

/**
 * Created by disenodosbbcl on 23-07-16.
 */
public abstract class SingleShareIntent extends ShareIntent {

    protected String playStoreURL = null;
    protected String appStoreURL = null;

    public SingleShareIntent(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    public void open(ReadableMap options) throws ActivityNotFoundException {
        System.out.println(getPackage());
        //  check if package is installed
        if(getPackage() != null || getDefaultWebLink() != null || getPlayStoreLink() != null) {
            if(this.isPackageInstalled(getPackage(), reactContext)) {
                System.out.println("INSTALLED");
                this.getIntent().setPackage(getPackage());
                super.open(options);
            } else {
                System.out.println("NOT INSTALLED");
                String url = "";
                String defaultWebLink = getDefaultWebLink();
                if(defaultWebLink != null) {
                    // check if default url has valid replacement strings. Was failing on replace if strings were not present.
                    if(defaultWebLink.contains("{url}") && defaultWebLink.contains("{message}"))
                    {
                        url = defaultWebLink
                                .replace("{url}",    this.urlEncode( options.getString("url")))
                                .replace("{message}",this.urlEncode( options.getString("message")));
                    }
                    else if(defaultWebLink.contains("{url}"))
                    {
                        url = defaultWebLink.replace("{url}", this.urlEncode( options.getString("url")));
                    }
                    else if(defaultWebLink.contains("{message}"))
                    {
                        url = defaultWebLink.replace("{message}", this.urlEncode(options.getString("message")));
                    }
                } else if(getPlayStoreLink() != null) {
                    url = getPlayStoreLink();
                } else {
                    //  TODO
                }
                //  open web intent
                this.setIntent(new Intent(new Intent("android.intent.action.VIEW", Uri.parse(url))));
            }
        }
        //  configure default
        super.open(options);
    }
    protected void openIntentChooser() throws ActivityNotFoundException {
        this.getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.reactContext.startActivity(this.getIntent());
    }
}
