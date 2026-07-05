ALTER TABLE sw_user
    ADD COLUMN pokemon_collect_sync_status TINYINT NOT NULL DEFAULT 0 COMMENT '0 not synced, 1 synced',
    ADD COLUMN pokemon_collect_sync_time DATETIME NULL COMMENT 'last successful pokedex sync time',
    ADD COLUMN pokemon_collect_sync_error VARCHAR(512) NULL COMMENT 'last pokedex sync failure reason';

ALTER TABLE sw_user
    MODIFY COLUMN collect_mode VARCHAR(32) NOT NULL DEFAULT 'scale_world' COMMENT 'collect mode: pokedex or scale_world';

INSERT INTO sw_collectible_type (code, name, sort, enabled)
SELECT 'scale_world', '比例世界', 11, 1
WHERE NOT EXISTS (SELECT 1 FROM sw_collectible_type WHERE code = 'scale_world');

ALTER TABLE sw_user_pokemon_collect
    ADD COLUMN source_type VARCHAR(32) NULL COMMENT 'scale_item or scale_sub_item',
    ADD COLUMN source_collect_id BIGINT NULL COMMENT 'source collect record id',
    ADD COLUMN source_id BIGINT NULL COMMENT 'item_id or sub_item_id';

CREATE UNIQUE INDEX uk_user_pokemon_scale_source
    ON sw_user_pokemon_collect (user_id, source_type, source_collect_id, source_id);
