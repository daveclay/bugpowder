$(document).ready(function() {
    var fear = new Fear();
    fear.load();
});

function Fear() {
    this.container = $('<div/>');
    this.container.addClass("container");
    addToBody(this.container);
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
};

Fear.prototype.handleImage = function(image) {
    var img = $('<img/>');
    img.attr("src", image);
    this.container.append(img);
};

