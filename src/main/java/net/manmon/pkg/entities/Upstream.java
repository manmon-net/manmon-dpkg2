package net.manmon.pkg.entities;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Upstream {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String arch;
    private boolean updates;
    private boolean downloadRunning;
    private Long channelId;
    private boolean needDownloading;
    private String url;
    private String loadedChecksum;
    private String type;
    private String filename;
    private boolean disabled = false;

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public boolean isUpdates() {
        return updates;
    }

    public void setUpdates(boolean updates) {
        this.updates = updates;
    }

    public boolean isDownloadRunning() {
        return downloadRunning;
    }

    public void setDownloadRunning(boolean downloadRunning) {
        this.downloadRunning = downloadRunning;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public boolean isNeedDownloading() {
        return needDownloading;
    }

    public void setNeedDownloading(boolean needDownloading) {
        this.needDownloading = needDownloading;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLoadedChecksum() {
        return loadedChecksum;
    }

    public void setLoadedChecksum(String loadedChecksum) {
        this.loadedChecksum = loadedChecksum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
