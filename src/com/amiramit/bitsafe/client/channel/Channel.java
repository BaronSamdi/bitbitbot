package com.amiramit.bitsafe.client.channel;

import java.util.ArrayList;
import java.util.List;

import com.amiramit.bitsafe.client.service.ChannelListenerService;
import com.google.appengine.api.channel.ChannelService;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Channel {
    private final List<ChannelListenerService> channelListeners;
    private final String channelName;
    private final ChannelServiceAsync channelService = GWT.create(ChannelService.class);
    private String token;

    public Channel(String channelName) {
        this.channelName = channelName;
        channelListeners = new ArrayList<ChannelListenerService>();
    }

    private void onMessage(String message) {
        for (int i = 0; i < channelListeners.size(); i++) {
            channelListeners.get(i).onMessage(message);
        }
    }

    private void onOpen() {
        for (int i = 0; i < channelListeners.size(); i++) {
            channelListeners.get(i).onOpen();
        }
    }

    private void onError(int code, String description) {
        for (int i = 0; i < channelListeners.size(); i++) {
            channelListeners.get(i).onError(code, description);
        }
    }

    private void onClose() {
        for (int i = 0; i < channelListeners.size(); i++) {
            channelListeners.get(i).onClose();
        }
    }

    public void join() {
        channelService.join(channelName, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable throwable) {
            }

            @Override
            public void onSuccess(String t) {
                token = t;
                join(token);
            }
        });
    }

    public void addChannelListener(ChannelListenerService channelListener) {
        channelListeners.add(channelListener);
    }

    public void removeChannelListener(ChannelListenerService channelListener) {
        channelListeners.remove(channelListener);
    }

    private native void join(String channelKey) /*-{
        var channel = new $wnd.goog.appengine.Channel(channelKey);
        var socket = channel.open();
        var self = this;

        socket.onmessage = function(evt) {
            var data = evt.data;
            self.@no.eirikb.gwtchannelapi.client.Channel::onMessage(Ljava/lang/String;)(data);
        };

        socket.onopen = function() {
            self.@no.eirikb.gwtchannelapi.client.Channel::onOpen()();
        };

        socket.onerror = function(error) {
            self.@no.eirikb.gwtchannelapi.client.Channel::onError(ILjava/lang/String;)(error.code, error.description);
        };

        socket.onclose = function() {
            self.@no.eirikb.gwtchannelapi.client.Channel::onClose()();
        };
    }-*/;

    public void send(String message) {
        send(message, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
            }

            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    public void send(String message, AsyncCallback<Void> callback) {
        channelService.onMessage(token, channelName, message, callback);
    }
}
