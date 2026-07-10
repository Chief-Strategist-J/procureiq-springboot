package com.procureiq.springboot_app.features.fieldservice.entity;

import java.io.Serializable;
import java.util.Objects;

public class TravelTimeCacheId implements Serializable {
    private Long originTerritory;
    private Integer destLatBucket;
    private Integer destLngBucket;

    public TravelTimeCacheId() {}

    public TravelTimeCacheId(Long originTerritory, Integer destLatBucket, Integer destLngBucket) {
        this.originTerritory = originTerritory;
        this.destLatBucket = destLatBucket;
        this.destLngBucket = destLngBucket;
    }

    public Long getOriginTerritory() { return originTerritory; }
    public void setOriginTerritory(Long originTerritory) { this.originTerritory = originTerritory; }

    public Integer getDestLatBucket() { return destLatBucket; }
    public void setDestLatBucket(Integer destLatBucket) { this.destLatBucket = destLatBucket; }

    public Integer getDestLngBucket() { return destLngBucket; }
    public void setDestLngBucket(Integer destLngBucket) { this.destLngBucket = destLngBucket; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelTimeCacheId that = (TravelTimeCacheId) o;
        return Objects.equals(originTerritory, that.originTerritory) &&
               Objects.equals(destLatBucket, that.destLatBucket) &&
               Objects.equals(destLngBucket, that.destLngBucket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originTerritory, destLatBucket, destLngBucket);
    }
}
