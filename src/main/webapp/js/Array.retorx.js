Array.prototype.forEach = function(callback) {
	for (var i = 0; i < this.length; i++) {
		callback(this[i], i);
	}
};

Array.prototype.addAll = function(others) {
	var thisArray = this;
	others.foreach(function(e) {
		thisArray.push(e);
	});
};

Array.prototype.findAll = function(predicate) {
	var found = new Array();
	this.forEach(function(current) {
		if (predicate(current)) {
			found.push(current);
		}
	});

	return found;
};

Array.prototype.find = function(predicate) {
	var foundAll = this.findAll(predicate);
	if (foundAll.length == 0) {
		return null;
	} else {
		return foundAll[0];
	}
};

Array.prototype.peek = function() {
	return this[this.length - 1];
}

Array.prototype.copy = function(predicate) {
	var newArray = new Array();
	this.forEach(function(element) {
		if (predicate(element)) {
			newArray.push(element);
		}
	});

	return newArray;
};
