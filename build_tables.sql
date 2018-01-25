DROP TABLE IF EXISTS coininfo CASCADE;

CREATE TABLE coininfo
(
   id        integer        DEFAULT "identity"(100241, 0, '1,1'::text) NOT NULL,
   name      varchar(256)   NOT NULL,
   symbol    varchar(256)   NOT NULL,
   coinname  varchar(256)   NOT NULL,
   fullname  varchar(256)   NOT NULL
);

DROP TABLE IF EXISTS datatable CASCADE;

CREATE TABLE datatable
(
   id          integer   DEFAULT "identity"(100267, 0, '1,1'::text) NOT NULL,
   closevalue  float8,
   highvalue   float8,
   lowvalue    float8,
   openvalue   float8,
   volumefrom  float8,
   volumeto    float8,
   unixtime    integer,
   coinid      integer
);

DROP TABLE IF EXISTS modeltype CASCADE;

CREATE TABLE modeltype
(
   id    integer        DEFAULT "identity"(100244, 0, '1,1'::text) NOT NULL,
   name  varchar(100)
);

ALTER TABLE modeltype
   ADD CONSTRAINT modeltype_pkey
   PRIMARY KEY (id);

DROP TABLE IF EXISTS predictions CASCADE;

CREATE TABLE predictions
(
   id                integer        DEFAULT "identity"(100296, 0, '1,1'::text) NOT NULL,
   requestdate       varchar(256)   NOT NULL,
   amznrequestid     varchar(256)   NOT NULL,
   highvaluepredict  float8         NOT NULL,
   coinid            integer        NOT NULL,
   unixtime          bigint         DEFAULT 0 NOT NULL,
   highvalueactual   float8,
   percenterror      numeric(5,2),
   modeltypeid       integer,
   awsmlmodelid      varchar(100)
);

ALTER TABLE predictions
   ADD CONSTRAINT predictions_pkey
   PRIMARY KEY (id);
