import Grid from '@material-ui/core/Grid';
import { Map } from 'ol';
import { Vector as VectorLayer } from 'ol/layer';
import React from 'react';
import { MapProps, OLMap } from './Map';

export type LocationSelectionProps = {
  onChange: (value?: GpsPosition) => void;
  initialPosition?: MapProps['initialPosition'];
  initialCameraPosition?: GpsPosition;
  value?: GpsPosition;
  height?: number;
};

export type GpsPosition = {
  lat: number;
  lng: number;
};

type State = {
  position?: GpsPosition;
};
export class LocationSelection extends React.Component<LocationSelectionProps, State> {
  private map: Map;
  private vectorLayer: VectorLayer;

  constructor(props) {
    super(props);
    this.state = {
      position: this.props.initialPosition,
    };
  }

  componentDidUpdate(prevProps: LocationSelectionProps) {
    if (prevProps.initialPosition !== this.props.initialPosition) {
      this.setState({
        ...this.state,
        position: this.props.initialPosition,
      });
    }
  }

  render() {
    const position = this.state.position;

    const markers = position ? [{ lng: position.lng, lat: position.lat }] : [];
    return (
      <Grid container>
        <Grid item xs={12}>
          <OLMap
            height={this.props.height}
            initialPosition={this.props.initialPosition ?? this.props.initialCameraPosition!}
            onClick={(p) => {
              const position = p ? { ...p } : undefined;
              this.setState({
                ...this.state,
                position,
              });
              this.props.onChange(position);
            }}
            onMarkerSelected={(p) => {}}
            markers={markers}
          />
        </Grid>
      </Grid>
    );
  }
}
