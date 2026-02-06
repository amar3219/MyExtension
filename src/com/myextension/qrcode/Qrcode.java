package com.myextension.qrcode;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import android.webkit.WebView;
import android.webkit.ValueCallback;

@DesignerComponent(version = 1,
    description = "Extension for Live Variable Monitoring & URL Control",
    category = ComponentCategory.EXTENSION,
    nonVisible = true)
@SimpleObject(external = true)
public class Qrcode extends AndroidNonvisibleComponent {

    private WebView web;

    public Qrcode(ComponentContainer container) {
        super(container.$form());
    }

    // WebViewer ko connect karein
    @SimpleProperty(description = "Connect existing WebViewer to this extension")
    public void WebViewerComponent(WebViewer webViewer) {
        this.web = (WebView) webViewer.getView();
    }

    // URL set aur load karein
    @SimpleFunction(description = "Load a specific URL in the connected WebViewer")
    public void SetGameURL(String url) {
        if (this.web != null) {
            this.web.loadUrl(url);
        }
    }

    // Saare variables ki list nikalne ke liye
    @SimpleFunction(description = "Fetches all global variables from the game")
    public void GetVariableList() {
        if (this.web != null) {
            final String script = "Object.keys(window).filter(k => typeof window[k] !== 'function').join(',');";
            this.web.post(new Runnable() {
                @Override
                public void run() {
                    web.evaluateJavascript(script, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            OnListReceived(value.replace("\"", "")); 
                        }
                    });
                }
            });
        }
    }

    // Kisi variable ki live value lene ke liye
    @SimpleFunction(description = "Get the live value of a variable by its name")
    public void GetValueByName(final String varName) {
        if (this.web != null) {
            final String script = "window['" + varName + "']";
            this.web.post(new Runnable() {
                @Override
                public void run() {
                    web.evaluateJavascript(script, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            OnValueReceived(varName, value.replace("\"", ""));
                        }
                    });
                }
            });
        }
    }

    @SimpleEvent(description = "Triggered when variable list is fetched")
    public void OnListReceived(String list) {
        EventDispatcher.dispatchEvent(this, "OnListReceived", list);
    }

    @SimpleEvent(description = "Triggered when a specific variable value is received")
    public void OnValueReceived(String name, String value) {
        EventDispatcher.dispatchEvent(this, "OnValueReceived", name, value);
    }
}
