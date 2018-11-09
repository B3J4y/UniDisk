package de.unidisk.rest;

public class LegacyRequestData {
    private LoadPurpose loadPurpose;
    public LegacyRequestData() {
    }

    public LegacyRequestData(LoadPurpose loadPurpose) {
        this.loadPurpose = loadPurpose;
    }

    public LoadPurpose getLoadPurpose() {
        return loadPurpose;
    }

    public void setLoadPurpose(LoadPurpose loadPurpose) {
        this.loadPurpose = loadPurpose;
    }
}