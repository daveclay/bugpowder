$(document).ready(function() {
    var fear = new Fear();
    fear.load();
});

function Fear() {
    this.container = $('<div/>');
    this.container.addClass("container");
    addToBody(this.container);
    this.index = 0;
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
    images.forEach(function(image) {
        self.handleImage(image);
    });

    this.startSequence();
};

Fear.prototype.handleImage = function(imageUrl) {
    var img = $('<img/>');
    img.addClass("fearImg hidden");
    img.attr("src", imageUrl);
    var image = {};
    image.element = img;
    this.images.push(image);
    this.container.append(img);
};

Fear.prototype.nextImage = function(image) {
    this.images[this.index].element.addClass("hidden");
    this.index++;
    this.images[this.index].element.removeClass("hidden");
};

Fear.prototype.startSequence = function(image) {
    var self = this;
    setInterval(function() {
        self.nextImage();
    }, 100);
};

