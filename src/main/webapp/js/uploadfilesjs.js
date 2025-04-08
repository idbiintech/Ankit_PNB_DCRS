var Upload = function(url) {
	this.url = url;

};

Upload.prototype.getType = function() {
	return this.file.type;
};
Upload.prototype.getSize = function() {
	return this.file.size;
};
Upload.prototype.getName = function() {
	return this.file.name;
};


Upload.prototype.doUpload = function(file, csrf) {
	debugger;
	var that = this;
	var formData = new FormData();

	var filename = document.getElementById("filename").value;
	var fileDate = document.getElementById("datepicker").value;
	// add assoc key values, this will be posts values
	debugger;
	formData.append("files", file, file.name);
	formData.append('filename', filename);
	formData.append('fileDate', fileDate);
	formData.append("upload_file", true);
	formData.append("CSRFToken", csrf);
	return $.ajax({
		type: "POST",
		url: this.url,
		enctype: "multipart/form-data",
		async: true,
		data: formData,
		cache: false,
		contentType: false,
		processData: false
	});
};

Upload.prototype.progressHandling = function(event) {
	var percent = 0;
	var position = event.loaded || event.position;
	var total = event.total;
	if (event.lengthComputable) {
		percent = Math.ceil(position / total * 100);
	}
	move(percent);
};



function move(by) {
	var elem = document.getElementById("myBar");
	var width = 1;
	var id = setInterval(frame, 10);
	function frame() {
		if (width >= by) {
			clearInterval(id);
		} else {
			width++;
			elem.style.width = width + '%';
		}
	}
}


function getSubCategory(e)
{
	debugger;
	
	
	var fileType = document.getElementById("filename").value
	console.log('filetype ',fileType);
	
	
	document.getElementById("fileCategory").style.display="none";
	if(fileType=="CBS"){
	document.getElementById("fileCategory").style.display="";
	}
}