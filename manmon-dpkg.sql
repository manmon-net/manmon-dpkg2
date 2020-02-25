CREATE TABLE pkg_ver(
  id BIGINT PRIMARY KEY,
  ver VARCHAR(255) NOT NULL UNIQUE,
  rpm_verid BIGINT,
  dpkg_verid BIGINT
);

CREATE TABLE pkg_name(
  id BIGINT PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE pkg (
  id BIGINT PRIMARY KEY,
  pkg_type VARCHAR(16) NOT NULL DEFAULT 'rpm',
  name VARCHAR(384) NOT NULL,
  arch VARCHAR(32) NOT NULL,
  ver_id BIGINT NOT NULL REFERENCES pkg_ver(id),

  sha256sum VARCHAR(65) NOT NULL,
  name_id BIGINT NOT NULL REFERENCES pkg_name(id),
  installed_size BIGINT NOT NULL,
  archive_size BIGINT NOT NULL,
  pkg_size BIGINT NOT NULL,
  local BOOLEAN NOT NULL.
  --source VARCHAR(512) NOT NULL,
  --maintainer VARCHAR(512) NOT NULL,
  --section VARCHAR(255) NOT NULL,
  --priority VARCHAR(128) NOT NULL
);
CREATE UNIQUE INDEX pkg_uniq_idx ON pkg(package_type,name,ver_id,arch,installed_size.archive_size,pkg_size,local, sha256sum);

CREATE TABLE pkg_description (
  id BIGINT PRIMARY KEY REFERENCES pkg(id),
  description TEXT NOT NULL
);

CREATE TABLE pkg_relation (
    id bigint PRIMARY KEY,
    pre ITN NOT NULL DEFAULT 0,
    flags character varying(32),
    name_id bigint NOT NULL REFERENCES pkg_name(id),
    pkg_ver_id bigint REFERENCES pkg_ver(id)
);
CREATE UNIQUE INDEX pkg_relation_uniq_idx ON pkg_relation USING btree (pre, flags, name_id, pkg_id);

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
    ver character varying(384)
);
CREATE INDEX pkg_eq_relation_with_ver_uniq_id ON pkg_eq_relation_with_ver USING btree (relation, ver);


CREATE TABLE pkg_eq_conflicts_with_ver (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL  REFERENCES pkg_eq_relation_with_ver(relation_id)
);
CREATE UNIQUE INDEX pkg_eq_conflicts_with_ver_uniq_idx ON pkg_eq_conflicts_with_ver USING btree (pkg_id, relation_id);

CREATE TABLE pkg_eq_obsoletes (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_eq_relation(relation_id)
);
CREATE UNIQUE INDEX pkg_eq_obsoletes_uniq_idx ON pkg_eq_obsoletes USING btree (pkg_id, relation_id);


CREATE TABLE pkg_eq_obsoletes_with_ver (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL  REFERENCES pkg_eq_relation_with_ver(relation_id)
);
CREATE UNIQUE INDEX pkg_eq_obsoletes_with_ver_uniq_idx ON pkg_eq_obsoletes_with_ver USING btree (pkg_id, relation_id);

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

CREATE TABLE pkg_obsoletes (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    relation_id bigint NOT NULL REFERENCES pkg_relation(id)
);
CREATE UNIQUE INDEX pkg_obsoletes_uniq_idx ON pkg_obsoletes USING btree (pkg_id, relation_id);

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


CREATE TABLE pkg_or_requires (
    pkg_id bigint NOT NULL REFERENCES pkg(id),
    or_relation_id bigint NOT NULL REFERENCES pkg_relation(id),
);
CREATE UNIQUE INDEX pkg_or_requires_uniq_idx ON pkg_or_requires USING btree (pkg_id, or_relation_id);