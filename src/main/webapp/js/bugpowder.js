$(document).ready(function() {
    var fear = new Fear();
    fear.load();
    var ticker = new Ticker();
    ticker.load();
    var ebcs = new EBCS();
    ebcs.load();
});

function Ticker() {
    this.timer = new Timer();
    this.timer.delay = 200;
    this.element = $('<div/>');
    this.element.addClass("ticker");
    addToBody(this.element);
}

Ticker.prototype.load = function() {
    var self = this;
    get("fear/feeds", function(ticker) {
        self.handleTicker(ticker)
    });
};

Ticker.prototype.handleTicker = function(ticker) {
    var self = this;
    this.timer.onUpdate(function(tick) {
        self.moveTicker(tick);
    });
    this.tickerText = ticker.text;
    this.tickerLength = this.tickerText.length;
    this.updateText();
    this.timer.start();
};

Ticker.prototype.updateText = function() {
    this.element.text(this.tickerText);
};

Ticker.prototype.moveTicker = function(tick) {
    this.tickerText = this.tickerText.substr(1, this.tickerLength - 2) + this.tickerText.charAt(0);
    this.updateText();
};

function Image(element, index, imageUrl) {
    this.element = element;
    this.index = index;
    this.imageUrl = imageUrl;
}

Image.prototype.show = function() {
    this.element.removeClass("hidden");
};

Image.prototype.hide = function() {
    this.element.addClass("hidden");
};

function Fear() {
    this.randomer = new Randomer();
    this.timer = new Timer();
    this.timer.delay = 70;
    this.container = $('<div/>');
    this.container.addClass("container");
    addToBody(this.container);
    this.currentlyVisibleImage = 0;
    this.images = [];
    this.insanitySwap = false;

    var self = this;
    this.insanityInterval = setInterval(function() {
        self.swapInsanity();
    }, 10000);
}

Fear.prototype.load = function() {
    var self = this;
    get("fear/images", function(images) {
        self.handleImages(images);
    });
};

Fear.prototype.handleImages = function(images) {
    var self = this;
    images.forEach(function(image, idx) {
        self.handleImage(image, idx);
    });

    this.timer.length = this.images.length;
    this.timer.onUpdate(function(tick) {
        self.nextImage(tick);
    });
    this.chosenImage = this.images[this.randomer.randomInt(this.images.length)];
    this.startSequence();
};

Fear.prototype.handleImage = function(imageUrl, idx) {
    var img = $('<img/>');
    img.addClass("fearImg hidden");
    img.attr("src", imageUrl);
    var image = new Image(img, idx, imageUrl);
    this.images.push(image);
    this.container.append(img);
};

Fear.prototype.nextImage = function(tick) {
    if (this.randomer.moreOftenThan(90)) {
        this.show(this.chosenImage);
    } else if (this.randomer.moreOftenThan(80)) {
    } else {
        this.show(this.images[tick]);
    }
};

Fear.prototype.swapInsanity = function() {
    var self = this;
    if (this.insanitySwap) {
        this.images.forEach(function(image) {
            image.element.addClass("fearImg hidden");
            image.element.removeClass("fearImgLayout");
        });
        this.timer.onUpdate(function(tick) {
            self.nextImage(tick);
        });
    } else {
        this.images.forEach(function(image) {
            image.element.removeClass("fearImg hidden");
            image.element.addClass("fearImgLayout");
        });
        this.timer.onUpdate(function(tick) {
            self.insanity(tick);
        });
    }
    this.insanitySwap = ! this.insanitySwap;
};

Fear.prototype.insanity = function(tick) {
    var self = this;
    this.images.forEach(function(image) {
        var randomImage = self.randomer.pickRandom(self.images);
        if ( ! randomImage) {
            console.log("WTF: " + randomImage);
        } else {
            var src = randomImage.imageUrl;
            $(image.element).attr("src", src);
        }
    });
};

Fear.prototype.show = function(image) {
    if (this.currentlyVisibleImage) {
        this.currentlyVisibleImage.hide();
    }
    image.show();
    this.currentlyVisibleImage = image;
};

Fear.prototype.startSequence = function() {
    this.timer.start();
};

function EBCS() {
    this.audioFileNameList = Array();
    this.nextAudioFileIdx = -1;
};

EBCS.prototype.load = function() {
    var self = this;
    get("fear/audio", function(audioClips) {
    	self.handleAudioFileNameList(audioClips);
    });
};

EBCS.prototype.handleAudioFileNameList = function(fileNames) {
    var self = this;
    this.audioFileNameList = fileNames;
    this.startPlayingAudio();
};

EBCS.prototype.playNextAudioFile = function() {
    var self = this;
    var audio = new Audio();
    audio.src = this.audioFileNameList[this.nextAudioFileIdx];
    audio.addEventListener("ended", function() { self.playNextAudioFile() }, false);
    this.nextAudioFileIdx ++;
    if (this.nextAudioFileIdx == this.audioFileNameList.length) {
        this.nextAudioFileIdx = 0;
    }
    audio.play();
};

EBCS.prototype.startPlayingAudio = function() {
    var self = this;
    this.nextAudioFileIdx = 0;
    this.playNextAudioFile();
};

function Randomer() {
}

Randomer.prototype.moreOftenThan = function(percent) {
    return this.randomInt(100) > percent;
};

Randomer.prototype.randomInt = function(range) {
    return Math.floor(Math.random() * range);
};

Randomer.prototype.pickRandom = function(array) {
    var index = Math.floor(Math.random() * array.length);
    return array[index];
};

function Timer() {
    this.tick = 0;
    this.length = 100;
    this.delay = 100;
    this.onUpdateHandler = function() {};
}

Timer.prototype.onUpdate = function(handler) {
    this.onUpdateHandler = handler;
};

Timer.prototype.start = function() {
    var self = this;
    this.interval = setInterval(function() {
        self.update();
    }, this.delay);
};

Timer.prototype.update = function() {
    this.onUpdateHandler(this.tick);
    this.tick++;
    if (this.tick >= this.length) {
        this.tick = 0;
    }
};

Timer.prototype.stop = function() {
    clearInterval(this.interval);
};


