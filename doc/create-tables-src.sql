
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

-- DROP TABLE src.iopt;
CREATE TABLE src.iopt
(
	io_code integer NOT NULL,
	io_owner integer NOT NULL,
	io_in_code integer NOT NULL,
	io_colname varchar(255),
	io_coltyper integer,
	io_colbaser integer,
	
	CONSTRAINT iopt_pkey PRIMARY KEY (io_code)
);


-- DROP TABLE src.ival;
CREATE TABLE src.ival
(
	iv_code integer NOT NULL,
	iv_in_code integer NOT NULL,
	iv_name varchar(255),
	iv_day char(8), -- format YYYYMMDD 
	iv_place varchar(100),
	iv_inn varchar(30),
	iv_typer integer,
	
 	CONSTRAINT ival_pkey PRIMARY KEY (iv_code)
);


-- DROP TABLE src.ivo;
CREATE TABLE src.ivo
(
	vo_code integer NOT NULL,
	vo_iv_code integer NOT NULL,
	vo_io_code integer NOT NULL,
	vo_val varchar(2000),
	
	CONSTRAINT ivo_pkey PRIMARY KEY (vo_code)
);

-- DROP TABLE src.refbook;
CREATE TABLE src.refbook
(
	rb_code integer NOT NULL,
	rb_owner integer NOT NULL,
	rb_path varchar(255),
	rb_name varchar(1000),
	rb_val varchar(255),
	
	CONSTRAINT rb_pkey PRIMARY KEY (rb_code)
);

