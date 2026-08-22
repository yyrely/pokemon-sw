ALTER TABLE sw_reservation
    ADD COLUMN balance_amount DECIMAL(12, 2) DEFAULT NULL COMMENT '尾款金额' AFTER reservation_amount;
