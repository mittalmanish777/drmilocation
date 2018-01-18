ALTER TABLE drmi.DRMI_Location__C ADD column caiso_submission_date DATE;
ALTER TABLE drmi.DRMI_Location__C ADD column meter_data_updated_date DATE;
ALTER TABLE drmi.DRMI_Location__C ADD column retrycount INT DEFAULT 0;
alter table drmi.drmi_enrollment__c add column locationid int;
alter table drmi.drmi_enrollment__c add column programid int;


-----------------------------------------------------------
--Registration column changes
ALTER TABLE drmi.drmi_registration_enrollment__c  ALTER COLUMN createddate SET DEFAULT now();
ALTER TABLE drmi.drmi_registration_enrollment__c  ALTER COLUMN systemmodstamp SET DEFAULT now();
ALTER TABLE drmi.drmi_registration_enrollment__c  ADD COLUMN EnrollmentId INT;
ALTER TABLE drmi.drmi_registration_enrollment__c  ADD COLUMN RegistrationId INT;

ALTER TABLE drmi.drmi_registration__C  ALTER COLUMN createddate SET DEFAULT now();
ALTER TABLE drmi.drmi_registration__c  ALTER COLUMN systemmodstamp SET DEFAULT now();
ALTER TABLE drmi.drmi_registration__c ADD COLUMN plr_kw double precision; 
ALTER TABLE drmi.drmi_registration__c ADD COLUMN retrycount integer DEFAULT 0;
ALTER TABLE drmi.drmi_registration__c ADD COLUMN ResourceId integer; 
ALTER TABLE drmi.drmi_registration__c ADD COLUMN meter_data_updated_date Date;
CREATE SEQUENCE registration_name_sequence MINVALUE 10 MAXVALUE 100000 CYCLE;

alter table drmi.drmi_registration_enrollment__c ADD UNIQUE(registrationid,enrollmentid)

alter table drmi.drmi_registration__c add column sqmd_flag_updated_date date; 

--------------------------------------------------------------------

ALTER TABLE drmi.drmi_bid__c ADD COLUMN resource_id INTEGER;

ALTER TABLE drmi.drmi_resource__c ADD COLUMN isautomaticbiddingenabled boolean;

ALTER TABLE drmi.drmi_resource__c ADD COLUMN flag_ei__c boolean;

alter table drmi.drmi_resource__c add max_on__c character varying(255);

alter table drmi.drmi_resource__c add min_on__c character varying(255);

alter table drmi.drmi_resource__c
add column     safetyfactor_he01 double precision,
add column     safetyfactor_he02 double precision,
add column     safetyfactor_he03 double precision,
add column     safetyfactor_he04 double precision,
add column     safetyfactor_he05 double precision,
add column     safetyfactor_he06 double precision,
add column     safetyfactor_he07 double precision,
add column     safetyfactor_he08 double precision,
add column     safetyfactor_he09 double precision,
add column     safetyfactor_he10 double precision,
add column     safetyfactor_he11 double precision,
add column     safetyfactor_he12 double precision,
add column     safetyfactor_he13 double precision,
add column     safetyfactor_he14 double precision,
add column     safetyfactor_he15 double precision,
add column     safetyfactor_he16 double precision,
add column    safetyfactor_he17 double precision,
add column     safetyfactor_he18 double precision,
add column    safetyfactor_he19 double precision,
add column     safetyfactor_he20 double precision,
add column   safetyfactor_he21 double precision,
add column    safetyfactor_he22 double precision,
add column   safetyfactor_he23 double precision,
add column    safetyfactor_he24 double precision;

update drmi.drmi_resource__c set isautomaticbiddingenabled=false;

update drmi.drmi_resource__c set flag_ei__c=true;


update drmi.drmi_resource__c set max_on__c=0.00,min_on__c=0.00;

update drmi.drmi_resource__c set safetyfactor_he01=0.00,safetyfactor_he02=0.00,safetyfactor_he03=0.00,safetyfactor_he04=0.00,safetyfactor_he05=0.00,safetyfactor_he06=0.00,
safetyfactor_he07=0.00,safetyfactor_he08=0.00,safetyfactor_he09=0.00,safetyfactor_he10=0.00,safetyfactor_he11=0.00,safetyfactor_he12=0.00,safetyfactor_he13=0.00,
safetyfactor_he14=0.00,
safetyfactor_he15=0.00,
safetyfactor_he16=0.00,safetyfactor_he17=0.00,safetyfactor_he18=0.00,
safetyfactor_he19=0.00,safetyfactor_he20=0.00,safetyfactor_he21=0.00,
safetyfactor_he22=0.00,safetyfactor_he23=0.00,safetyfactor_he24=0.00 where isdeleted=false;

CREATE TABLE drmi.schedule_messages
(
    id integer NOT NULL DEFAULT nextval('drmi.schedule_messages_id_seq'::regclass),
    bid_id integer,
    he integer,
    message character varying(1000),
    bid_ref_id character varying(18),
    CONSTRAINT schedule_messages_pkey PRIMARY KEY (id)
)


---------------------------------------------
CREATE SEQUENCE drmi.schedule_messages_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;


---------------------------------------------

CREATE OR REPLACE FUNCTION drmi.updatelocationid()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100.0
    VOLATILE NOT LEAKPROOF 
AS $BODY$

BEGIN
UPDATE drmi.drmi_location__c SET location_reference_ei__c = NEW.id WHERE id = NEW.id;
RETURN NEW;
END;

$BODY$;


---------------------------------------------
	
CREATE OR REPLACE FUNCTION drmi.updateenrollmentid()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100.0
    VOLATILE NOT LEAKPROOF 
AS $BODY$

BEGIN
UPDATE drmi.drmi_enrollment__c SET enrollment_id__c = NEW.id WHERE id = NEW.id;
RETURN NEW;
END;

$BODY$;

-------------------------------
CREATE OR REPLACE FUNCTION drmi.updatelocationidfk()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100.0
    VOLATILE NOT LEAKPROOF 
AS $BODY$

BEGIN
UPDATE drmi.drmi_enrollment__c SET Location_Synhronization_Reference_EI__c = NEW.locationid WHERE id = NEW.id;
RETURN NEW;
END;

$BODY$;
-------------------------------
CREATE OR REPLACE FUNCTION drmi.UpdateRegistrationId()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100.0
    VOLATILE NOT LEAKPROOF 
AS $BODY$

BEGIN
UPDATE drmi.drmi_registration__c SET registration_id__c = NEW.id,resource_ei__r__resource_id__c = NEW.resourceId WHERE id = NEW.id;
RETURN NEW;
END;

$BODY$;
---------------------------------------------
CREATE OR REPLACE FUNCTION drmi.UpdateResourceId()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100.0
    VOLATILE NOT LEAKPROOF 
AS $BODY$

BEGIN
UPDATE drmi.drmi_Resource__c SET Resource_id__c = NEW.id WHERE id = NEW.id;
RETURN NEW;
END;

$BODY$;
---------------------------------------------


CREATE OR REPLACE FUNCTION drmi.UpdateRegistrationEnrollmentId()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100.0
    VOLATILE NOT LEAKPROOF 
AS $BODY$

BEGIN
UPDATE drmi.drmi_Registration_Enrollment__c SET External_ref_id_ei__c = NEW.id WHERE id = NEW.id;
UPDATE drmi.drmi_Registration_Enrollment__c 
SET enrollmentid = CAST(enrollment_ei__r__enrollment_id__c AS INT), registrationid = CAST(registration_ei__r__registration_id__c AS INT)
WHERE id = NEW.id and NEW.enrollment_ei__r__enrollment_id__c IS NOT NULL AND NEW.registration_ei__r__registration_id__c IS NOT NULL;

UPDATE drmi.drmi_Registration_Enrollment__c AS rr
SET registrationid = r.id,registration_ei__r__registration_id__c = r.id,enrollmentid = CAST(enrollment_ei__r__enrollment_id__c AS INT)
FROM drmi.drmi_registration__c as r
WHERE rr.id = NEW.id and NEW.registration_ei__c IS NOT NULL AND NEW.registration_ei__r__registration_id__c IS NULL
AND r.sfid = rr.registration_ei__c;

RETURN NEW;
END;

$BODY$;



------------------------------------------
CREATE OR REPLACE FUNCTION drmi.UpdateResourceRefId()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100.0
    VOLATILE NOT LEAKPROOF 
AS $BODY$

BEGIN
UPDATE drmi.drmi_Registration__c set resource_ei__r__resource_id__c = NEW.ResourceId WHERE Id = NEW.Id;
RETURN NEW;
END;

$BODY$;
------------------------------------------------------
CREATE OR REPLACE FUNCTION drmi.drmi_schedule__c()
RETURNS trigger
LANGUAGE 'plpgsql'
COST 100.0
VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
  UPDATE drmi.drmi_schedule__c as s
  set bid_ref_id = b.SFID 
  FROM drmi.drmi_bid__c as b
  WHERE s.id = NEW.id
  AND s.bid_id = b.id;
  RETURN NEW;
END;
$BODY$;

---------------------------------------------
--TRIGGERS
  -------------------------------------------------------
  
CREATE TRIGGER Update_BID_SFID
AFTER UPDATE OF bid_id
ON drmi.drmi_schedule__c
FOR EACH ROW
EXECUTE PROCEDURE drmi.drmi_schedule__c();
------------------------------------------------------

CREATE TRIGGER update_location_id
    AFTER INSERT
    ON drmi.drmi_location__c
    FOR EACH ROW
    EXECUTE PROCEDURE drmi.updatelocationid();
	
 -------------------------------------------------------
CREATE TRIGGER update_location_id_fk
    AFTER UPDATE OF LocationId
    ON drmi.drmi_enrollment__c
    FOR EACH ROW
    --WHEN (((old.locationid IS NULL) AND (new.locationid IS NOT NULL)))
    EXECUTE PROCEDURE drmi.updatelocationidfk();
--------------------------------------------------
CREATE TRIGGER update_enrollment_id
    AFTER INSERT
    ON drmi.drmi_enrollment__c
    FOR EACH ROW
    EXECUTE PROCEDURE drmi.updateenrollmentid();
-----------------
CREATE TRIGGER UpdateRegistrationId
    AFTER INSERT
    ON drmi.drmi_Registration__c
    FOR EACH ROW
    EXECUTE PROCEDURE drmi.UpdateRegistrationId();
 -----------------   
 CREATE TRIGGER UpdateResourceId
    AFTER INSERT
    ON drmi.drmi_Resource__c
    FOR EACH ROW
    EXECUTE PROCEDURE drmi.UpdateResourceId();
 ----------------- 
 CREATE TRIGGER UpdateRegistrationEnrollmentId
    AFTER INSERT
    ON drmi.drmi_Registration_Enrollment__c
    FOR EACH ROW
    EXECUTE PROCEDURE drmi.UpdateRegistrationEnrollmentId();
 ----------------- 
CREATE TRIGGER Update_Resource_Ref_Id
    AFTER UPDATE 
    ON drmi.drmi_Registration__c
    FOR EACH ROW
    WHEN (((old.resourceId IS NULL) AND (new.resourceId IS NOT NULL)))
    EXECUTE PROCEDURE drmi.UpdateResourceRefId();
--------------------------------------------------
 ---------------------------------------------------------------
