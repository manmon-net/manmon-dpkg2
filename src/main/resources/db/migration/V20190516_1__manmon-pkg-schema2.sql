CREATE TABLE channel (
    id bigint PRIMARY KEY,
    arch character varying(255) NOT NULL,
    disabled boolean NOT NULL DEFAULT false,
    name character varying(255) NOT NULL,
    org_id bigint,
    public_channel boolean NOT NULL,
    show_to_child_orgs boolean
);
CREATE UNIQUE INDEX channel_name_arch_org_uniq_idx ON channel USING btree (name, arch, org_id);

CREATE TABLE upstream (
    id bigint PRIMARY KEY,
    arch character varying(255),
    type VARCHAR(32) NOT NULL,
    download_running boolean DEFAULT false NOT NULL,
    name character varying(255),
    need_downloading boolean DEFAULT false NOT NULL,
    updates boolean DEFAULT false NOT NULL,
    url character varying(1024) NOT NULL,
    filename VARCHAR(512) NOT NULL,
    channel_id bigint NOT NULL REFERENCES channel(id),
    loaded_checksum character varying(255),
    disabled BOOLEAN DEFAULT false
);
CREATE UNIQUE INDEX upstream_name_arch_channel_uniq_idx ON upstream USING btree (name, arch, channel_id);
