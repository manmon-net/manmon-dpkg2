CREATE SEQUENCE hibernate_sequence;

CREATE TABLE pkg_ver(
  id BIGINT PRIMARY KEY,
  ver VARCHAR(255) NOT NULL UNIQUE,
  verid BIGINT
);
CREATE INDEX pkg_ver_verid_idx ON pkg_ver(verid);

CREATE TABLE pkg_name(
  id BIGINT PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE pkg (
  id BIGINT PRIMARY KEY,
  pkg_type VARCHAR(16) NOT NULL DEFAULT 'RPM',
  name VARCHAR(384) NOT NULL,
  arch VARCHAR(32) NOT NULL,
  ver_id BIGINT NOT NULL REFERENCES pkg_ver(id),
  version VARCHAR(255) NOT NULL,
  sha256sum VARCHAR(65) NOT NULL,
  name_id BIGINT NOT NULL REFERENCES pkg_name(id),
  installed_size BIGINT NOT NULL,
  archive_size BIGINT NOT NULL,
  pkg_size BIGINT NOT NULL,
  local BOOLEAN NOT NULL
  --source VARCHAR(512) NOT NULL,
  --maintainer VARCHAR(512) NOT NULL,
  --section VARCHAR(255) NOT NULL,
  --priority VARCHAR(128) NOT NULL
);
CREATE UNIQUE INDEX pkg_uniq_idx ON pkg(pkg_type,name,version,arch,installed_size,archive_size,pkg_size,local, sha256sum);
--CREATE INDEX pkg_sha256sum_idx ON pkg(sha256sum);

CREATE TABLE pkg_description (
  id BIGINT PRIMARY KEY REFERENCES pkg(id),
  description TEXT NOT NULL
);

CREATE TABLE pkg_relation (
    id bigint PRIMARY KEY,
    pre BOOLEAN DEFAULT FALSE,
    flags character varying(32),
    name_id bigint NOT NULL REFERENCES pkg_name(id),
    pkg_ver_id bigint REFERENCES pkg_ver(id)
);
CREATE UNIQUE INDEX pkg_relation_uniq_idx ON pkg_relation USING btree (pre, flags, name_id, pkg_ver_id);

CREATE TABLE pkg_conflicts (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_relation(id)
);
CREATE UNIQUE INDEX pkg_conflicts_uniq_idx ON pkg_conflicts USING btree (pkg_id, relation_id);

CREATE TABLE pkg_eq_relation (
    relation_id bigint PRIMARY KEY,
    relation character varying(512)
);
CREATE UNIQUE INDEX pkg_eq_relation_uniq_idx ON pkg_eq_relation USING btree (relation);

CREATE TABLE pkg_eq_conflicts (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_eq_relation(relation_id)
);
CREATE UNIQUE INDEX pkg_eq_conflicts_uniq_idx ON pkg_eq_conflicts USING btree (pkg_id, relation_id);

CREATE TABLE pkg_eq_relation_with_ver (
    relation_id bigint PRIMARY KEY,
    relation character varying(512),
    pre boolean not null default false,
    ver character varying(384)
);
CREATE INDEX pkg_eq_relation_with_ver_uniq_id ON pkg_eq_relation_with_ver USING btree (pre,relation, ver);


CREATE TABLE pkg_eq_conflicts_with_ver (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL  REFERENCES pkg_eq_relation_with_ver(relation_id)
);
CREATE UNIQUE INDEX pkg_eq_conflicts_with_ver_uniq_idx ON pkg_eq_conflicts_with_ver USING btree (pkg_id, relation_id);

CREATE TABLE pkg_eq_replaces (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_eq_relation(relation_id)
);
CREATE UNIQUE INDEX pkg_eq_replaces_uniq_idx ON pkg_eq_replaces USING btree (pkg_id, relation_id);


CREATE TABLE pkg_eq_replaces_with_ver (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL  REFERENCES pkg_eq_relation_with_ver(relation_id)
);
CREATE UNIQUE INDEX pkg_eq_replaces_with_ver_uniq_idx ON pkg_eq_replaces_with_ver USING btree (pkg_id, relation_id);

CREATE TABLE pkg_eq_provides (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_eq_relation(relation_id)
);
CREATE UNIQUE INDEX pkg_eq_provides_uniq_idx ON pkg_eq_provides USING btree (pkg_id, relation_id);

CREATE TABLE pkg_eq_provides_with_ver (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_eq_relation_with_ver(relation_id)
);
CREATE UNIQUE INDEX pkg_eq_provides_with_ver_uniq_idx ON pkg_eq_provides_with_ver USING btree (pkg_id, relation_id);

CREATE TABLE pkg_eq_requires (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_eq_relation(relation_id)
);
CREATE UNIQUE INDEX pkg_eq_requires_uniq_idx ON pkg_eq_requires USING btree (pkg_id, relation_id);

CREATE TABLE pkg_eq_requires_with_ver (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_eq_relation_with_ver(relation_id)
);
CREATE UNIQUE INDEX pkg_eq_requires_with_ver_uniq_idx ON pkg_eq_requires_with_ver USING btree (pkg_id, relation_id);

CREATE TABLE pkg_replaces (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_relation(id)
);
CREATE UNIQUE INDEX pkg_replaces_uniq_idx ON pkg_replaces USING btree (pkg_id, relation_id);

CREATE TABLE pkg_provides (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_relation(id)
);
CREATE UNIQUE INDEX pkg_provides_uniq_idx ON pkg_provides USING btree (pkg_id, relation_id);

CREATE TABLE pkg_requires (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_relation(id)
);
CREATE UNIQUE INDEX pkg_requires_uniq_idx ON pkg_requires USING btree (pkg_id, relation_id);


CREATE TABLE pkg_or_require(
    id BIGINT PRIMARY KEY
);

CREATE TABLE pkg_or_require_pkgs(
    or_require_id BIGINT NOT NULL REFERENCES pkg_or_require(id),
    pkg_id bigint NOT NULL REFERENCES pkg(id)
);
CREATE UNIQUE INDEX pkg_or_require_pkgs_uniq_idx ON pkg_or_require_pkgs(or_require_id, pkg_id);

CREATE TABLE pkg_or_requires (
    or_require_id BIGINT NOT NULL REFERENCES pkg_or_require(id),
    or_relation_id bigint NOT NULL REFERENCES pkg_relation(id)
);
CREATE UNIQUE INDEX pkg_or_requires_uniq_idx ON pkg_or_requires USING btree (or_require_id, or_relation_id);


CREATE TABLE pkg_or_eq_requires_with_ver (
    or_relation_id bigint NOT NULL REFERENCES pkg_or_require(id),
    relation_id bigint NOT NULL REFERENCES pkg_eq_relation_with_ver(relation_id)
);
CREATE UNIQUE INDEX pkg_or_eq_requires_with_ver_uniq_idx ON pkg_or_eq_requires_with_ver USING btree (or_relation_id, relation_id);

CREATE TABLE pkg_or_eq_requires (
    or_relation_id bigint NOT NULL REFERENCES pkg_or_require(id),
    relation_id bigint NOT NULL REFERENCES pkg_eq_relation(relation_id)
);
CREATE UNIQUE INDEX pkg_or_eq_requires_uniq_idx ON pkg_or_eq_requires USING btree (or_relation_id, relation_id);




CREATE TABLE pkg_eq_breaks (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_eq_relation(relation_id)
);
CREATE UNIQUE INDEX pkg_eq_breaks_uniq_idx ON pkg_eq_breaks USING btree (pkg_id, relation_id);


CREATE TABLE pkg_eq_breaks_with_ver (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_eq_relation_with_ver(relation_id)
);
CREATE UNIQUE INDEX pkg_eq_breaks_with_ver_uniq_idx ON pkg_eq_breaks_with_ver USING btree (pkg_id, relation_id);

CREATE TABLE pkg_breaks (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_relation(id)
);
CREATE UNIQUE INDEX pkg_breaks_uniq_idx ON pkg_breaks USING btree (pkg_id, relation_id);