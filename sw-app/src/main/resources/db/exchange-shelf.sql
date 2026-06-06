ALTER TABLE sw_user
    ADD COLUMN contact VARCHAR(80) NULL COMMENT 'contact text';

ALTER TABLE sw_user_item_collect
    ADD COLUMN exchange_status TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'available for exchange';

ALTER TABLE sw_user_item_collect
    ADD COLUMN exchange_remark VARCHAR(255) NULL COMMENT 'exchange remark';

ALTER TABLE sw_user_sub_item_collect
    ADD COLUMN exchange_status TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'available for exchange';

ALTER TABLE sw_user_sub_item_collect
    ADD COLUMN exchange_remark VARCHAR(255) NULL COMMENT 'exchange remark';
