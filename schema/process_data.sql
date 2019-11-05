CREATE OR REPLACE PROCEDURE PROCESS_CREDITS()
LANGUAGE plpgsql
AS $$
DECLARE
    data_cur CURSOR FOR SELECT DISSEMINATION_ID, ACTION, ORIGINAL_DISSEMINATION_ID
        FROM CREDITS
        WHERE (ACTION = 'CORRECT' OR ACTION = 'CANCEL') AND PROCESSED IS NULL;
    cur_row RECORD;
    updated INT;
BEGIN
    OPEN data_cur;
    LOOP
        FETCH data_cur INTO cur_row;
        EXIT WHEN NOT FOUND;
        raise notice 'Value: %', cur_row.dissemination_id;
        IF cur_row.action = 'CANCEL' THEN
            UPDATE CREDITS SET CANCELED_BY = cur_row.dissemination_id WHERE DISSEMINATION_ID = cur_row.original_dissemination_id;
        ELSE
            UPDATE CREDITS SET CORRECTED_BY = cur_row.dissemination_id WHERE DISSEMINATION_ID = cur_row.original_dissemination_id;
        END IF;
        GET DIAGNOSTICS updated = ROW_COUNT;
        IF updated = 1 THEN UPDATE CREDITS SET PROCESSED = TRUE WHERE DISSEMINATION_ID = cur_row.dissemination_id; END IF;
    END LOOP;
    CLOSE data_cur;
    COMMIT;
END;
$$;


CREATE OR REPLACE PROCEDURE PROCESS_FOREX()
LANGUAGE plpgsql
AS $$
DECLARE
    data_cur CURSOR FOR SELECT DISSEMINATION_ID, ACTION, ORIGINAL_DISSEMINATION_ID
        FROM FOREX
        WHERE (ACTION = 'CORRECT' OR ACTION = 'CANCEL') AND PROCESSED IS NULL;
    cur_row RECORD;
    updated INT;
BEGIN
    OPEN data_cur;
    LOOP
        FETCH data_cur INTO cur_row;
        EXIT WHEN NOT FOUND;
        raise notice 'Value: %', cur_row.dissemination_id;
        IF cur_row.action = 'CANCEL' THEN
            UPDATE FOREX SET CANCELED_BY = cur_row.dissemination_id WHERE DISSEMINATION_ID = cur_row.original_dissemination_id;
        ELSE
            UPDATE FOREX SET CORRECTED_BY = cur_row.dissemination_id WHERE DISSEMINATION_ID = cur_row.original_dissemination_id;
        END IF;
        GET DIAGNOSTICS updated = ROW_COUNT;
        IF updated = 1 THEN UPDATE FOREX SET PROCESSED = TRUE WHERE DISSEMINATION_ID = cur_row.dissemination_id; END IF;
    END LOOP;
    CLOSE data_cur;
    COMMIT;
END;
$$;



CREATE OR REPLACE PROCEDURE PROCESS_RATES()
LANGUAGE plpgsql
AS $$
DECLARE
    data_cur CURSOR FOR SELECT DISSEMINATION_ID, ACTION, ORIGINAL_DISSEMINATION_ID
        FROM RATES
        WHERE (ACTION = 'CORRECT' OR ACTION = 'CANCEL') AND PROCESSED IS NULL;
    cur_row RECORD;
    updated INT;
BEGIN
    OPEN data_cur;
    LOOP
        FETCH data_cur INTO cur_row;
        EXIT WHEN NOT FOUND;
        raise notice 'Value: %', cur_row.dissemination_id;
        IF cur_row.action = 'CANCEL' THEN
            UPDATE RATES SET CANCELED_BY = cur_row.dissemination_id WHERE DISSEMINATION_ID = cur_row.original_dissemination_id;
        ELSE
            UPDATE RATES SET CORRECTED_BY = cur_row.dissemination_id WHERE DISSEMINATION_ID = cur_row.original_dissemination_id;
        END IF;
        GET DIAGNOSTICS updated = ROW_COUNT;
        IF updated = 1 THEN UPDATE RATES SET PROCESSED = TRUE WHERE DISSEMINATION_ID = cur_row.dissemination_id; END IF;
    END LOOP;
    CLOSE data_cur;
    COMMIT;
END;
$$;



CREATE OR REPLACE PROCEDURE PROCESS_COMMODITIES()
LANGUAGE plpgsql
AS $$
DECLARE
    data_cur CURSOR FOR SELECT DISSEMINATION_ID, ACTION, ORIGINAL_DISSEMINATION_ID
        FROM COMMODITIES
        WHERE (ACTION = 'CORRECT' OR ACTION = 'CANCEL') AND PROCESSED IS NULL;
    cur_row RECORD;
    updated INT;
BEGIN
    OPEN data_cur;
    LOOP
        FETCH data_cur INTO cur_row;
        EXIT WHEN NOT FOUND;
        raise notice 'Value: %', cur_row.dissemination_id;
        IF cur_row.action = 'CANCEL' THEN
            UPDATE COMMODITIES SET CANCELED_BY = cur_row.dissemination_id WHERE DISSEMINATION_ID = cur_row.original_dissemination_id;
        ELSE
            UPDATE COMMODITIES SET CORRECTED_BY = cur_row.dissemination_id WHERE DISSEMINATION_ID = cur_row.original_dissemination_id;
        END IF;
        GET DIAGNOSTICS updated = ROW_COUNT;
        IF updated = 1 THEN UPDATE COMMODITIES SET PROCESSED = TRUE WHERE DISSEMINATION_ID = cur_row.dissemination_id; END IF;
    END LOOP;
    CLOSE data_cur;
    COMMIT;
END;
$$;



CREATE OR REPLACE PROCEDURE PROCESS_EQUITIES()
LANGUAGE plpgsql
AS $$
DECLARE
    data_cur CURSOR FOR SELECT DISSEMINATION_ID, ACTION, ORIGINAL_DISSEMINATION_ID
        FROM EQUITIES
        WHERE (ACTION = 'CORRECT' OR ACTION = 'CANCEL') AND PROCESSED IS NULL;
    cur_row RECORD;
    updated INT;
BEGIN
    OPEN data_cur;
    LOOP
        FETCH data_cur INTO cur_row;
        EXIT WHEN NOT FOUND;
        raise notice 'Value: %', cur_row.dissemination_id;
        IF cur_row.action = 'CANCEL' THEN
            UPDATE EQUITIES SET CANCELED_BY = cur_row.dissemination_id WHERE DISSEMINATION_ID = cur_row.original_dissemination_id;
        ELSE
            UPDATE EQUITIES SET CORRECTED_BY = cur_row.dissemination_id WHERE DISSEMINATION_ID = cur_row.original_dissemination_id;
        END IF;
        GET DIAGNOSTICS updated = ROW_COUNT;
        IF updated = 1 THEN UPDATE EQUITIES SET PROCESSED = TRUE WHERE DISSEMINATION_ID = cur_row.dissemination_id; END IF;
    END LOOP;
    CLOSE data_cur;
    COMMIT;
END;
$$;
