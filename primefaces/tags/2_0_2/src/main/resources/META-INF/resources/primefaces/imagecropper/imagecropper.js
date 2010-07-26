PrimeFaces.widget.ImageCropperUtils = {
		
	attachedCroppedArea : function(event, obj) {
		var cropArea = this.getCropCoords();
		var cropCoords = cropArea.top + "_" + cropArea.left + "_" + cropArea.width + "_" + cropArea.height;

		document.getElementById(obj.hiddenFieldId).value = cropCoords;
	}
};