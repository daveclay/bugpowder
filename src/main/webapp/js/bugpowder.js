$(document).ready(function() {
    var fear = new Fear();
    fear.load();
    var ticker = new Ticker();
    ticker.load();
    var ebcs = new EBCS();
    ebcs.load();
});

function Ticker() {
    this.imageTimer = new Timer();
    this.imageTimer.delay = 200;
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
    this.imageTimer.onUpdate(function(tick) {
        self.moveTicker(tick);
    });
    this.tickerText = ticker.text;
    this.tickerLength = this.tickerText.length;
    this.updateText();
    this.imageTimer.start();
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
    this.imageTimer = new Timer();
    this.imageTimer.delay = 70;
    this.container = $('<div/>');
    this.container.addClass("container");
    addToBody(this.container);
    this.currentlyVisibleImage = 0;
    this.images = [];
    this.insanitySwap = false;

    var self = this;
    this.insanityTimer = new Timer();
    this.adjustInsanity();
    this.insanityTimer.onUpdate(function() {
        self.swapInsanity();
    });
    this.imageTimer.onLoopStart(function() {
        self.images = shuffle(self.images);
    });
    this.insanityTimer.start();
}

Fear.prototype.adjustInsanity = function() {
    this.insanityTimer.delay = Math.floor(Math.random() * 10000);
};

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

    this.imageTimer.length = this.images.length;
    this.imageTimer.onUpdate(function(tick) {
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
    this.adjustInsanity();
    var self = this;
    if (this.insanitySwap) {
        this.images.forEach(function(image) {
            image.element.addClass("fearImg hidden");
            image.element.removeClass("fearImgLayout");
        });
        this.imageTimer.onUpdate(function(tick) {
            self.nextImage(tick);
        });
    } else {
        this.images.forEach(function(image) {
            image.element.removeClass("fearImg hidden");
            image.element.addClass("fearImgLayout");
        });
        this.imageTimer.onUpdate(function(tick) {
            self.insanity(tick);
        });
    }
    this.insanitySwap = ! this.insanitySwap;
};

Fear.prototype.insanity = function(tick) {
    var self = this;
    this.images.forEach(function(image) {
        var randomImage = self.randomer.pickRandom(self.images);
        if (randomImage) {
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
    this.imageTimer.start();
};

function EBCS() {
    this.audioClipSpecList = [];
};

EBCS.prototype.load = function() {
    var self = this;
    get("fear/audio", function(audioClips) {
    	self.handleAudioClipSpecList(audioClips);
    });
};

EBCS.prototype.handleAudioClipSpecList = function(fileNames) {
    var self = this;
    this.audioClipSpecList = fileNames;
    this.startPlayingAudio();
};

EBCS.prototype.startPlayingAudio = function() {
    var self = this;
    this.buzzSounds = [];

    this.audioClipSpecList.forEach(function(audioClipSpec, index) {
        var file = audioClipSpec.file;
        var formatsArray = audioClipSpec.formats;
        var sound = new buzz.sound(file, { formats: formatsArray });
        sound.index = index;

        if (index > 0) {
            var previous = self.buzzSounds[index - 1];
            previous.next = sound;
        }

        var playNextSound = function() {
            if (sound.next) {
                sound.next.play();
            } else {
                console.log("No next sound found for " + index);
            }
        };

        sound.bind("ended", function(e) {
            playNextSound();
        });

        sound.bind("error", function(e) {
            playNextSound();
        });

        self.buzzSounds.push(sound);
    });

    var first = this.buzzSounds[0];
    first.next = this.buzzSounds[1];
    var last = this.buzzSounds[this.buzzSounds.length - 1];
    last.next = first;
    this.buzzSounds[0].play();

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
    this.onLoopStartHandler = function() {};
}

Timer.prototype.onLoopStart = function(handler) {
    this.onLoopStartHandler = handler;
};

Timer.prototype.onUpdate = function(handler) {
    this.onUpdateHandler = handler;
};

Timer.prototype.start = function() {
    this.trigger();
};

Timer.prototype.trigger = function() {
    var self = this;
    this.timeout = setTimeout(function() {
        self.callOnTick();
    }, this.delay);
};

Timer.prototype.callOnLoopStart = function() {
    this.onLoopStartHandler();
};

Timer.prototype.callOnTick = function() {
    this.onUpdateHandler(this.tick);
    this.tick++;
    if (this.tick >= this.length) {
        this.tick = 0;
        this.callOnLoopStart();
    }
    this.trigger();
};

Timer.prototype.stop = function() {
    clearInterval(this.interval);
};

function shuffle(o) {
    for (var j, x, i = o.length; i; j = parseInt(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
    return o;
}

