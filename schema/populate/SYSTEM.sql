/**
* This function accepts a category name and returns its category_id
* It is intended to make population scripts easier and self-documenting.
*/
CREATE OR REPLACE FUNCTION SYSTEM_ID_FROM_NAME (p_name in VARCHAR)
   RETURN INTEGER
IS
    retval integer;
    
    CURSOR c1 IS
        SELECT system_id FROM system
             WHERE upper(name) = upper (p_name);
BEGIN
    OPEN c1;
    FETCH c1 INTO retval;
    
    IF c1%notfound THEN
          raise_application_error(-20001,'No such system name - '||p_name);
    END IF;
    
    CLOSE c1;
    RETURN retval;

EXCEPTION
WHEN OTHERS THEN
   raise_application_error(-20001,'An error was encountered - '||SQLCODE||' -ERROR- '||SQLERRM);
END;   
   
/

-- Magnets
insert into system values (system_id.nextval,'Correctors', category_id_from_name('Trim Supply'), 1000);
insert into system values (system_id.nextval,'Quads', category_id_from_name('Trim Supply'), 1000);

insert into system values (system_id.nextval, 'Dipoles', category_id_from_name('Box Supply'), 1000);


-- Diagnostics

insert into system values (system_id.nextval, 'Synchotron Light Monitors', category_id_from_name('Viewers'), 1000);
insert into system values (system_id.nextval, 'OTRs', category_id_from_name('Viewers'), 1000);
insert into system values (system_id.nextval, 'Standard Viewers', category_id_from_name('Viewers'), 1000);

insert into system values (system_id.nextval, 'nanoAmp', category_id_from_name('BPMs'), 1000);
insert into system values (system_id.nextval, 'SEE', category_id_from_name('BPMs'), 1000);
insert into system values (system_id.nextval, 'SPM', category_id_from_name('BPMs'), 1000);

insert into system values (system_id.nextval, 'Harps', category_id_from_name('Diagnostics'), 1000);
insert into system values (system_id.nextval, 'Fast Feedback', category_id_from_name('Diagnostics'), 1000);


insert into system values (system_id.nextval, 'BCMs', category_id_from_name('Current Monitors'), 1000);
insert into system values (system_id.nextval, 'F-Cups', category_id_from_name('Current Monitors'), 1000);
insert into system values (system_id.nextval, 'P-Cups', category_id_from_name('Current Monitors'), 1000);



-- Vacuum

insert into system values (system_id.nextval, 'Beam Valves (VBV)' , category_id_from_name('Beamine Vacuum'), 1000);
insert into system values  (system_id.nextval, 'Flow Valves (VFV)' , category_id_from_name('Beamine Vacuum'), 1000);
insert into system values  (system_id.nextval, 'Ion Pumps (VIP)' , category_id_from_name('Beamine Vacuum'), 1000);
insert into system values  (system_id.nextval, 'VDP' , category_id_from_name('Beamine Vacuum'), 1000);


insert into system values  (system_id.nextval, 'Insulating SRF' , category_id_from_name('SRF Vacuum'), 1000);
insert into system values  (system_id.nextval, 'Waveguide SRF' , category_id_from_name('SRF Vacuum'), 1000);



-- RF

insert into system values  (system_id.nextval, 'RF Zone' , category_id_from_name('Cold RF'), 1000);
insert into category values (system_id.nextval, , category_id_from_name('Warm RF'), 1000);
insert into category values (system_id.nextval, , category_id_from_name('MO'), 1000);

-- Is each Cryomodule supposed to be an RF Subsystem?
insert into system values  (system_id.nextval, '0L02' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '0L03' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '0L04' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L02' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L03' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L04' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L05' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L06' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L07' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L08' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L09' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L10' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L11' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L12' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L13' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L14' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L15' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L16' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L17' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L18' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L19' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L20' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L21' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L22' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L23' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L24' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L25' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '1L26' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L02' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L03' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L04' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L05' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L06' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L07' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L08' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L09' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L10' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L11' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L12' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L13' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L14' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L15' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L16' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L17' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L18' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L19' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L20' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L21' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L22' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L23' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L24' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L25' , category_id_from_name('Cold RF'), 1000);
insert into system values  (system_id.nextval, '2L26' , category_id_from_name('Cold RF'), 1000);



-- Safety Systems
insert into system values  (system_id.nextval, 'BLM' , category_id_from_name('MPS'), 1000);
insert into system values  (system_id.nextval, 'FSD' , category_id_from_name('MPS'), 1000);




-- Operations

insert into system values  (system_id.nextval, 'Radcon Checklists' , category_id_from_name('Documentation'), 1000);
insert into system values  (system_id.nextval, 'Startup Checklists' , category_id_from_name('Documentation'), 1000);


-- Facilities


-- Cryo
insert into system values  (system_id.nextval, 'CHL Plant' , category_id_from_name('CHL'), 1000);


-- Radiation Controls




-- Info Systems

insert into system values  (system_id.nextval,'Low Level Apps', category_id_from_name('Software'), 1000);
insert into system values  (system_id.nextval,'High Level Apps', category_id_from_name('Software'), 1000);

insert into system values  (system_id.nextval, 'File Servers', category_id_from_name('Servers/Network'), 1000);
insert into system values  (system_id.nextval, 'Networks Switches', category_id_from_name('Servers/Network'), 1000);
insert into system values  (system_id.nextval,  , category_id_from_name('UPS'), 1000);



-- Beam Dumps

