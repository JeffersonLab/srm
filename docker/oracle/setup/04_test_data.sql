alter session set container = XEPDB1;

-- Special characters such as the ampersand will result in prompt without this directive.
SET DEFINE OFF;

insert into SRM_OWNER.SETTINGS (SETTINGS_ID, GOAL_DATE, AUTO_EMAIL_YN, MASK_REQUEST_EMAIL_CSV, ACTIVITY_EMAIL_CSV) values (1, DATE '2013-11-22', 'N', 'tester@example.com', 'tester@example.com');