package net.manmon.pkg.loader;

public class PkgVersion {
    private Long id;
    private String version;
    private Long verId;

    public PkgVersion() {

    }

    public PkgVersion(Long id, String version, Long verId) {
        this.id = id;
        this.version = version;
        this.verId = verId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getVerId() {
        return verId;
    }

    public void setVerId(Long verId) {
        this.verId = verId;
    }
}