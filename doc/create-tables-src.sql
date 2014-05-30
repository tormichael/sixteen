
--CREATE DATABASE depot
--  WITH OWNER = postgres
--       ENCODING = 'UTF8'
;

CREATE SCHEMA src
  AUTHORIZATION postgres
;

--DROP TABLE src.info;
CREATE TABLE src.info
(
	in_code integer NOT NULL,
	in_name varchar(255),
	in_datey integer,
	in_datem integer,
	in_dated integer,
	in_region varchar(100),
	in_typer integer,
	
	CONSTRAINT info_pkey PRIMARY KEY (in_code)
);

CREATE TABLE src.iopt
(
	io_code integer NOT NULL,
	io_in_code integer NOT NULL,
	io_colname varchar(255),
	io_coltype integer,
	
	CONSTRAINT iopt_pkey PRIMARY KEY (io_code)
);


CREATE TABLE src.ival
(
	iv_code integer NOT NULL,
	iv_in_code integer NOT NULL,
	iv_name varchar(255),
	iv_day char(8), -- format YYYYMMDD 
	iv_place varchar(100),
	iv_doc varchar(255),
	iv_adrreg varchar(255),
	iv_adrfact varchar(255),
	iv_tel varchar(100),
	iv_inn varchar(30),
	iv_typer integer,
	
 	CONSTRAINT ival_pkey PRIMARY KEY (iv_code)
);


CREATE TABLE src.ivo
(
	vo_code integer NOT NULL,
	vo_iv_code integer NOT NULL,
	vo_io_code integer NOT NULL,
	vo_val varchar(255),
	
	CONSTRAINT ivo_pkey PRIMARY KEY (vo_code)
);
