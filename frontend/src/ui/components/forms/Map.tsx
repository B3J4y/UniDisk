import Grid from '@material-ui/core/Grid';
import { Feature, Map, View } from 'ol';
// Start Openlayers imports
import { Point } from 'ol/geom';
import { Heatmap as HeatmapLayer, Tile as TileLayer, Vector as VectorLayer } from 'ol/layer';
import { fromLonLat } from 'ol/proj';
import { OSM as OSMSource, Vector as VectorSource } from 'ol/source';
import { Style, Text } from 'ol/style';
import React from 'react';
import './Map.css';

// End Openlayers imports

export type GpsPosition = {
  lat: number;
  lng: number;
};
export type Marker = {
  id: string;
  score: number;
} & GpsPosition;

export type MapProps = {
  onClick?: (value?: GpsPosition) => void;
  onHover?: (marker: Marker, position: { x: number; y: number }) => void;
  onCreate?: (map: Map) => void;
  onHoverExit?: () => void;
  initialPosition: GpsPosition & { zoom?: number };
  markers?: Marker[];
  height?: number;
};

type State = {
  height?: number;
  markers: Marker[];
};

function max(values: number[]): number {
  var currentMax = -1;
  for (let i = 0; i < values.length; i++) {
    const value = values[i];
    if (value > currentMax) currentMax = value;
  }
  return currentMax;
}

function createHeatmapFeature(position: GpsPosition, score: number): Feature {
  const { lat, lng } = position;

  const feature = new Feature({
    geometry: new Point(fromLonLat([lng, lat])),
  });
  feature.setProperties({ score: score.toString() });
  return feature;
}

const styleFunction = function (feature, resolution) {
  return new Style({
    text: new Text({
      text: feature.get('score'),
    }),
  });
};

export class OLMap extends React.Component<MapProps, State> {
  private map: Map;
  private defaultLayer: VectorLayer;
  private heatmapLayer: HeatmapLayer;

  private readonly toggleMapViewThreshhold = 6;

  private mapId: string;
  constructor(props) {
    super(props);
    this.state = {
      markers: props.markers ?? [],
    };
    this.mapId = (new Date().getTime() * Math.random()).toString();
    this.updateDimensions = this.updateDimensions.bind(this);
  }
  updateDimensions() {
    const h = window.innerWidth >= 992 ? window.innerHeight : 400;
    this.setState({ height: this.props.height ?? h });
  }
  componentWillMount() {
    window.addEventListener('resize', this.updateDimensions);
    this.updateDimensions();
  }

  componentDidUpdate(oldProps: MapProps) {
    const previousMarker = oldProps.markers ?? [];
    const newMarker = this.props.markers ?? [];

    // Simplest solution for now, doesn't detect change if marker data changed
    const markerChanged = previousMarker.length !== newMarker.length;

    if (!markerChanged) return;

    const features = this.buildMarkerFeatures(newMarker);

    [this.heatmapLayer, this.defaultLayer].forEach((layer) => {
      const layerSource = layer.getSource();
      const oldFeatures = layerSource.getFeatures();
      oldFeatures.forEach((feature) => layerSource.removeFeature(feature));
      features.forEach((f) => layerSource.addFeature(f));
    });
  }

  buildMarkerFeatures(marker: Marker[]) {
    const features = marker.map((m) => {
      const score = m.score;
      if (!score) {
        console.log(m);
      }

      const feature = createHeatmapFeature(m, score);
      return feature;
    });

    return features;
  }

  componentDidMount() {
    const { initialPosition, onCreate } = this.props;
    const { lng, lat } = initialPosition;

    try {
      const mapView = new View({
        center: fromLonLat([lng, lat]),
        zoom: initialPosition.zoom ?? 17.2,
      });
      const map = new Map({
        target: this.mapId,
        layers: [
          new TileLayer({
            source: new OSMSource(),
          }),
        ],
        view: mapView,
      });
      this.map = map;

      const marker = this.props.markers ?? [];

      const scores = marker.map((m) => m.score);
      const maxScore = max(scores);

      const features = this.buildMarkerFeatures(marker);

      const vectorSource = new VectorSource({
        features: features,
      });

      this.defaultLayer = new VectorLayer({
        source: vectorSource,
        style: styleFunction,
      });

      this.heatmapLayer = new HeatmapLayer({
        source: vectorSource,
        blur: 12,
        radius: 15,
        weight: function (feature) {
          //value in range [0,1]
          return parseInt(feature.get('score')) / maxScore;
        },
      });

      map.addLayer(this.defaultLayer);
      map.addLayer(this.heatmapLayer);

      this.heatmapLayer.setVisible(false);

      // [defaultLayer].forEach((layer) => {
      //   const layerSource = layer.getSource();
      //   features.forEach((f) => layerSource.addFeature(f));
      // });

      map.on('moveend', (e) => {
        var newZoom = map.getView().getZoom();

        if (newZoom <= this.toggleMapViewThreshhold) {
          this.heatmapLayer.setVisible(false);
          this.defaultLayer.setVisible(true);
        } else {
          this.heatmapLayer.setVisible(true);
          this.defaultLayer.setVisible(false);
        }
        console.log('zoom end, new zoom: ' + newZoom);
      });

      if (onCreate) {
        onCreate(map);
      }
    } catch (error) {
      console.error(error);
    }
  }
  componentWillUnmount() {
    window.removeEventListener('resize', this.updateDimensions);
  }
  render() {
    const style = {
      width: '100%',
      height: this.state.height,
      backgroundColor: '#cccccc',
    };
    return (
      <Grid container>
        <Grid item xs={12}>
          <div id={this.mapId} style={style}></div>
        </Grid>
      </Grid>
    );
  }
}
