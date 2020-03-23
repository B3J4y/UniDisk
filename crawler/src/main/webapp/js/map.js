  const mapIconScale = .2;
    const initialLatitude = 52.39356;
    const initialLongitude = 13.13044;
    const initialZoom = 18;

    function createIcon(src){
    return new ol.style.Style({
    image: new ol.style.Icon({
    src: src,

    scale: mapIconScale
})
});
}

    function refreshMap(jsonMarker){
    console.log(jsonMarker);
    const marker = jsonMarker;

    const features = marker.map(m => {

    const lat = m.university.lat;
    const lng = m.university.lng;

    const featureIcon = createIcon(m.iconUrl);
    const newMarker = new ol.Feature({
    geometry: new ol.geom.Point(
    ol.proj.fromLonLat([lng, lat])
    )
});
    newMarker.setStyle(featureIcon);
    return newMarker;
});
    console.log(features);

    var vectorSource = new ol.source.Vector({
    features: features
});
    vectorLayer = new ol.layer.Vector({
    source: vectorSource
});

    var oldFeatures = markerVectorLayer.getSource().getFeatures();
    oldFeatures.forEach(feature => {
    markerVectorLayer.getSource().removeFeature(feature);
});


    map.addLayer(vectorLayer);
}

    var map = new ol.Map({
    target: 'map',
    layers: [
    new ol.layer.Tile({
    source: new ol.source.OSM()
})
    ],
    view: new ol.View({
    center: ol.proj.fromLonLat([initialLongitude, initialLatitude]),
    zoom: initialZoom
})
});

    /*var marker = new ol.Feature({
    geometry: new ol.geom.Point(
    ol.proj.fromLonLat([13.13044, 52.39356])
    ),  // Cordinates of New York's Town Hall
});*/
    var vectorSource = new ol.source.Vector({
    features: []
});
    var markerVectorLayer = new ol.layer.Vector({
    source: vectorSource,
});
    map.addLayer(markerVectorLayer);

