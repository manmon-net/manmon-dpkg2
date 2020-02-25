
CREATE TABLE channel_set (
    id bigint PRIMARY KEY
);

CREATE TABLE channel_set_channel (
    channel_id BIGINT NOT NULL REFERENCES channel(id),
    channel_set_id bigint NOT NULL REFERENCES channel_set(id)
);
CREATE UNIQUE INDEX channel_set_channel_uniq_idx ON channel_set_channel USING btree (channel_id, channel_set_id);


CREATE TABLE dbid (
    id bigint PRIMARY KEY
);

CREATE TABLE distribution (
    id bigint PRIMARY KEY,
    name character varying(255) NOT NULL,
    version character varying(128) NOT NULL
);
CREATE UNIQUE INDEX distribution_uniq_idx ON distribution USING btree (name, version);


CREATE TABLE pkg_change_set (
    id bigint PRIMARY KEY,
    checksum character varying(255),
    checksum_name character varying(255),
    pkgcount bigint
);
CREATE UNIQUE INDEX pkg_change_set_uniq_idx ON pkg_change_set USING btree (checksum, checksum_name);

CREATE TABLE pkg_set (
    id bigint PRIMARY KEY,
    checksum character varying(255),
    pkg_buildtime_checksum character varying(255),
    pkgcount bigint
);


CREATE TABLE host_pkg_info (
    id bigint PRIMARY KEY,
    needing_check boolean NOT NULL,
    pkg_set_installed_id bigint NOT NULL REFERENCES pkg_set(id),
    pkg_change_set_id bigint REFERENCES pkg_change_set(id),
    channel_set_id bigint NOT NULL REFERENCES channel_set(id)
);

CREATE TABLE hostgroup (
    id bigint PRIMARY KEY,
    org_id bigint NOT NULL,
    authkey character varying(255)
);


CREATE TABLE pkg_set_installed (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    pkg_set_id bigint NOT NULL REFERENCES pkg_set(id)
);
CREATE UNIQUE INDEX pkg_set_installed_uniq_idx ON pkg_set_installed USING btree (pkg_id, pkg_set_id);


CREATE TABLE pkg_set_update (
    id bigint PRIMARY KEY,
    install_instead_of_update boolean DEFAULT false NOT NULL,
    severity integer,
    pkg_change_set_id bigint NOT NULL REFERENCES pkg_change_set(id),
    pkg_id bigint NOT NULL REFERENCES pkg(id)
);


CREATE TABLE pkg_upstream (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    upstream_id bigint NOT NULL REFERENCES upstream(id)
);
CREATE UNIQUE INDEX pkg_upstream_uniq_idx ON pkg_upstream USING btree (pkg_id, upstream_id);


CREATE TABLE roothost (
    id bigint PRIMARY KEY,
    org_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    hostname character varying(384) NOT NULL,
    hostgroup_id bigint NOT NULL REFERENCES hostgroup(id),
    authkey character varying(255) UNIQUE,
    host_pkg_info_id bigint REFERENCES host_pkg_info(id)
);
