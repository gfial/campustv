var DEFAULTOFFSET = 15;

function TvSession (session) {
	this.session = session;
	this.channelCounter = 0;
    this.channelsSessions = [];
    this.smartTvSession = null;
    this.setupChannels();
}

//Reloads the channels of the session.
TvSession.prototype.setupChannels = function() {
    this.smartTvSession = new ChannelSession(this.session.member.smartTv);
    var i;
    for(i = 0; i < this.session.member.channels.length; i++) {
        this.channelsSessions[i] = new ChannelSession(this.session.member.channels[i]);
    }
};

TvSession.prototype.loadSmartTvNews = function(callback, errorCallback) {
    this.smartTvSession.downloadNews(callback, errorCallback);
};

TvSession.prototype.loadChannel = function(index, callback, errorCallback) {
	this.channelsSessions[index].downloadNews(callback, errorCallback);
};

TvSession.prototype.loadChannels = function(callback) {
    var tvSession = this;
	var readyChannels = 0;
    var i;
    if(this.session.member.channels.length == 0) {
        callback();
        return;
    }
	for(i = 0; i < this.session.member.channels.length; i++) {
		this.loadChannel(i, function () {
			readyChannels++;
            if(readyChannels == tvSession.session.member.channels.length) {
                callback();
			}
		}, function(error) {
            console.log(error);
        });
	}
};

TvSession.prototype.loadTvChannels = function(callback) {
	var ready = false;
	this.loadSmartTvNews(function() {
		if(ready) {
			callback();
		} else {
			ready = true;
		}
	}, function(error) {
        console.log(error);
    });

	this.loadChannels(function() {
		if(ready) {
			callback();
		} else {
			ready = true;
		}
	});
};

TvSession.prototype.getNextChannel = function() {
    //Gets the previous channel.
    var previousChannel = this.channelCounter;
    var prevChannel = this.channelsSessions[previousChannel];

    //Gets the next channel.
    this.channelCounter++;
	this.channelCounter = this.channelCounter % this.session.member.channels.length;

    //Gets the next channel.
    var newChannel = this.channelsSessions[this.channelCounter];
    //Loads the next channel.
    this.loadChannel(previousChannel, function(loadedNews) {
        prevChannel.news = loadedNews;
    });
    console.log(newChannel.channel.name);
    return newChannel;
};

TvSession.prototype.getSmartTvNews = function() {
    this.loadSmartTvNews(function() {});
};

function setupTvSession(session, callback) {
	var tvSession = new TvSession(session);
	tvSession.loadTvChannels(function() {
        callback(tvSession);
    });
}

