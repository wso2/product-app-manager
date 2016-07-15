CREATE TABLE API_REQUEST_SUMMARY ( 

	api VARCHAR(100),
 	api_version VARCHAR(100),
	version VARCHAR(100),
	trackingCode VARCHAR(100),
 	referer VARCHAR (500),
	apiPublisher VARCHAR(100),
	userId VARCHAR(100),
 	context VARCHAR(100),
	max_request_time BIGINT, 
	total_request_count INT, 
	hostName VARCHAR(100), 
	year SMALLINT, 
	month SMALLINT, 
	day SMALLINT, 
	time VARCHAR(30),	
	PRIMARY KEY(api,api_version,referer,apiPublisher,userId,context,hostName,time)
);

CREATE TABLE CACHE_REQUEST_SUMMARY ( 
	api VARCHAR(100), 
	api_version VARCHAR(100), 
	version VARCHAR(100),
	trackingCode VARCHAR(100), 
	referer VARCHAR (500),
	apiPublisher VARCHAR(100),
	userId VARCHAR(100), 
	context VARCHAR(100),
	max_request_time BIGINT,
	fullRequestPath VARCHAR(100),
	cacheHit INT,
 	total_request_count INT, 
	hostName VARCHAR(100), 
	year SMALLINT, 
	month SMALLINT, 
	day SMALLINT, 
	time VARCHAR(30),
	PRIMARY KEY(api,api_version,version,apiPublisher,userId,context,hostName,time,fullRequestPath,cacheHit)
);

CREATE TABLE API_VERSION_USAGE_SUMMARY (
	api VARCHAR(100), 
	version VARCHAR(100),
	apiPublisher VARCHAR(100),
	context VARCHAR(100),
	trackingCode VARCHAR(100),
	total_request_count INT,
	hostName VARCHAR(100), 
	year SMALLINT, 
	month SMALLINT, 
	day SMALLINT, 
	time VARCHAR(30), 
	PRIMARY KEY(api,version,apiPublisher,context,hostName,time)
);

CREATE TABLE API_RESPONSE_SUMMARY ( 
	api VARCHAR(100), 
	version VARCHAR(100),
	api_version VARCHAR(100),
	apiPublisher VARCHAR(100),
	context VARCHAR(100),
	trackingCode VARCHAR(100),
	referer VARCHAR (500),
	hostName VARCHAR(100),
	serviceTime BIGINT, 
	PRIMARY KEY(api,version,api_version,apiPublisher,context,hostName,referer)
);


CREATE TABLE IF NOT EXISTS APM_APP_HITS (
	uuid VARCHAR(500),
	app_name VARCHAR(200),
	version VARCHAR(50),
	context VARCHAR(256),
	user_id VARCHAR(50),
	tenant_id INT,
	hit_time VARCHAR(500)
);
