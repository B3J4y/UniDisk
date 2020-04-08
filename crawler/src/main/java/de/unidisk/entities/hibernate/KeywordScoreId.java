package de.unidisk.entities.hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class KeywordScoreId implements Serializable {

    private int keywordId;

    private int metadataId;

    public KeywordScoreId() {
    }

    public KeywordScoreId(int keywordId, int metadataId) {
        this.keywordId = keywordId;
        this.metadataId = metadataId;
    }

    public int getKeywordId() {
        return keywordId;
    }

    public int getMetadataId() {
        return metadataId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeywordScoreId)) return false;
        KeywordScoreId that = (KeywordScoreId) o;
        return Objects.equals(getKeywordId(), that.getKeywordId()) &&
                Objects.equals(getMetadataId(), that.getMetadataId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKeywordId(), getMetadataId());
    }

    public void setKeywordId(int keywordId) {
        this.keywordId = keywordId;
    }

    public void setMetadataId(int metadataId) {
        this.metadataId = metadataId;
    }
}