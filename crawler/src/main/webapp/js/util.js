const htmlToImage = require("html-to-image");

document.getElementById("export-png").addEventListener("click", function() {
  var exportOptions = {
    filter: function(element) {
      return element.className
        ? element.className.indexOf("ol-control") === -1
        : true;
    }
  };

  map.once("rendercomplete", function() {
    htmlToImage
      .toPng(map.getTargetElement(), exportOptions)
      .then(function(dataURL) {
        var link = document.getElementById("image-download");
        link.href = dataURL;
        link.click();
      });
  });
  map.renderSync();
});


