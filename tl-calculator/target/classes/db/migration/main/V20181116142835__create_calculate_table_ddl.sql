CREATE TABLE eg_tl_calculator_tradetype(
id CHARACTER VARYING(64),
tenantid CHARACTER VARYING(64),
consumercode CHARACTER VARYING(64),
tradeTypeBillingSlabIds text[],
createdtime bigint,
createdby varchar,
lastmodifiedtime bigint,
lastmodifiedby varchar
);

CREATE TABLE eg_tl_calculator_accessory(
id CHARACTER VARYING(64),
tenantid CHARACTER VARYING(64),
consumercode CHARACTER VARYING(64),
accessoryBillingSlabIds text[],
createdtime bigint,
createdby varchar,
lastmodifiedtime bigint,
lastmodifiedby varchar
);