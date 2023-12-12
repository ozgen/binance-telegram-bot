package com.ozgen.telegrambinancebot.model.binance;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "snapshot_data")
public class SnapshotData {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private int code;
    private String msg;

    @OneToMany(mappedBy = "snapshotData", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SnapshotVo> snapshotVos;

    public UUID getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<SnapshotVo> getSnapshotVos() {
        return snapshotVos;
    }

    public void setSnapshotVos(List<SnapshotVo> snapshotVos) {
        this.snapshotVos = snapshotVos;
    }

    @Override
    public String toString() {
        return "Snapshot{" +
                "id=" + id +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                ", snapshotVos=" + snapshotVos +
                '}';
    }

    public Double getCoinValue(String assetName) {
        if (this == null || this.getSnapshotVos() == null) {
            return 0.0;
        }

        for (SnapshotData.SnapshotVo snapshotVo : this.getSnapshotVos()) {
            SnapshotData.Data data = snapshotVo.getData();
            if (data != null && data.getBalances() != null) {
                for (SnapshotData.Balance balance : data.getBalances()) {
                    if (assetName.equals(balance.getAsset())) {
                        try {
                            return Double.parseDouble(balance.getFree());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            // Handle parse error
                        }
                    }
                }
            }
        }
        return 0.0;
    }

    @Entity
    @Table(name = "snapshot_vo")
    public static class SnapshotVo {

        @Id
        @GeneratedValue(generator = "UUID")
        private UUID id;

        private String type;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
        private Date updateTime;

        @ManyToOne
        @JoinColumn(name = "snapshot_data_id")
        private SnapshotData snapshotData;

        @OneToOne(mappedBy = "snapshotVo", cascade = CascadeType.ALL, orphanRemoval = true)
        private Data data;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

        public SnapshotData getSnapshot() {
            return snapshotData;
        }

        public void setSnapshot(SnapshotData snapshotData) {
            this.snapshotData = snapshotData;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "SnapshotVo{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", updateTime=" + updateTime +
                    ", snapshot=" + snapshotData +
                    ", data=" + data +
                    '}';
        }
    }

    @Entity
    @Table(name = "data")
    public static class Data {
        @Id
        @GeneratedValue(generator = "UUID")
        private UUID id;

        private String totalAssetOfBtc;

        @OneToOne
        @JoinColumn(name = "snapshot_vo_id")
        private SnapshotVo snapshotVo;

        @OneToMany(mappedBy = "data", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Balance> balances;

        public UUID getId() {
            return id;
        }

        public String getTotalAssetOfBtc() {
            return totalAssetOfBtc;
        }

        public void setTotalAssetOfBtc(String totalAssetOfBtc) {
            this.totalAssetOfBtc = totalAssetOfBtc;
        }

        public SnapshotVo getSnapshotVo() {
            return snapshotVo;
        }

        public void setSnapshotVo(SnapshotVo snapshotVo) {
            this.snapshotVo = snapshotVo;
        }

        public List<Balance> getBalances() {
            return balances;
        }

        public void setBalances(List<Balance> balances) {
            this.balances = balances;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "id=" + id +
                    ", totalAssetOfBtc='" + totalAssetOfBtc + '\'' +
                    ", snapshotVo=" + snapshotVo +
                    ", balances=" + balances +
                    '}';
        }
    }

    @Entity
    @Table(name = "balance")
    public static class Balance {
        @Id
        @GeneratedValue(generator = "UUID")
        private UUID id;

        private String asset;
        private String free;
        private String locked;

        @ManyToOne
        @JoinColumn(name = "data_id")
        private Data data;

        public UUID getId() {
            return id;
        }

        public String getAsset() {
            return asset;
        }

        public void setAsset(String asset) {
            this.asset = asset;
        }

        public String getFree() {
            return free;
        }

        public void setFree(String free) {
            this.free = free;
        }

        public String getLocked() {
            return locked;
        }

        public void setLocked(String locked) {
            this.locked = locked;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Balance{" +
                    "id=" + id +
                    ", asset='" + asset + '\'' +
                    ", free='" + free + '\'' +
                    ", locked='" + locked + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
}

