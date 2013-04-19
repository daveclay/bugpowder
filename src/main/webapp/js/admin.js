function appendPropertiesEditor(image, caption) {
    var propertiesDiv = $('<div/>');
	var properties = image.properties;
	if (!properties) {
		properties = {}
	}

	for (var name in properties) {
		appendPropertyEditorField(name, properties[name], propertiesDiv);
	}

	appendPropertyEditorField("", "", propertiesDiv);

    var editorDiv = $('<div/>');
    editorDiv.addClass("adminProperties");
    var add = $('<input/>');
    add.attr("type", "button");
    add.val("add");
    add.click(function() {
        appendPropertyEditorField("", "", propertiesDiv);
    });

    editorDiv.append(propertiesDiv);
    editorDiv.append(add);

	var save = $("<input/>");
	save.attr("type", "button");
	save.val("save");
	save.click(function() {
		saveProperties(editorDiv, image);
	});

	editorDiv.append($("<br/>"));
	editorDiv.append(save);
	caption.append(editorDiv);
}

function appendPropertyEditorField(name, value, editorDiv) {
	var nameField = $("<input/>");
	nameField.addClass("propName");
	nameField.attr("name", "name");
	nameField.val(name);

	var valueField = $("<input/>");
	valueField.addClass("propValue");
	valueField.attr("name", "value");
	valueField.val(value);

	var span = $("<span/>");
	span.addClass("propertySpan");
	span.append(nameField);
	span.append(valueField);

	editorDiv.append($("<br/>"));
	editorDiv.append(span);
}

function saveProperties(editorDiv, image) {
	var properties = {};

	editorDiv.find(".propertySpan").each(function(i, propertySpan) {
		var name = $(propertySpan).find(".propName").val();
        properties[name] = $(propertySpan).find(".propValue").val();
	});

	$.ajax({
		type: 'POST',
		url: baseServicePath + "admin/" + image.name + "/properties",
		data: JSON.stringify(properties),
        contentType: "text/json",
		success: function() {
			notify("saved.");
            image.properties = properties;
		}
	});
}

function notify(msg) {
    alert(msg);
}

function loadPageData() {
    loadAllTagsAnd(function(tags) {
        handleLoadTags(tags);
    });
}

function handleLoadTags(tags) {
    tags.forEach(function(tag) {
        handleTag(tag);
    });
}

function handleTag(tag) {
    var tagSection = createTagSection(tag);
    loadImagesForTagAnd(tag, function(images) {
        handleLoadImagesForTag(tagSection, images);
    });
}

function handleLoadImagesForTag(tagSection, images) {
    tagSection.images = [];
    images.forEach(function(image) {
        calculateImageProperties(image);
        tagSection.images.push(image);
        createImageSection(tagSection, image);
    });
}

function createImageSection(tagSection, image) {
    var imageName = $('<span/>');
    imageName.addClass("adminImageName");
    imageName.text(image.name);

    var thumb = $('<img/>');
    thumb.attr("src", image.imageFile.thumbnailSrc);

    var item = $("<div/>");
    item.addClass("adminImageContainer");
    item.append(thumb);
    item.append(imageName);

    appendPropertiesEditor(image, item);

    var imageList = $(tagSection.find(".adminImageList"));
    imageList.append(item);
}

function createTagSection(tag) {
    var section = $('<div/>');
    section.addClass("adminSection");

    var nameField = $("<input/>");
    nameField.addClass("propValue");
    nameField.attr("name", "tag");
    nameField.val(tag);

    var nameSpan = $('<span/>');
    nameSpan.append(nameField);

    var updateTagNameButton = button("rename");
    updateTagNameButton.click(function() {
        renameTag(tag, nameField);
    });

    section.append(nameSpan);
    section.append(updateTagNameButton);

    var imageList = $("<div/>");
    imageList.addClass("adminImageList");
    section.append(imageList);

    appendToContents(section);
    appendToContents("<br/>");
    //tagSections.push(section);

    return section;
}

function renameTag(existingTag, tagField) {
    $.ajax({
        type: 'POST',
        url: baseServicePath + "admin/rename/" + existingTag,
        data: tagField.val(),
        contentType: "text/json",
        success: function() {
            alert("saved.");
        }
    });
}

function button(value) {
    var button = $('<input/>');
    button.attr("type", "button");
    button.val(value);
    return button;
}

function appendToContents(elem) {
    contents.append(elem)
}

$(document).ready(function() {
    contents = $('<div/>');
    contents.addClass("contents");
    $('body').append(contents);

    var reloadTagsLink = button("Reload Tags");
    reloadTagsLink.click(function() {
        $.ajax({
            type: 'GET',
            url: baseServicePath + "admin/reloadTags",
            success: function() {
                notify("Reloaded.");
            }
        });

    });
    contents.append(reloadTagsLink);

    var reloadFilesLink = button("Reload from Files");
    reloadFilesLink.click(function() {
        $.ajax({
            type: 'GET',
            url: baseServicePath + "admin/reloadFromFiles",
            success: function() {
                notify("Reloaded.");
            }
        });

    });
    contents.append(reloadFilesLink);

    loadPageData();
});

var contents;
