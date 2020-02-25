package net.manmon.pkg.loader;

import net.manmon.pkg.entities.Upstream;
import net.manmon.pkg.pojo.RepoPojo;
import net.manmon.pkg.services.utils.LoaderUtils;
import net.manmon.pkg.services.yumrepoimport.*;
import net.manmon.pkg.services.yumrepomdimport.Data;
import net.manmon.pkg.services.yumrepomdimport.Repomd;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

public class YumRepoImporter {
    private static final Logger logger = LoggerFactory.getLogger(YumRepoImporter.class);

    public void loadYumUpstreams() throws Exception {

    }

    private Long getNextId() {
        return null;
    }

    protected HashSet<GenericPackage> metadataToGenericPkgList(Upstream upstream) {
        Metadata metadata = null;
        try {
            metadata = loadMetaData(upstream, upstream.getUrl(), true, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashSet<GenericPackage> pkgList = new HashSet<>();
        for (RpmPackage rpm : metadata.getRpmPackages()) {
            GenericPackage pkg = new GenericPackage();
            pkg.setArch(rpm.getArch());
            pkg.setArchiveSize(Long.valueOf(rpm.getSize().getArchive()));
            pkg.setInstalledSize(Long.valueOf(rpm.getSize().getInstalled()));
            pkg.setLocal(false);
            pkg.setName(rpm.getName());
            pkg.setPkgType("RPM");
            pkg.setPkgSize(Long.valueOf(rpm.getSize().getPkg()));
            if (rpm.getChecksum() != null && rpm.getChecksum().getType() != null && rpm.getChecksum().getType().toUpperCase().equals("SHA256")) {
                pkg.setSha256hash(rpm.getChecksum().getChecksum());
            }
            pkg.setVersion(rpmVerToString(rpm.getVersion()));

            if (rpm.getFormat() != null && rpm.getFormat().getRequires() != null) {
                for (RpmEntry entry : rpm.getFormat().getRequires().getEntries()) {
                    pkg.getRequiresRelations().add(rpmEntryToGenericRelation(entry));
                }
            }
            if (rpm.getFormat() != null && rpm.getFormat().getConflicts() != null) {
                for (RpmEntry entry : rpm.getFormat().getConflicts().getEntries()) {
                    pkg.getConflictsRelations().add(rpmEntryToGenericRelation(entry));
                }
            }
            if (rpm.getFormat() != null && rpm.getFormat().getObsoletes() != null) {
                for (RpmEntry entry : rpm.getFormat().getObsoletes().getEntries()) {
                    pkg.getReplacesRelations().add(rpmEntryToGenericRelation(entry));
                }
            }
            if (rpm.getFormat() != null && rpm.getFormat().getProvides() != null) {
                for (RpmEntry entry : rpm.getFormat().getProvides().getEntries()) {
                    pkg.getProvidesRelations().add(rpmEntryToGenericRelation(entry));
                }
            }
            pkgList.add(pkg);
        }
        return pkgList;
    }


    private GenericRelation rpmEntryToGenericRelation(RpmEntry entry) {
        GenericRelation relation = new GenericRelation();
        String verStr = rpmVerToString(entry.getEpoch(), entry.getVer(), entry.getRel());
        if (verStr != null) {
            relation.setVersion(verStr);
        }
        relation.setName(entry.getName());
        relation.setRelation(entry.getFlags());
        if (entry.getPre() != null && entry.getPre().equals("1")) {
            relation.setPre(true);
        }
        return relation;
    }


    private String rpmVerToString(String epoch, String ver, String rel) {
        String verStr = "";
        if (epoch != null && !epoch.equals("0")) {
            verStr = epoch+":";
        }
        if (ver != null && ver.length()>0) {
            verStr = verStr + ver;
        } else {
            return null;
        }
        if (rel != null && rel.length()>0) {
            verStr = verStr + "-" + rel;
        }
        return verStr;
    }

    private String rpmVerToString(Version ver) {
        String verStr = "";
        if (ver.getEpoch() != null && !ver.getEpoch().equals(0)) {
            verStr = ver.getEpoch()+":";
        }
        verStr = verStr + ver.getVer();
        if (ver.getRel() != null && ver.getRel().length()>0) {
            verStr = verStr + "-" + ver.getRel();
        }
        return verStr;
    }

    protected Metadata loadMetaData(Upstream upstream, String repoUrl, boolean download, String loadedChecksum) throws Exception {
        RepoPojo repoPojo = loadUpstreamRepository(upstream.getId(), repoUrl, download, loadedChecksum);
        if (loadedChecksum != null && loadedChecksum.equals(repoPojo.getChecksum())) {
            return null;
        } else {
            JAXBContext jaxbContext = JAXBContext.newInstance(Metadata.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Metadata metadata;
            if (repoPojo.getPrimaryFilename().endsWith(".gz")) {
                metadata = (Metadata) jaxbUnmarshaller.unmarshal(new GZIPInputStream(new FileInputStream((repoPojo.getPrimaryFilename()))));
            } else {
                metadata = (Metadata) jaxbUnmarshaller.unmarshal(new File(repoPojo.getPrimaryFilename()));
            }
            return metadata;
        }
    }



    public RepoPojo loadUpstreamRepository(Long upstreamId, String repoUrl, boolean download, String loadedChecksum) throws Exception {
        String localRepoDir = System.getProperty("user.home") + "/manmon-pkg/metadata/" + upstreamId;

        CloseableHttpAsyncClient httpclient = null;
        if (download) {
            httpclient = HttpAsyncClients.createDefault();
            httpclient.start();
        }

        File localRepoDirFile = new File(localRepoDir+"/repodata");
        if (!localRepoDirFile.exists()) {
            localRepoDirFile.mkdirs();
        }

        boolean downloadRepoMd = false;
        String localRepoMdFilename = localRepoDir + "/repodata/repomd.xml";
        HttpResponse httpResponse = null;
        if (download) {
            HttpHead head = new HttpHead(repoUrl + "/repodata/repomd.xml");
            httpResponse = httpclient.execute(head, null).get();
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception("Error downloading upstream information " + httpResponse.getStatusLine().getStatusCode());
            }
            String lastModified = httpResponse.getLastHeader("last-modified").getValue();
            Date remoteRepoMdLastModifiedDate = DateUtils.parseDate(lastModified);
            File localRepoMdFile = new File(localRepoMdFilename);

            if (localRepoMdFile.exists()) {
                Date localRepoMdLastModifiedDate = new Date(localRepoMdFile.lastModified());
                if (!remoteRepoMdLastModifiedDate.equals(localRepoMdLastModifiedDate)) {
                    localRepoMdFile.delete();
                    downloadRepoMd = true;
                }
            } else {
                downloadRepoMd = true;
            }
            if (downloadRepoMd) {
                logger.info("Upstream id "+upstreamId+" downloading repomd.xml from " + repoUrl + "/repodata/repomd.xml");
                Future<HttpResponse> httpFuture = httpclient.execute(new HttpGet(repoUrl + "/repodata/repomd.xml"), null);
                httpResponse = httpFuture.get();
                Files.copy(httpResponse.getEntity().getContent(), Paths.get(localRepoMdFilename));
                localRepoMdFile.setLastModified(DateUtils.parseDate(lastModified).getTime());
            } else {
                logger.info("Upstream id "+upstreamId+" repomd.xml up to date");
            }
        }


        JAXBContext jaxbContext = JAXBContext.newInstance(Repomd.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Repomd repomd = (Repomd) jaxbUnmarshaller.unmarshal(new File(localRepoMdFilename));
        for (Data data : repomd.getData()) {
            if (data.getType().equals("primary")) {
                String localPrimaryFileName = localRepoDir+"/"+data.getLocation().getHref();

                File localPrimaryFile = new File(localPrimaryFileName);
                httpResponse = httpclient.execute(new HttpHead(repoUrl+"/repodata/repomd.xml"), null).get();
                Date remotePrimaryLastModifiedDate = DateUtils.parseDate(httpResponse.getLastHeader("last-modified").getValue());
                if (!localPrimaryFile.exists() || localPrimaryFile.lastModified() != remotePrimaryLastModifiedDate.getTime() ||  !LoaderUtils.getChecksumForFile(localPrimaryFileName, data.getChecksum().getType()).equals(data.getChecksum().getContent())) {
                    logger.info("Upstream id "+upstreamId+" downloading primary.xml from "+repoUrl+"/"+data.getLocation().getHref());
                    for (File localRepoFile : new File(localRepoDir+"/repodata").listFiles()) {
                        if (!localRepoFile.getName().equals("repomd.xml")) {
                            localRepoFile.delete();
                        }
                    }

                    httpResponse = httpclient.execute(new HttpGet(repoUrl+"/"+data.getLocation().getHref()), null).get();
                    Files.copy(httpResponse.getEntity().getContent(), Paths.get(localPrimaryFileName));
                    localPrimaryFile.setLastModified(remotePrimaryLastModifiedDate.getTime());
                    if (LoaderUtils.getChecksumForFile(localPrimaryFileName, data.getChecksum().getType()).equals(data.getChecksum().getContent())) {
                        logger.debug("Upstream id "+upstreamId+" "+localPrimaryFileName+" checksum valid");
                    } else {
                        throw new Exception(localPrimaryFileName+" checksum not valid "+ LoaderUtils.getChecksumForFile(localPrimaryFileName, data.getChecksum().getType())+" "+data.getChecksum().getContent());
                    }
                } else {
                    logger.debug("Upstream id "+upstreamId+" primary.xml up to date");
                }
                httpclient.close();
                RepoPojo repoPojo = new RepoPojo();
                repoPojo.setChecksum(data.getChecksum().getContent());
                repoPojo.setPrimaryFilename(localPrimaryFileName);
                return repoPojo;
            }
        }
        try {
            httpclient.close();
        } catch (Exception e) {

        }
        return null;
    }


}
