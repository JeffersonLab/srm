


/**
* This function accepts a category name and returns its category_id
* It is intended to make population scripts easier and self-documenting.
*/
CREATE OR REPLACE FUNCTION CATEGORY_ID_FROM_NAME (p_name in VARCHAR)
   RETURN INTEGER
IS
    retval integer;
    
    CURSOR c1 IS
        SELECT category_id FROM category
             WHERE upper(name) = upper (p_name);
BEGIN
    OPEN c1;
    FETCH c1 INTO retval;
    
    IF c1%notfound THEN
          raise_application_error(-20001,'No such category name - '||p_name);
    END IF;
    
    CLOSE c1;
    RETURN retval;

EXCEPTION
WHEN OTHERS THEN
   raise_application_error(-20001,'An error was encountered - '||SQLCODE||' -ERROR- '||SQLERRM);
END;   
   
/

-- Top Level Categories

insert into category values (category_id.nextval, 'CEBAF', NULL, 10);


-- Second Level Categories

insert into category values (category_id.nextval, 'RECO', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'Cryo', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'Facilities', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'Gun/Laser', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'Magnets', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'Operations', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'Radiation Controls', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'Vacuum', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'Safety System', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'Software', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'Beamline', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'Beam Dumps', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'Info Systems', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'Controls', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'Diagnostics', category_id_from_name('CEBAF'), 1000);
insert into category values (category_id.nextval, 'RF', category_id_from_name('CEBAF'), 1000);

-- Third Level Categories

insert into category values (category_id.nextval, 'Trim Supply', category_id_from_name('Magnets'), 1000);
insert into category values (category_id.nextval, 'Box Supply', category_id_from_name('Magnets'), 1000);

insert into category values (category_id.nextval, 'Viewers', category_id_from_name('Diagnostics'), 1000);
insert into category values (category_id.nextval, 'BPMs', category_id_from_name('Diagnostics'), 1000);
insert into category values (category_id.nextval, 'Harps', category_id_from_name('Diagnostics'), 1000);
insert into category values (category_id.nextval, 'Fast Feedback', category_id_from_name('Diagnostics'), 1000);
insert into category values (category_id.nextval, 'Current Monitors', category_id_from_name('Diagnostics'), 1000);


insert into category values (category_id.nextval, 'Beamine Vacuum', category_id_from_name('Vacuum'), 1000);
insert into category values (category_id.nextval, 'SRF Vacuum', category_id_from_name('Vacuum'), 1000);

insert into category values (category_id.nextval, 'Cold RF', category_id_from_name('RF'), 1000);
insert into category values (category_id.nextval, 'Warm RF', category_id_from_name('RF'), 1000);
insert into category values (category_id.nextval, 'MO', category_id_from_name('RF'), 1000);

insert into category values (category_id.nextval, 'PSS', category_id_from_name('Safety Systems'), 1000);
insert into category values (category_id.nextval, 'MPS', category_id_from_name('Safety Systems'), 1000);
insert into category values (category_id.nextval, 'BELS', category_id_from_name('Safety Systems'), 1000);


insert into category values (category_id.nextval, 'Staffing Requirements', category_id_from_name('Operations'), 1000);
insert into category values (category_id.nextval, 'Documentation', category_id_from_name('Operations'), 1000);
insert into category values (category_id.nextval, 'Alarms', category_id_from_name('Operations'), 1000);
insert into category values (category_id.nextval, 'Bypasses', category_id_from_name('Operations'), 1000);
insert into category values (category_id.nextval, 'Acceptance', category_id_from_name('Operations'), 1000);


insert into category values (category_id.nextval, 'Electrical Distribution', category_id_from_name('Facilities'), 1000);
insert into category values (category_id.nextval, 'Fire System', category_id_from_name('Facilities'), 1000);
insert into category values (category_id.nextval, 'HVAC', category_id_from_name('Facilities'), 1000);
insert into category values (category_id.nextval, 'LCW', category_id_from_name('Facilities'), 1000);
insert into category values (category_id.nextval, 'Compressed Air', category_id_from_name('Facilities'), 1000);
insert into category values (category_id.nextval, 'Power Monitoring', category_id_from_name('Facilities'), 1000);

insert into category values (category_id.nextval, 'CHL', category_id_from_name('Cryo'), 1000);

insert into category values (category_id.nextval, 'Survey Equipment', category_id_from_name('Radiation Controls'), 1000);

insert into category values (category_id.nextval, 'Software', category_id_from_name('Info Systems'), 1000);
insert into category values (category_id.nextval, 'Servers/Network', category_id_from_name('Info Systems'), 1000);
insert into category values (category_id.nextval, 'UPS', category_id_from_name('Info Systems'), 1000);







