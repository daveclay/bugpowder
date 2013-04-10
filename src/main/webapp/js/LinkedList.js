function LinkedListHelper(obj) {
	this.obj = obj;
}

LinkedListHelper.prototype.forEach = function(callback) {
	var element = this.obj.previous
	while (element) {
		callback(element);
		element = element.previous;
	}

	element = this.obj.next;
	while (element) {
		callback(element);
		element = element.next;
	}
};

