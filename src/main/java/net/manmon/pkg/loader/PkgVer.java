package net.manmon.pkg.loader;

public class PkgVer {
    private Long id;
    private String ver;
    private Long verid;

    public PkgVer() {

    }

    public PkgVer(Long id, String ver, Long verid) {
        this.id = id;
        this.ver = ver;
        this.verid = verid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public Long getVerid() {
        return verid;
    }

    public void setVerid(Long verid) {
        this.verid = verid;
    }
}
