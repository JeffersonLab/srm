alter session set container = XEPDB1;

-- SEQUENCES

CREATE SEQUENCE SRM_OWNER.CATEGORY_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
	NOCACHE
	ORDER;

CREATE SEQUENCE SRM_OWNER.CHECKLIST_HISTORY_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
	NOCACHE
	ORDER;

CREATE SEQUENCE SRM_OWNER.CHECKLIST_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
	NOCACHE
	ORDER;

CREATE SEQUENCE SRM_OWNER.COMPONENT_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
	NOCACHE
	ORDER;

CREATE SEQUENCE SRM_OWNER.GROUP_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
	NOCACHE
	ORDER;

CREATE SEQUENCE SRM_OWNER.GROUP_RESPONSIBILITY_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
	NOCACHE
	ORDER;

CREATE SEQUENCE SRM_OWNER.GROUP_SIGNOFF_HISTORY_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
	NOCACHE
	ORDER;

CREATE SEQUENCE SRM_OWNER.GROUP_SIGNOFF_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
	NOCACHE
	ORDER;

CREATE SEQUENCE SRM_OWNER.HIBERNATE_SEQUENCE
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
	NOCACHE
	ORDER;

CREATE SEQUENCE SRM_OWNER.MASKING_REQUEST_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
	NOCACHE
	ORDER;

CREATE SEQUENCE SRM_OWNER.SAVED_SIGNOFF_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
	NOCACHE
	ORDER;

CREATE SEQUENCE SRM_OWNER.SYSTEM_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
	NOCACHE
	ORDER;

-- TABLES

CREATE TABLE SRM_OWNER.APPLICATION_REVISION_INFO
(
    REV                  INTEGER NOT NULL ,
    REVTSTMP             INTEGER NOT NULL ,
    USERNAME             VARCHAR2(64) NULL ,
    ADDRESS              VARCHAR2(64) NULL ,
    CONSTRAINT APPLICATION_REVISION_INFO_PK PRIMARY KEY (REV)
);

CREATE TABLE SRM_OWNER.SYSTEM_AUD
(
    SYSTEM_ID            INTEGER NOT NULL ,
    REV                  INTEGER NOT NULL ,
    REVTYPE              INTEGER NOT NULL ,
    NAME                 VARCHAR2(128 CHAR) NOT NULL ,
    CATEGORY_ID          INTEGER NOT NULL ,
    WEIGHT               INTEGER NULL ,
    CONSTRAINT SYSTEM_AUD_PK PRIMARY KEY (SYSTEM_ID,REV),
    CONSTRAINT SYSTEM_AUD_FK1 FOREIGN KEY (REV) REFERENCES SRM_OWNER.APPLICATION_REVISION_INFO (REV)
);

CREATE TABLE SRM_OWNER.APPLICATION
(
    APPLICATION_ID       INTEGER NOT NULL ,
    NAME                 VARCHAR2(128 CHAR) NOT NULL ,
    CONSTRAINT APPLICATION_PK PRIMARY KEY (APPLICATION_ID),
    CONSTRAINT APPLICATION_AK1 UNIQUE (NAME)
);

CREATE TABLE SRM_OWNER.CATEGORY
(
    CATEGORY_ID          INTEGER NOT NULL ,
    NAME                 VARCHAR2(128 CHAR) NOT NULL ,
    PARENT_ID            INTEGER NULL ,
    WEIGHT               INTEGER NULL ,
    CONSTRAINT CATEGORY_PK PRIMARY KEY (CATEGORY_ID),
    CONSTRAINT CATEGORY_AK1 UNIQUE (NAME),
    CONSTRAINT CATEGORY_FK1 FOREIGN KEY (PARENT_ID) REFERENCES SRM_OWNER.CATEGORY (CATEGORY_ID) ON DELETE SET NULL
);

CREATE TABLE SRM_OWNER.SYSTEM
(
    SYSTEM_ID            INTEGER NOT NULL ,
    NAME                 VARCHAR2(128 CHAR) NOT NULL ,
    CATEGORY_ID          INTEGER NOT NULL ,
    WEIGHT               INTEGER NULL ,
    CONSTRAINT SYSTEM_PK PRIMARY KEY (SYSTEM_ID),
    CONSTRAINT SYSTEM_AK1 UNIQUE (NAME),
    CONSTRAINT SYSTEM_FK1 FOREIGN KEY (CATEGORY_ID) REFERENCES SRM_OWNER.CATEGORY (CATEGORY_ID) ON DELETE SET NULL
);

CREATE TABLE SRM_OWNER.SYSTEM_APPLICATION
(
    APPLICATION_ID       INTEGER NOT NULL ,
    SYSTEM_ID            INTEGER NOT NULL ,
    CONSTRAINT SYSTEM_APPLICATION_PK PRIMARY KEY (APPLICATION_ID,SYSTEM_ID),
    CONSTRAINT SYSTEM_APPLICATION_FK1 FOREIGN KEY (APPLICATION_ID) REFERENCES SRM_OWNER.APPLICATION (APPLICATION_ID),
    CONSTRAINT SYSTEM_APPLICATION_FK2 FOREIGN KEY (SYSTEM_ID) REFERENCES SRM_OWNER.SYSTEM (SYSTEM_ID) ON DELETE CASCADE
);

CREATE TABLE SRM_OWNER.REGION
(
    REGION_ID            INTEGER NOT NULL ,
    NAME                 VARCHAR2(128 CHAR) NOT NULL ,
    ALIAS                VARCHAR2(128 CHAR) NULL ,
    WEIGHT               INTEGER NULL ,
    CONSTRAINT REGION_PK PRIMARY KEY (REGION_ID)
);

CREATE TABLE SRM_OWNER.STATUS
(
    STATUS_ID            INTEGER NOT NULL ,
    NAME                 VARCHAR2(128 CHAR) NOT NULL ,
    CONSTRAINT STATUS_PK PRIMARY KEY (STATUS_ID),
    CONSTRAINT STATUS_AK1 UNIQUE (NAME)
);

CREATE TABLE SRM_OWNER.RESPONSIBLE_GROUP
(
    GROUP_ID             INTEGER NOT NULL ,
    NAME                 VARCHAR2(128 CHAR) NOT NULL ,
    DESCRIPTION          VARCHAR2(1024 CHAR) NULL ,
    GOAL_PERCENT         SMALLINT DEFAULT 0 NOT NULL CONSTRAINT RESPONSIBLE_GROUP_CK1 CHECK (GOAL_PERCENT BETWEEN 0 AND 100),
    LEADER_WORKGROUP_ID  INTEGER DEFAULT 123037 NOT NULL ,
    LEADER_WORKGROUP     VARCHAR2(64 CHAR) DEFAULT 'hcoadm' NOT NULL ,
    CONSTRAINT RESPONSIBLE_GROUP_PK PRIMARY KEY (GROUP_ID),
    CONSTRAINT RESPONSIBLE_GROUP_AK1 UNIQUE (NAME)
);

CREATE TABLE SRM_OWNER.SAVED_SIGNOFF_TYPE
(
    SAVED_SIGNOFF_TYPE_ID INTEGER NOT NULL ,
    NAME                 VARCHAR2(128 CHAR) NOT NULL ,
    WEIGHT               INTEGER NULL ,
    CONSTRAINT SAVED_SIGNOFF_TYPE_PK PRIMARY KEY (SAVED_SIGNOFF_TYPE_ID),
    CONSTRAINT  SAVED_SIGNOFF_TYPE_AK1 UNIQUE (NAME)
);

CREATE TABLE SRM_OWNER.SAVED_SIGNOFF
(
    SAVED_SIGNOFF_ID      INTEGER NOT NULL ,
    SAVED_SIGNOFF_TYPE_ID INTEGER NOT NULL ,
    SIGNOFF_NAME          VARCHAR2(256 CHAR) NOT NULL ,
    FILTER_GROUP_ID       INTEGER NULL ,
    FILTER_SYSTEM_ID      INTEGER NULL ,
    FILTER_REGION_ID      INTEGER NULL ,
    FILTER_STATUS_ID      INTEGER NULL ,
    FILTER_COMPONENT_NAME VARCHAR2(128 CHAR) NULL ,
    SIGNOFF_STATUS_ID     INTEGER NOT NULL ,
    SIGNOFF_COMMENTS      VARCHAR2(1024 CHAR) NULL ,
    WEIGHT                INTEGER NULL ,
    CONSTRAINT SAVED_SIGNOFF_PK PRIMARY KEY (SAVED_SIGNOFF_ID),
    CONSTRAINT SAVED_SIGNOFF_AK1 UNIQUE (SIGNOFF_NAME),
    CONSTRAINT SAVED_SIGNOFF_FK1 FOREIGN KEY (FILTER_REGION_ID) REFERENCES SRM_OWNER.REGION (REGION_ID) ON DELETE SET NULL,
    CONSTRAINT SAVED_SIGNOFF_FK2 FOREIGN KEY (SIGNOFF_STATUS_ID) REFERENCES SRM_OWNER.STATUS (STATUS_ID) ON DELETE SET NULL,
    CONSTRAINT SAVED_SIGNOFF_FK3 FOREIGN KEY (FILTER_SYSTEM_ID) REFERENCES SRM_OWNER.SYSTEM (SYSTEM_ID) ON DELETE SET NULL,
    CONSTRAINT SAVED_SIGNOFF_FK4 FOREIGN KEY (FILTER_GROUP_ID) REFERENCES SRM_OWNER.RESPONSIBLE_GROUP (GROUP_ID) ON DELETE SET NULL,
    CONSTRAINT SAVED_SIGNOFF_FK5 FOREIGN KEY (FILTER_STATUS_ID) REFERENCES SRM_OWNER.STATUS (STATUS_ID) ON DELETE SET NULL,
    CONSTRAINT SAVED_SIGNOFF_FK6 FOREIGN KEY (SAVED_SIGNOFF_TYPE_ID) REFERENCES SRM_OWNER.SAVED_SIGNOFF_TYPE (SAVED_SIGNOFF_TYPE_ID) ON DELETE SET NULL
);

CREATE TABLE SRM_OWNER.COMPONENT
(
    COMPONENT_ID         INTEGER NOT NULL ,
    NAME                 VARCHAR2(128 CHAR) NOT NULL CONSTRAINT COMPONENT_CK4 CHECK (INSTR(NAME, '*') = 0),
    SYSTEM_ID            INTEGER NOT NULL ,
    DATA_SOURCE          VARCHAR2(24 CHAR) DEFAULT 'INTERNAL' NOT NULL CONSTRAINT COMPONENT_CK1 CHECK (DATA_SOURCE IN ('INTERNAL', 'CED', 'LED', 'UED')),
    DATA_SOURCE_ID       INTEGER NULL ,
    REGION_ID            INTEGER NOT NULL ,
    MASKED               CHAR(1 CHAR) DEFAULT 'N' NOT NULL CONSTRAINT COMPONENT_CK3 CHECK (MASKED IN ('Y', 'N')),
	MASKED_COMMENT       VARCHAR2(512 CHAR) NULL ,
	MASKED_DATE          DATE NULL ,
	MASKED_BY            INTEGER NULL ,
    MASKED_USERNAME      VARCHAR2(64 CHAR) NULL ,
	WEIGHT               INTEGER NULL ,
	ADDED_DATE           DATE DEFAULT SYSDATE NOT NULL ,
	UNPOWERED_YN         CHAR(1 CHAR) DEFAULT 'N' NOT NULL CONSTRAINT COMPONENT_CK5 CHECK (UNPOWERED_YN IN ('Y', 'N')),
	MASK_EXPIRATION_DATE DATE NULL ,
	MASK_TYPE_ID         INTEGER NULL  CONSTRAINT  COMPONENT_CK6 CHECK (MASK_TYPE_ID IN (150, 200, 250)),
	NAME_ALIAS           VARCHAR2(128 CHAR),
CONSTRAINT COMPONENT_PK PRIMARY KEY (COMPONENT_ID),
CONSTRAINT COMPONENT_AK1 UNIQUE (NAME,SYSTEM_ID),
CONSTRAINT COMPONENT_AK2 UNIQUE (SYSTEM_ID,COMPONENT_ID),
CONSTRAINT COMPONENT_FK1 FOREIGN KEY (REGION_ID) REFERENCES SRM_OWNER.REGION (REGION_ID),
CONSTRAINT COMPONENT_FK2 FOREIGN KEY (SYSTEM_ID) REFERENCES SRM_OWNER.SYSTEM (SYSTEM_ID) ON DELETE SET NULL,
CONSTRAINT COMPONENT_CK2 CHECK ( (DATA_SOURCE_ID IS NOT NULL AND DATA_SOURCE = 'CED') OR
(DATA_SOURCE_ID IS NOT NULL AND DATA_SOURCE = 'LED') OR
(DATA_SOURCE_ID IS NOT NULL AND DATA_SOURCE = 'UED')
OR
(DATA_SOURCE_ID IS NULL AND DATA_SOURCE = 'INTERNAL') )
);

CREATE TABLE SRM_OWNER.MASKING_REQUEST
(
    MASKING_REQUEST_ID   INTEGER NOT NULL ,
    COMPONENT_ID         INTEGER NOT NULL ,
    REQUEST_BY           INTEGER DEFAULT 123037 NOT NULL ,
    REQUEST_USERNAME     VARCHAR2(64 CHAR) DEFAULT 'hcoadm' NOT NULL ,
    REQUEST_DATE         DATE NOT NULL ,
    REQUEST_REASON       VARCHAR2(512 CHAR) NOT NULL ,
    REQUEST_STATUS       VARCHAR2(32) DEFAULT 'PENDING' NOT NULL CONSTRAINT MASKING_REQUEST_CK1 CHECK (REQUEST_STATUS IN ('PENDING', 'ACCEPTED', 'REJECTED')),
    MASK_EXPIRATION_DATE DATE NOT NULL ,
    CONSTRAINT MASKING_REQUEST_PK PRIMARY KEY (MASKING_REQUEST_ID),
    CONSTRAINT MASKING_REQUEST_FK1 FOREIGN KEY (COMPONENT_ID) REFERENCES SRM_OWNER.COMPONENT (COMPONENT_ID) ON DELETE CASCADE
);

CREATE TABLE SRM_OWNER.HCO_SETTINGS
(
    HCO_SETTINGS_ID      INTEGER NOT NULL ,
    GOAL_DATE            DATE NULL ,
    AUTO_EMAIL_YN        CHAR(1 CHAR) DEFAULT 'N' NOT NULL CONSTRAINT  HCO_SETTINGS_CK1 CHECK (AUTO_EMAIL_YN IN ('Y', 'N')),
	FEEDBACK_EMAIL_CSV   VARCHAR2(512) NULL ,
	MASK_REQUEST_EMAIL_CSV VARCHAR2(512) NULL ,
	ACTIVITY_EMAIL_CSV   VARCHAR2(512) NULL ,
CONSTRAINT HCO_SETTINGS_PK PRIMARY KEY (HCO_SETTINGS_ID)
);

CREATE TABLE SRM_OWNER.GROUP_SIGNOFF_HISTORY
(
    GROUP_SIGNOFF_HISTORY_ID INTEGER NOT NULL ,
    SYSTEM_ID                NUMBER NOT NULL,
    GROUP_ID                 NUMBER NOT NULL,
    COMPONENT_ID             NUMBER NOT NULL,
    STATUS_ID                INTEGER NOT NULL ,
    MODIFIED_BY              INTEGER DEFAULT 123037 NOT NULL ,
    MODIFIED_USERNAME        VARCHAR2(64 CHAR) DEFAULT 'hcoadm' NOT NULL ,
    MODIFIED_DATE            DATE NOT NULL ,
    COMMENTS                 VARCHAR2(1024 CHAR) NULL ,
    CHANGE_TYPE              VARCHAR2(24 CHAR) NULL CONSTRAINT GROUP_SIGNOFF_HISTORY_CK1 CHECK (CHANGE_TYPE IN ('UPGRADE', 'DOWNGRADE', 'CASCADE', 'COMMENT')),
    CONSTRAINT GROUP_SIGNOFF_HISTORY_PK PRIMARY KEY (GROUP_SIGNOFF_HISTORY_ID),
    CONSTRAINT GROUP_SIGNOFF_HISTORY_AK1 UNIQUE (GROUP_ID,SYSTEM_ID,COMPONENT_ID,MODIFIED_DATE)
);

CREATE TABLE SRM_OWNER.CHECKLIST
(
    CHECKLIST_ID         INTEGER NOT NULL ,
    BODY_HTML            CLOB NULL ,
    AUTHOR               VARCHAR2(64 CHAR) NULL ,
    MODIFIED_DATE        DATE NOT NULL ,
    MODIFIED_BY          INTEGER DEFAULT 123037 NOT NULL ,
    MODIFIED_USERNAME    VARCHAR2(64 CHAR) DEFAULT 'hcoadm' NOT NULL ,
    COMMENTS             VARCHAR2(1024 CHAR) NULL ,
    CONSTRAINT CHECKLIST_PK PRIMARY KEY (CHECKLIST_ID)
);

CREATE TABLE SRM_OWNER.GROUP_RESPONSIBILITY
(
    GROUP_RESPONSIBILITY_ID INTEGER NOT NULL ,
    GROUP_ID                INTEGER NOT NULL ,
    SYSTEM_ID               INTEGER NOT NULL ,
    WEIGHT                  INTEGER NOT NULL ,
    CHECKLIST_ID            INTEGER NULL ,
    CHECKLIST_REQUIRED      CHAR(1 CHAR) DEFAULT 'Y' NOT NULL CONSTRAINT GROUP_RESPONSIBILITY_CK1 CHECK (CHECKLIST_REQUIRED IN ('Y', 'N')),
	PUBLISHED               CHAR(1 CHAR) NOT NULL ,
	PUBLISHED_DATE          DATE NULL ,
	PUBLISHED_BY            INTEGER NULL ,
    PUBLISHED_USERNAME      VARCHAR2(64 CHAR) NULL ,
CONSTRAINT GROUP_RESPONSIBILITY_PK PRIMARY KEY (GROUP_RESPONSIBILITY_ID),
CONSTRAINT GROUP_RESPONSIBILITY_AK2 UNIQUE (SYSTEM_ID,WEIGHT),
CONSTRAINT GROUP_RESPONSIBILITY_AK1 UNIQUE (GROUP_ID,SYSTEM_ID),
CONSTRAINT GROUP_RESPONSIBILITY_FK1 FOREIGN KEY (GROUP_ID) REFERENCES SRM_OWNER.RESPONSIBLE_GROUP (GROUP_ID),
CONSTRAINT GROUP_RESPONSIBILITY_FK2 FOREIGN KEY (SYSTEM_ID) REFERENCES SRM_OWNER.SYSTEM (SYSTEM_ID) ON DELETE SET NULL,
CONSTRAINT GROUP_RESPONSIBILITY_FK3 FOREIGN KEY (CHECKLIST_ID) REFERENCES SRM_OWNER.CHECKLIST (CHECKLIST_ID) ON DELETE SET NULL
);

CREATE TABLE SRM_OWNER.GROUP_SIGNOFF
(
    GROUP_SIGNOFF_ID     INTEGER NOT NULL ,
    SYSTEM_ID            INTEGER NOT NULL ,
    GROUP_ID             INTEGER NOT NULL ,
    COMPONENT_ID         INTEGER NOT NULL ,
    STATUS_ID            INTEGER NOT NULL ,
    MODIFIED_BY          INTEGER DEFAULT 123037 NOT NULL ,
    MODIFIED_USERNAME    VARCHAR2(64 CHAR) DEFAULT 'hcoadm' NOT NULL ,
    MODIFIED_DATE        DATE DEFAULT  SYSDATE  NOT NULL ,
    COMMENTS             VARCHAR2(1024 CHAR) NULL ,
    CHANGE_TYPE          VARCHAR2(24 CHAR) NOT NULL CONSTRAINT GROUP_SIGNOFF_CK1 CHECK (CHANGE_TYPE IN ('UPGRADE', 'DOWNGRADE', 'CASCADE', 'COMMENT')),
    CONSTRAINT GROUP_SIGNOFF_PK PRIMARY KEY (GROUP_SIGNOFF_ID),
    CONSTRAINT GROUP_SIGNOFF_AK1 UNIQUE (SYSTEM_ID,GROUP_ID,COMPONENT_ID),
    CONSTRAINT GROUP_SIGNOFF_FK1 FOREIGN KEY (SYSTEM_ID, COMPONENT_ID) REFERENCES SRM_OWNER.COMPONENT (SYSTEM_ID, COMPONENT_ID) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
    CONSTRAINT GROUP_SIGNOFF_FK2 FOREIGN KEY (STATUS_ID) REFERENCES SRM_OWNER.STATUS (STATUS_ID),
    CONSTRAINT GROUP_SIGNOFF_FK3 FOREIGN KEY (GROUP_ID, SYSTEM_ID) REFERENCES SRM_OWNER.GROUP_RESPONSIBILITY (GROUP_ID, SYSTEM_ID) ON DELETE CASCADE
);

CREATE TABLE SRM_OWNER.BEAM_DESTINATION
(
    BEAM_DESTINATION_ID  INTEGER NOT NULL ,
    NAME                 VARCHAR2(128 CHAR) NULL ,
    WEIGHT               INTEGER NULL ,
    TARGET_YN            CHAR(1 CHAR) DEFAULT 'N' NOT NULL CONSTRAINT BEAM_DESTINATION_CK1 CHECK (TARGET_YN IN ('Y', 'N')),
CONSTRAINT  BEAM_DESTINATION_PK PRIMARY KEY (BEAM_DESTINATION_ID),
CONSTRAINT  BEAM_DESTINATION_AK1 UNIQUE (NAME)
);

CREATE TABLE SRM_OWNER.COMPONENT_BEAM_DESTINATION
(
    COMPONENT_ID         INTEGER NOT NULL ,
    BEAM_DESTINATION_ID  INTEGER NOT NULL ,
    CONSTRAINT COMPONENT_BEAM_DESTINATION_PK PRIMARY KEY (COMPONENT_ID,BEAM_DESTINATION_ID),
    CONSTRAINT COMPONENT_BEAM_DESTINATION_FK1 FOREIGN KEY (BEAM_DESTINATION_ID) REFERENCES SRM_OWNER.BEAM_DESTINATION (BEAM_DESTINATION_ID) ON DELETE CASCADE,
    CONSTRAINT COMPONENT_BEAM_DESTINATION_FK2 FOREIGN KEY (COMPONENT_ID) REFERENCES SRM_OWNER.COMPONENT (COMPONENT_ID) ON DELETE CASCADE
);

CREATE TABLE SRM_OWNER.COMPONENT_AUD
(
    COMPONENT_ID         INTEGER NOT NULL ,
    REV                  INTEGER NOT NULL ,
    REVTYPE              INTEGER NOT NULL ,
    NAME                 VARCHAR2(128 CHAR) NOT NULL CONSTRAINT COMPONENT_AUD_CK1 CHECK (INSTR(NAME, '*') = 0),
    SYSTEM_ID            INTEGER NOT NULL ,
    DATA_SOURCE          VARCHAR2(24 CHAR) DEFAULT 'INTERNAL' NOT NULL CONSTRAINT COMPONENT_AUD_CK2 CHECK (DATA_SOURCE IN ('INTERNAL', 'CED', 'LED', 'UED')),
    DATA_SOURCE_ID       INTEGER NULL ,
    REGION_ID            INTEGER NOT NULL ,
    MASKED               CHAR(1 CHAR) DEFAULT 'N' NOT NULL CONSTRAINT COMPONENT_AUD_CK3 CHECK (MASKED IN ('Y', 'N')),
	MASKED_COMMENT       VARCHAR2(512 CHAR) NULL ,
	MASKED_DATE          DATE NULL ,
	MASKED_BY            INTEGER NULL ,
    MASKED_USERNAME      VARCHAR2(64 CHAR) NULL ,
	WEIGHT               INTEGER NULL ,
	ADDED_DATE           DATE DEFAULT  SYSDATE  NOT NULL ,
	UNPOWERED_YN         CHAR(1 CHAR) DEFAULT 'N' NOT NULL CONSTRAINT COMPONENT_AUD_CK4 CHECK (UNPOWERED_YN IN ('Y', 'N')),
	MASK_EXPIRATION_DATE DATE NULL ,
	MASK_TYPE_ID         INTEGER NULL ,
	NAME_ALIAS           VARCHAR2(128 CHAR),
CONSTRAINT COMPONENT_AUD_PK PRIMARY KEY (COMPONENT_ID,REV),
CONSTRAINT COMPONENT_AUD_FK1 FOREIGN KEY (REV) REFERENCES SRM_OWNER.APPLICATION_REVISION_INFO (REV),
CONSTRAINT COMPONENT_AUD_CK5 CHECK ( (DATA_SOURCE_ID IS NOT NULL AND DATA_SOURCE = 'CED') OR
(DATA_SOURCE_ID IS NOT NULL AND DATA_SOURCE = 'LED') OR
(DATA_SOURCE_ID IS NOT NULL AND DATA_SOURCE = 'UED')
OR
(DATA_SOURCE_ID IS NULL AND DATA_SOURCE = 'INTERNAL') )
);

CREATE TABLE SRM_OWNER.CHECKLIST_HISTORY
(
    CHECKLIST_HISTORY_ID INTEGER NOT NULL ,
    CHECKLIST_ID         INTEGER NULL ,
    BODY_HTML            CLOB NULL ,
    AUTHOR               VARCHAR2(64 CHAR) NULL ,
    MODIFIED_DATE        DATE NOT NULL ,
    MODIFIED_BY          INTEGER DEFAULT 123037 NOT NULL ,
    MODIFIED_USERNAME    VARCHAR2(64 CHAR) DEFAULT 'hcoadm' NOT NULL ,
    COMMENTS             VARCHAR2(1024 CHAR) NULL ,
    CONSTRAINT CHECKLIST_HISTORY_PK PRIMARY KEY (CHECKLIST_HISTORY_ID),
    CONSTRAINT CHECKLIST_HISTORY_FK1 FOREIGN KEY (CHECKLIST_ID) REFERENCES SRM_OWNER.CHECKLIST (CHECKLIST_ID) ON DELETE CASCADE
);

CREATE TABLE SRM_OWNER.CATEGORY_AUD
(
    CATEGORY_ID          INTEGER NOT NULL ,
    REV                  INTEGER NOT NULL ,
    REVTYPE              INTEGER NOT NULL ,
    NAME                 VARCHAR2(128 CHAR) NOT NULL ,
    PARENT_ID            INTEGER NULL ,
    WEIGHT               INTEGER NULL ,
    CONSTRAINT CATEGORY_AUD_PK PRIMARY KEY (CATEGORY_ID,REV),
    CONSTRAINT CATEGORY_AUD_FK1 FOREIGN KEY (REV) REFERENCES SRM_OWNER.APPLICATION_REVISION_INFO (REV)
);

-- Tmp Table
/**
 * Obtain a cursor over category ids representing the children of a
 * particular category based on a set of filter criteria
 */
create global temporary table srm_owner.tmp_component_categories (category_id integer) on commit delete rows;
create global temporary table srm_owner.tmp_activity_summary (GROUP_ID integer, CHANGE_TYPE varchar2(24), STATUS_ID integer, SIGNOFF_COUNT integer) on commit delete rows;

-- Custom Oracle Types

CREATE TYPE SRM_OWNER.CATEGORY_STATUS IS OBJECT (CATEGORY_ID INTEGER, STATUS_ID INTEGER);
/
CREATE TYPE SRM_OWNER.CATEGORY_STATUS_TABLE IS TABLE OF SRM_OWNER.CATEGORY_STATUS;
/
CREATE TYPE SRM_OWNER.SYSTEM_STATUS IS OBJECT (SYSTEM_ID INTEGER, STATUS_ID INTEGER);
/
CREATE TYPE SRM_OWNER.SYSTEM_STATUS_TABLE IS TABLE OF SRM_OWNER.SYSTEM_STATUS;
/
CREATE TYPE SRM_OWNER.CATEGORY_CHILD AS OBJECT(CATEGORY_ID INTEGER);
/
CREATE TYPE SRM_OWNER.CATEGORY_CHILD_TABLE IS TABLE OF SRM_OWNER.CATEGORY_CHILD;
/
CREATE TYPE SRM_OWNER.NUMBER_TAB AS TABLE OF NUMBER;
/

-- Special Indexes (Performance tweaks)

-- Note: Unique Constraint could work if not for NVL2()
CREATE UNIQUE INDEX COMPONENT_PERF1 ON SRM_OWNER.COMPONENT (DATA_SOURCE, NVL2(DATA_SOURCE_ID, DATA_SOURCE_ID, COMPONENT_ID));

-- Not Unique, so not a constraint
CREATE INDEX GROUP_SIGNOFF_IN1 on SRM_OWNER.GROUP_SIGNOFF(COMPONENT_ID, STATUS_ID);

-- Views

--
-- Creates a view showing the status_id for all the groups that must
-- signoff on the component.  It accounts for the fact that
-- an entry in the group_signoff table may be missing by assigning
-- 100 (Not Ready)
--
create view srm_owner.component_signoff as
select c.system_id, c.component_id, c.group_id, nvl(x.status_id, 100) as status_id
from
    srm_owner.group_signoff x,
    (select c.system_id, c.component_id, g.group_id
     from srm_owner.component c, srm_owner.group_responsibility g
     where
             c.system_id = g.system_id) c
where
        c.group_id = x.group_id (+) and
        c.component_id = x.component_id (+);

--
-- Creates a view showing the status of components, including those which are masked.
--
CREATE VIEW SRM_OWNER.COMPONENT_STATUS ("COMPONENT_ID", "NAME", "WEIGHT", "SYSTEM_ID", "SYSTEM_NAME", "REGION_ID", "REGION_NAME", "UNPOWERED_YN", "STATUS_ID") AS
select a.component_id, a.name, a.weight, a.system_id, c.name as system_name, a.region_id, d.name as region_name, a.unpowered_yn,
       case
           when a.masked = 'Y' then a.mask_type_id
           else nvl((select max(b.status_id) from srm_owner.component_signoff b where a.component_id = b.component_id), 1)
           end as status_id
from srm_owner.component a,
     srm_owner.system c,
     srm_owner.region d
where a.system_id = c.system_id
  and a.region_id = d.region_id;

-- This is a version with less columns (streamlined)
CREATE VIEW SRM_OWNER.COMPONENT_STATUS_2 ("COMPONENT_ID", "STATUS_ID") AS
select a.component_id,
       case
           when a.masked = 'Y' then a.mask_type_id
           else nvl((select max(b.status_id) from srm_owner.component_signoff b where a.component_id = b.component_id), 1)
           end as status_id
from srm_owner.component a;

-- See org.jlab.srm.persistence.entity.view.SignoffActivityRecord
create view signoff_activity as
select
    a.group_signoff_history_id,
    a.modified_date,
    a.modified_by,
    a.comments,
    a.system_id,
    a.component_id,
    a.group_id,
    a.status_id,
    a.change_type,
    c.region_id,
    b.name as system_name,
    c.name as component_name,
    c.unpowered_yn,
    d.name as group_name,
    e.name as status_name,
    f.name as region_name,
    a.modified_username
from
    srm_owner.group_signoff_history a,
    srm_owner.system b,
    srm_owner.component c,
    srm_owner.responsible_group d,
    srm_owner.status e,
    srm_owner.region f
where a.system_id = b.system_id
  and a.component_id = c.component_id
  and a.group_id = d.group_id
  and a.status_id = e.status_id
  and c.region_id = f.region_id;

create or replace view activity_summary as
select a.group_id, a.name,
       nvl((select signoff_count from srm_owner.tmp_activity_summary where group_id = a.group_id and change_type = 'UPGRADE' and status_id = 1), 0) as Upgrade_Ready_Count,
       nvl((select signoff_count from srm_owner.tmp_activity_summary where group_id = a.group_id and change_type = 'UPGRADE' and status_id = 50), 0) as Upgrade_Checked_Count,
       nvl((select signoff_count from srm_owner.tmp_activity_summary where group_id = a.group_id and change_type = 'DOWNGRADE' and status_id = 50), 0) as Downgrade_Checked_Count,
       nvl((select signoff_count from srm_owner.tmp_activity_summary where group_id = a.group_id and change_type = 'DOWNGRADE' and status_id = 100), 0) as Downgrade_Not_Ready_Count,
       nvl((select signoff_count from srm_owner.tmp_activity_summary where group_id = a.group_id and change_type = 'CASCADE' and status_id = 50), 0) as Cascade_Count,
       nvl((select signoff_count from srm_owner.tmp_activity_summary where group_id = a.group_id and change_type = 'COMMENT' and status_id = 1), 0) +
       nvl((select signoff_count from srm_owner.tmp_activity_summary where group_id = a.group_id and change_type = 'COMMENT' and status_id = 50), 0) +
       nvl((select signoff_count from srm_owner.tmp_activity_summary where group_id = a.group_id and change_type = 'COMMENT' and status_id = 100), 0) as Comment_Count
from srm_owner.responsible_group a order by a.name asc;

-- Functions

/*Check all prior signoff statuses to see who is in the hot seat / at bat */
create or replace function srm_owner.previous_signoff_status(v_component_id in integer, v_weight in integer)
    return integer
    is
    retval integer;
    cursor c1 is
        select max(m.status_id) from srm_owner.component_signoff m, srm_owner.group_responsibility n where m.system_id = n.system_id and m.group_id = n.group_id and m.component_id = v_component_id and n.weight < v_weight order by n.weight desc;
begin
    OPEN c1;
    FETCH c1 INTO retval;

    IF c1%notfound THEN
        retval := 1;
    END IF;

    IF retval IS NULL THEN
        retval := 1;
    END IF;

    CLOSE c1;
    RETURN retval;

EXCEPTION
    WHEN OTHERS THEN
        raise_application_error(-20001,'An error was encountered - '||SQLCODE||' -ERROR- '||SQLERRM);
end;
/

create or replace FUNCTION SRM_OWNER.csv_2_nums(
    p_str IN VARCHAR2)
    RETURN number_tab
AS
    l_str LONG DEFAULT p_str || ',';
    l_n NUMBER;
    l_data number_tab := number_tab();
BEGIN
    LOOP
        l_n := instr( l_str, ',' );
        EXIT
            WHEN (NVL(l_n,0) = 0);
        l_data.extend;
        l_data( l_data.count ) := ltrim(rtrim(SUBSTR(l_str,1,l_n-1)));
        l_str                  := SUBSTR( l_str, l_n            +1 );
    END LOOP;
    RETURN l_data;
END;
/

create or replace FUNCTION SRM_OWNER.FILTER_SYSTEM_STATUS (sys_id in INTEGER, dest_id_csv in VARCHAR2, sys_id_csv in VARCHAR2, reg_id in INTEGER, grp_id in INTEGER, stat_id_csv in VARCHAR2)
    RETURN INTEGER
    IS
    retval integer;

    CURSOR c1 IS
        SELECT MAX(a.status_id)
        FROM
            component_status_2 a left join component b on a.component_id = b.component_id
                                 left outer join component_beam_destination c on b.component_id = c.component_id
        WHERE b.system_id = sys_id
          AND b.masked = 'N'
          AND CASE
                  WHEN reg_id IS NULL THEN 1
                  WHEN reg_id IS NOT NULL AND b.region_id = reg_id THEN 1
                  ELSE 0 END = 1
          AND CASE
                  WHEN dest_id_csv IS NULL THEN 1
                  WHEN dest_id_csv IS NOT NULL AND c.beam_destination_id in (select * from table(csv_2_nums(dest_id_csv))) THEN 1
                  ELSE 0 END = 1
          AND CASE
                  WHEN sys_id_csv IS NULL THEN 1
                  WHEN sys_id_csv IS NOT NULL AND b.system_id in (select * from table(csv_2_nums(sys_id_csv))) THEN 1
                  ELSE 0 END = 1
          AND CASE
                  WHEN grp_id IS NULL THEN 1
                  WHEN grp_id IS NOT NULL AND a.component_id in (select component_id from component_signoff z where z.group_id = grp_id) THEN 1
                  ELSE 0 END = 1
          AND CASE
                  WHEN stat_id_csv IS NULL THEN 1
                  WHEN stat_id_csv IS NOT NULL AND a.status_id in (select * from table(csv_2_nums(stat_id_csv))) THEN 1
                  ELSE 0 END = 1;
BEGIN
    OPEN c1;
    FETCH c1 INTO retval;

    IF c1%notfound THEN
        retval := 1;
    END IF;

    IF retval IS NULL THEN
        retval := 1;
    END IF;

    CLOSE c1;
    RETURN retval;

EXCEPTION
    WHEN OTHERS THEN
        raise_application_error(-20001,'An error was encountered - '||SQLCODE||' -ERROR- '||SQLERRM);
END;
/

create or replace FUNCTION SRM_OWNER.FILTER_CATEGORY_STATUS (cat_id in INTEGER, dest_id_csv in VARCHAR2, sys_id_csv in VARCHAR2, reg_id in INTEGER, grp_id in INTEGER, stat_id_csv in VARCHAR2)
    RETURN INTEGER
    IS
    retval integer;

    CURSOR c1 IS
        SELECT MAX(filter_system_status(system_id, dest_id_csv, sys_id_csv, reg_id, grp_id, stat_id_csv)) FROM system WHERE category_id IN
                                                                                                                            (SELECT category_id FROM category START WITH category_id = cat_id CONNECT BY PRIOR category_id = parent_id);
BEGIN
    OPEN c1;
    FETCH c1 INTO retval;

    IF c1%notfound THEN
        retval := 1;
    END IF;

    IF retval IS NULL THEN
        retval := 1;
    END IF;

    CLOSE c1;
    RETURN retval;

EXCEPTION
    WHEN OTHERS THEN
        raise_application_error(-20001,'An error was encountered - '||SQLCODE||' -ERROR- '||SQLERRM);
END;
/

create or replace FUNCTION SRM_OWNER.FILTER_CATEGORY_STATUS_TABLE (cat_id in integer, dest_id_csv in varchar2, sys_id_csv in varchar2, reg_id in integer, grp_id in integer, stat_id_csv in varchar2) RETURN CATEGORY_STATUS_TABLE AS
    return_table CATEGORY_STATUS_TABLE := CATEGORY_STATUS_TABLE();
    n integer := 0;
BEGIN
    for r in (select category_id from category where parent_id = cat_id)
        loop
            return_table.extend;
            n := n + 1;
            return_table(n) := CATEGORY_STATUS(r.category_id, filter_category_status(r.category_id, dest_id_csv, sys_id_csv, reg_id, grp_id, stat_id_csv));
        end loop;
    RETURN return_table;
END;
/

create or replace FUNCTION SRM_OWNER.FILTER_SYSTEM_STATUS_TABLE (dest_id_csv in varchar2, sys_id_csv in varchar2, reg_id in integer, grp_id in integer, stat_id_csv in varchar2) RETURN SYSTEM_STATUS_TABLE AS
    return_table SYSTEM_STATUS_TABLE := SYSTEM_STATUS_TABLE();
    n integer := 0;
BEGIN
    for r in (select system_id from system)
        loop
            return_table.extend;
            n := n + 1;
            return_table(n) := SYSTEM_STATUS(r.system_id, filter_system_status(r.system_id, dest_id_csv, sys_id_csv, reg_id, grp_id, stat_id_csv));
        end loop;
    RETURN return_table;
END;
/

-- Procedures

create or replace PROCEDURE SRM_OWNER.FILTER_CATEGORY_CHILD_TABLE (cat_id in integer, dest_id_csv in varchar2, sys_id_csv in varchar2, reg_id in integer, grp_id in integer, stat_id_csv in varchar2, rtn_cursor out SYS_REFCURSOR) AS
BEGIN
    delete from tmp_component_categories;
    FOR item IN
        (select distinct b.category_id from component a
                                                left join component_status_2 e on a.component_id = e.component_id
                                                left outer join component_beam_destination c on a.component_id = c.component_id,
                                            system b
         where a.system_id = b.system_id
           and a.system_id in (select system_id from system_application where application_id = 1)
           and (reg_id is null or reg_id = a.region_id)
           and (dest_id_csv is null or c.beam_destination_id in (select * from table(csv_2_nums(dest_id_csv))))
           and (sys_id_csv is null or a.system_id in (select * from table(csv_2_nums(sys_id_csv))))
           and (grp_id is null or
                a.component_id in (select component_id from component_signoff z where z.group_id = grp_id))
           and (stat_id_csv is null or
                e.status_id in (select * from table(csv_2_nums(stat_id_csv)))))
        LOOP
            --DBMS_OUTPUT.PUT_LINE('component category: ' || item.category_id);
            FOR thing IN
                (select w.category_id from category w start with w.category_id = item.category_id connect by prior w.parent_id = w.category_id)
                LOOP
                    --DBMS_OUTPUT.PUT_LINE('category heirarchy: ' || thing.category_id);
                    insert into tmp_component_categories values(thing.category_id);
                END LOOP;
        END LOOP;
    open rtn_cursor for select distinct category_id from tmp_component_categories intersect select category_id from category where parent_id = cat_id;
END;
/

--- CHANGE COMPONENT SYSTEM PROCEDURE
create or replace procedure SRM_OWNER.CHANGE_COMPONENT_SYSTEM (comp_id in integer, new_sys_id in integer) AS
    old_sys_id integer;
BEGIN
    select system_id into old_sys_id from component where component_id = comp_id;
    update component set system_id = new_sys_id where component_id = comp_id;
    for newGroup in
        (select group_id from group_responsibility where system_id = new_sys_id)
        loop
            --DBMS_OUTPUT.PUT_LINE('looking at new group: ' || newGroup.group_id);
            for oldGroup in
                (select group_id from group_responsibility where system_id = old_sys_id)
                loop
                    --DBMS_OUTPUT.PUT_LINE('looking at old group: ' || oldGroup.group_id);
                    if oldGroup.group_id = newGroup.group_id then
                        --DBMS_OUTPUT.PUT_LINE('found match');
                        update group_signoff set system_id = new_sys_id where component_id = comp_id and group_id = newGroup.group_id;
                        update group_signoff_history set system_id = new_sys_id where component_id = comp_id and group_id = newGroup.group_id;
                    end if;
                end loop;
        end loop;
    delete from group_signoff where component_id = comp_id and system_id = old_sys_id;
END;
/

-- CHANGE COMPONENT POWER PROCEDURE
create or replace procedure SRM_OWNER.CHANGE_COMPONENT_POWER (comp_id in integer, new_unpowered_yn in char) AS
    comp_system_id integer;
    old_status_id integer;
    new_change_type varchar2(24 char) := 'DOWNGRADE';
    new_staff_id integer := 233; -- Can't be null so... Ron Lazue!
    new_comment varchar(1024 char);
    formatted_yn varchar(3);
BEGIN
    select decode(new_unpowered_yn, 'Y', 'Yes', 'No') into formatted_yn from dual;
    new_comment := 'Unpowered Changed to "' || formatted_yn || '"';

    select system_id into comp_system_id from component where component_id = comp_id;

    update component set unpowered_yn = new_unpowered_yn where component_id = comp_id;

    for resp_group in
        (select group_id from group_responsibility where system_id = comp_system_id)
        loop
            --DBMS_OUTPUT.PUT_LINE('looking at group: ' || resp_group.group_id);

            select status_id into old_status_id from group_signoff where group_id = resp_group.group_id and component_id = comp_id;

            if old_status_id is null or old_status_id = 100 then
                new_change_type := 'COMMENT';
            end if;

            if old_status_id is not null then
                update group_signoff set status_id = 100, comments = new_comment, change_type = new_change_type, modified_date = sysdate, modified_by = new_staff_id where component_id = comp_id and group_id = resp_group.group_id;
            else
                insert into group_signoff
                (group_signoff_id, system_id, group_id, component_id, status_id, modified_by, modified_date, comments, change_type)
                values(group_signoff_id.nextval, comp_system_id, resp_group.group_id, comp_id, 100, new_staff_id, sysdate, new_comment, new_change_type);
            end if;

            insert into group_signoff_history
            (group_signoff_history_id, system_id, group_id, component_id, status_id, modified_by, modified_date, comments, change_type)
            values(group_signoff_history_id.nextval, comp_system_id, resp_group.group_id, comp_id, 100, new_staff_id, sysdate, new_comment, new_change_type);
        end loop;
END;
/
