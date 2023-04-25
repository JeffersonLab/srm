alter session set container = XEPDB1;

insert into SRM_OWNER.HCO_SETTINGS (HCO_SETTINGS_ID, GOAL_DATE, AUTO_EMAIL_YN, FEEDBACK_EMAIL_CSV, MASK_REQUEST_EMAIL_CSV, ACTIVITY_EMAIL_CSV) values (1, DATE '2013-11-22', 'N', null, 'tester@example.com', 'tester@example.com');
