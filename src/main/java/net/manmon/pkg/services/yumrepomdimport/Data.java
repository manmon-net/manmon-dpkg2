package net.manmon.pkg.services.yumrepomdimport;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement(namespace = "http://linux.duke.edu/metadata/repo", name="data")
@XmlAccessorType(XmlAccessType.FIELD)
public class Data implements Serializable
{
	private static final long serialVersionUID = 1L;

	@XmlElement(namespace = "http://linux.duke.edu/metadata/repo")
	private String timestamp;

    @XmlElement(namespace = "http://linux.duke.edu/metadata/repo")
    private Location location;

    @XmlElement(namespace = "http://linux.duke.edu/metadata/repo")
    private Checksum checksum;
    
    @XmlElement(name="open-checksum", namespace = "http://linux.duke.edu/metadata/repo")
    private Openchecksum openchecksum;
    @XmlAttribute(name="type")
    private String type;

    @XmlElement(name="open-size", namespace = "http://linux.duke.edu/metadata/repo")
    private String opensize;
    
	@XmlElement(namespace = "http://linux.duke.edu/metadata/repo")
    private String size;

    public String getTimestamp ()
    {
        return timestamp;
    }

    public void setTimestamp (String timestamp)
    {
        this.timestamp = timestamp;
    }

    public Location getLocation ()
    {
        return location;
    }

    public void setLocation (Location location)
    {
        this.location = location;
    }

    public Checksum getChecksum ()
    {
        return checksum;
    }

    public void setChecksum (Checksum checksum)
    {
        this.checksum = checksum;
    }

    public Openchecksum getOpenchecksum ()
    {
        return openchecksum;
    }

    public void setOpenchecksum (Openchecksum openchecksum)
    {
        this.openchecksum = openchecksum;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getOpensize ()
    {
        return opensize;
    }

    public void setOpensize (String opensize)
    {
        this.opensize = opensize;
    }

    public String getSize ()
    {
        return size;
    }

    public void setSize (String size)
    {
        this.size = size;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [timestamp = "+timestamp+", location = "+location+", checksum = "+checksum+", open-checksum = "+openchecksum+", type = "+type+", open-size = "+opensize+", size = "+size+"]";
    }
}