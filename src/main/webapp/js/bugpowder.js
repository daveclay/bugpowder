$(document).ready(function() {
    var fear = new Fear();
    fear.load();
});

function Image(element, index) {
    this.element = element;
    this.index = index;
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
    var image = new Image(img, idx);
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

function Randomer() {
}

Randomer.prototype.moreOftenThan = function(percent) {
    return this.randomInt(100) > percent;
};

Randomer.prototype.randomInt = function(range) {
    return Math.round(Math.random() * range);
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


