
CREATE DATABASE depot
  WITH OWNER = postgres
       ENCODING = 'UTF8'
;

--DROP TABLE infosrc;
CREATE TABLE srcinfo
(
	si_code integer NOT NULL,
	si_name varchar(255),
	si_datey integer,
	si_datem integer,
	si_dated integer,
	si_region varchar(100),
	si_typer integer,
	si_negdesc varchar(255),
	si_3fdesc varchar(255),
	si_optdesc varchar(255),
	
	CONSTRAINT srcinfo_pkey PRIMARY KEY (si_code)
);

CREATE TABLE aobj
(
	ob_code integer NOT NULL,
	ob_name varchar(100),
	ob_id	varchar(50),
	ob_title varchar(255),
	
	CONSTRAINT aobj_pkey PRIMARY KEY (ob_code)
);

CREATE TABLE aperson
(
	pr_code integer NOT NULL,
	pr_lname varchar(50),
	pr_fname varchar(50),
	pr_pname varchar(50),
	pr_bday char(8),
	pr_bplace varchar(100),
	pr_hash integer,
	
	CONSTRAINT aperson_pkey PRIMARY KEY (pr_code)
);

CREATE TABLE adoc
(
	dc_code integer NOT NULL,
	dc_objp integer,
	dc_typer integer,
	dc_series varchar(10),
	dc_number varchar(20),
	dc_who varchar(255),
	dc_when char(8),
	dc_from char(8),
	dc_hash integer,
	
	CONSTRAINT adoc_pkey PRIMARY KEY (dc_code)
);

CREATE TABLE aaddress
(
	ad_code integer NOT NULL,
	ad_objp integer,
	ad_typer integer,
	ad_index varchar(10),
	ad_country varchar(50),
	ad_region varchar(100),
	ad_city varchar(100),
	ad_place varchar(255),
	ad_from char(8),
	ad_hash integer,
	
	CONSTRAINT aaddress_pkey PRIMARY KEY (ad_code)
);


CREATE TABLE areftab
(
	rf_code integer NOT NULL,
	rf_own integer NOT NULL,
	rf_name varchar(255),
	
	CONSTRAINT areftab_pkey PRIMARY KEY (rf_code)
);

-----------------------------------------------------------------------

CREATE SCHEMA negative
  AUTHORIZATION postgres
;

-----------------------------------------------------------------------

/*CREATE TABLE negative.base
(
	bs_code integer NOT NULL,
	bs_srcp integer,
	bs_objp integer,
	bs_prsp integer,
	bs_docp integer,
	bs_adrregp integer,
	bs_AdrFactP integer,
	bs_name varchar(255),
	bs_inn varchar(30),
	bs_tel1 varchar(20),
	bs_tel2 varchar(20),
	bs_negative varchar(255),
	bs_3face varchar(255),
	bs_optional text,
	
 	CONSTRAINT base_pkey PRIMARY KEY (bs_code)
);*/

--DROP TABLE negative.base;
CREATE TABLE negative.base
(
	bs_code integer NOT NULL,
	bs_srcp integer,
	bs_name varchar(255),
	bs_bday char(8),
	bs_bplace varchar(100),
	bs_doc varchar(255),
	bs_adrreg varchar(255),
	bs_adrfact varchar(255),
	bs_tel1 varchar(20),
	bs_tel2 varchar(20),
	bs_inn varchar(30),
	bs_negative varchar(255),
	bs_3face varchar(255),
	bs_optional text,
	
 	CONSTRAINT base_pkey PRIMARY KEY (bs_code)
);
